package com.education.conversation.services;

import com.education.conversation.dto.ChatModel;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.dto.message.MessageStatus;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.entities.Conversation;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAiService openAiService;
    private final ExecutorService executorService;
    private final ConversationService conversationService;

    public MessageResponseDto handleTextMessage(MessageRequestDto messageRequestDto) {
        return MessageResponseDto.makeMessageResponseDto(
                processUserMessage(messageRequestDto));
    }

    private ChatMessage processUserMessage(MessageRequestDto messageRequestDto) {
        if (ChatModel.getEnumOrNull(messageRequestDto.getModel().name()) == null ) {
            throw new ErrorResponseException(ErrorStatus.MODEL_NOT_SUPPORTED);
        }

        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        try {
            OpenAiChatCompletionResponse response = openAiService
                    .fetchResponse(userMessage, chatMessageList, messageRequestDto.getTemperature());

            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(response, userMessage.getConversation());
            userMessage.setStatus(MessageStatus.DONE);

            chatMessageRepository.save(userMessage);
            return chatMessageRepository.save(assistantMessage);
        } catch (Exception e) {
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, ErrorStatus.OPENAI_CONNECTION_ERROR);
            throw new ErrorResponseException(ErrorStatus.OPENAI_CONNECTION_ERROR);
        }
    }

    private List<ChatMessage> processUserMessageWithResponses(MessageRequestDto messageRequestDto) {
        int timeoutSeconds = 5;
        int requestCount = 5;

        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        List<ChatMessage> conversationMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        //Concurrency
        List<Callable<OpenAiChatCompletionResponse>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            tasks.add(() -> openAiService.fetchResponse(
                    userMessage, conversationMessageList, messageRequestDto.getTemperature()));
        }

        try {
            // Отправляем все задачи и получаем список Future
            List<Future<OpenAiChatCompletionResponse>> futures = executorService.invokeAll(tasks);
            try {
                for (Future<OpenAiChatCompletionResponse> future : futures) {
                    ChatMessage assistantMessage = ChatMessage.newAssistantMessage(
                            future.get(timeoutSeconds, TimeUnit.SECONDS), userMessage.getConversation());

                    chatMessages.add(chatMessageRepository.save(assistantMessage));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Concurrency error!", e);
            }

            userMessage.setStatus(MessageStatus.DONE);
            chatMessageRepository.save(userMessage);

        } catch (InterruptedException e) {
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, ErrorStatus.OPENAI_CONNECTION_ERROR);
            throw new ErrorResponseException(ErrorStatus.OPENAI_CONNECTION_ERROR);
        }

        return chatMessages;
    }

    private ChatMessage makeUserMessage(MessageRequestDto messageRequestDto) {
        Conversation conversation = conversationService
                .getByIdOrThrowException(messageRequestDto.getConversationId());

        if (conversation.getName() == null) {
            conversation = conversationService.setNameForConversation(conversation, messageRequestDto.getContent());
        }

        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto);
        userMessage.setConversation(conversation);

        return chatMessageRepository.save(userMessage);
    }

    private void setStatusAndErrorDetails(ChatMessage userMessage, MessageStatus messageStatus, ErrorStatus errorStatus) {
        userMessage.setStatus(messageStatus);
        userMessage.setErrorDetails(errorStatus.getMessage());
        chatMessageRepository.save(userMessage);
    }

    public List<MessageResponseDto> handleTextMessageForManyResponses(MessageRequestDto messageRequestDto) {
        return processUserMessageWithResponses(messageRequestDto)
                .stream()
                .map(MessageResponseDto::makeMessageResponseDto)
                .toList();
    }

}
