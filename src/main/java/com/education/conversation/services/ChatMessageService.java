package com.education.conversation.services;

import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.dto.message.MessageStatus;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
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
        ChatMessage userMessage = createAndSaveNewUserMessage(messageRequestDto);
        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationDto().getId());

        try {
            OpenAiChatCompletionResponse response = openAiService
                    .fetchResponse(userMessage, chatMessageList);

            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(response);
            assistantMessage.setConversation(userMessage.getConversation());
            userMessage.setStatus(MessageStatus.DONE);

            chatMessageRepository.save(userMessage);
            return chatMessageRepository.save(assistantMessage);
        } catch (Exception e) {
            userMessage.setStatus(MessageStatus.ERROR);
            userMessage.setErrorDetails(ErrorStatus.OPENAI_CONNECTION_ERROR.getMessage());

            chatMessageRepository.save(userMessage);
            throw new ErrorResponseException(ErrorStatus.OPENAI_CONNECTION_ERROR);
        }
    }


    private ChatMessage createAndSaveNewUserMessage(MessageRequestDto messageRequestDto) {
        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto.getContent());
        userMessage.setConversation(conversationService.getOrCreateByDto(messageRequestDto.getConversationDto()));

        return chatMessageRepository.save(userMessage);
    }

    public List<MessageResponseDto> handleTextMessageForManyResponses(MessageRequestDto messageRequestDto) {
        return processUserMessageWithResponses(messageRequestDto)
                .stream()
                .map(MessageResponseDto::makeMessageResponseDto)
                .toList();
    }


    private List<ChatMessage> processUserMessageWithResponses(MessageRequestDto messageRequestDto) {
        int timeoutSeconds = 5;
        int requestCount = 5;

        ChatMessage userMessage = createAndSaveNewUserMessage(messageRequestDto);
        List<ChatMessage> conversationMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationDto().getId());

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        //Concurrency
        List<Callable<OpenAiChatCompletionResponse>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            tasks.add(() -> openAiService.fetchResponse(userMessage, conversationMessageList));
        }

        try {
            // Отправляем все задачи и получаем список Future
            List<Future<OpenAiChatCompletionResponse>> futures = executorService.invokeAll(tasks);

            try {
                for (Future<OpenAiChatCompletionResponse> future : futures) {
                    ChatMessage assistantMessage =
                            ChatMessage.newAssistantMessage(future.get(timeoutSeconds, TimeUnit.SECONDS));

                    chatMessages.add(chatMessageRepository.save(assistantMessage));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Concurrency error!", e);
            }

            userMessage.setStatus(MessageStatus.DONE);
            chatMessageRepository.save(userMessage);

        } catch (InterruptedException e) {
            userMessage.setStatus(MessageStatus.ERROR);
            userMessage.setErrorDetails(ErrorStatus.OPENAI_CONNECTION_ERROR.getMessage());
            chatMessageRepository.save(userMessage);
            throw new ErrorResponseException(ErrorStatus.OPENAI_CONNECTION_ERROR);
        }

        return chatMessages;
    }

}
