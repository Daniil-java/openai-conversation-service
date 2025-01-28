package com.education.conversation.services;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.dto.enums.MessageStatus;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.entities.Conversation;
import com.education.conversation.entities.Model;
import com.education.conversation.entities.User;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.providers.ProviderProcessorHandler;
import com.education.conversation.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ExecutorService executorService;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final ProviderProcessorHandler providerProcessorHandler;
    private final UserService userService;

    public MessageResponseDto handleTextMessage(MessageRequestDto messageRequestDto) {
        //Конвертация данных для выдачи результата контроллеру
        return MessageResponseDto.makeMessageResponseDto(
                processUserMessage(messageRequestDto));
    }

    private ChatMessage processUserMessage(MessageRequestDto messageRequestDto) {
        //Конвертация дто в сущность
        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        //Получение контекста беседы
        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        User user = userService.findByIdOrThrowError(userMessage.getConversation().getId());

        if (user.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorResponseException(ErrorStatus.USER_INSUFFICIENT_FUNDS);
        }

        try {
            //Получение провайдера по параметру приходящего запроса и исполнение запроса
            AiResponse response = providerProcessorHandler.getProvider(userMessage.getModel().getProvider())
                    .fetchResponse(userMessage, chatMessageList);

            //Конвертация ответа в сущность
            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(
                    response,
                    userMessage,
                    messageRequestDto.getTemperature()
            );

            userMessage.setStatus(MessageStatus.DONE);
            //Вычитание токенов с баланса пользователя
            user.setBalance(user.getBalance()
                    .subtract(assistantMessage.getInputToken()
                            .add(assistantMessage.getOutputToken()))
            );
            chatMessageRepository.save(userMessage);

            assistantMessage.setStatus(MessageStatus.DONE);
            return chatMessageRepository.save(assistantMessage);
        } catch (Exception e) {
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, e.getMessage());
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }
    }

    private List<ChatMessage> processUserMessageWithResponses(MessageRequestDto messageRequestDto) {
        int timeoutSeconds = 5;
        int requestCount = 5;

        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        List<ChatMessage> conversationMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        User user = userService.findByIdOrThrowError(userMessage.getConversation().getId());

        if (user.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorResponseException(ErrorStatus.USER_INSUFFICIENT_FUNDS);
        }

        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        //Concurrency
        List<Callable<AiResponse>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            tasks.add(() -> providerProcessorHandler.getProvider(userMessage.getModel().getProvider())
                    .fetchResponse(userMessage, conversationMessageList));
        }

        try {
            // Отправляем все задачи и получаем список Future
            List<Future<AiResponse>> futures = executorService.invokeAll(tasks);
            try {
                for (Future<AiResponse> future : futures) {
                    ChatMessage assistantMessage = ChatMessage.newAssistantMessage(
                            future.get(timeoutSeconds, TimeUnit.SECONDS),
                            userMessage,
                            messageRequestDto.getTemperature()
                    );

                    chatMessages.add(chatMessageRepository.save(assistantMessage));

                    user.setBalance(user.getBalance().subtract(
                            assistantMessage.getOutputToken().add(assistantMessage.getInputToken()))
                    );
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Concurrency error!", e);
            }

            userMessage.setStatus(MessageStatus.DONE);
            chatMessageRepository.save(userMessage);
            userService.save(user);

        } catch (InterruptedException e) {
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, e.getMessage());
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }

        return chatMessages;
    }

    private ChatMessage makeUserMessage(MessageRequestDto messageRequestDto) {
        Conversation conversation = conversationService
                .getByIdOrThrowException(messageRequestDto.getConversationId());

        Model model = modelService.findModelOrThrowError(messageRequestDto.getModel());

        if (conversation.getName() == null) {
            conversation = conversationService.setNameForConversation(conversation, messageRequestDto.getContent());
        }

        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto, conversation, model);

        return chatMessageRepository.save(userMessage);
    }

    private void setStatusAndErrorDetails(ChatMessage userMessage, MessageStatus messageStatus, String errorStatus) {
        userMessage.setStatus(messageStatus);
        userMessage.setErrorDetails(errorStatus);
        chatMessageRepository.save(userMessage);
    }

    public List<MessageResponseDto> handleTextMessageForManyResponses(MessageRequestDto messageRequestDto) {
        return processUserMessageWithResponses(messageRequestDto)
                .stream()
                .map(MessageResponseDto::makeMessageResponseDto)
                .toList();
    }



}
