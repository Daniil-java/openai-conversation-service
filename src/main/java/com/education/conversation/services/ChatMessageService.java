package com.education.conversation.services;

import com.education.conversation.dto.*;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAiService openAiService;
    private final ExecutorService executorService;

    public MessageResponseDto handleTextMessage(MessageRequestDto messageRequestDto) {
        return MessageResponseDto.makeMessageResponseDto(
                processUserMessage(createAndSaveNewUserMessage(messageRequestDto.getContent()))
        );
    }

    private ChatMessage processUserMessage(ChatMessage userMessage) {
        try {
            OpenAiChatCompletionResponse response = openAiService.fetchResponse(userMessage.getContent());
            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(response);
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

    private ChatMessage createAndSaveNewUserMessage(String content) {
        return chatMessageRepository.save(ChatMessage.newUserMessage(content));
    }

    public List<MessageResponseDto> handleTextMessageForManyResponses(MessageRequestDto messageRequestDto) {
        return processUserMessageWithResponses(createAndSaveNewUserMessage(messageRequestDto.getContent()))
                .stream()
                .map(MessageResponseDto::makeMessageResponseDto)
                .collect(Collectors.toList());
    }


    private List<ChatMessage> processUserMessageWithResponses(ChatMessage userMessage) {
        int timeoutSeconds = 5;
        int requestCount = 5;

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        //Concurrency
        List<Callable<OpenAiChatCompletionResponse>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            tasks.add(() -> openAiService.fetchResponse(userMessage.getContent()));
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
