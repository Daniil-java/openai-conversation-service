package com.education.conversation.services;

import com.education.conversation.dto.*;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAiService openAiService;

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

}
