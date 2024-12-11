package com.education.conversation.services;

import com.education.conversation.dto.ChatRole;
import com.education.conversation.dto.MessageRequestDto;
import com.education.conversation.dto.MessageResponseDto;
import com.education.conversation.dto.MessageType;
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
        ChatMessage chatMessage = buildUserChatMessage(messageRequestDto.getContent(), ChatRole.USER);
        try {
            String response = openAiService.fetchResponse(messageRequestDto.getContent());
            return MessageResponseDto.makeMessageResponseDto(chatMessageRepository.save(
                    buildUserChatMessage(response, ChatRole.ASSISTANT)));
        } catch (Exception e) {
            return MessageResponseDto.makeMessageResponseDto(
                    chatMessageRepository.save(
                            chatMessage.setErrorDetails(ErrorStatus.OPENAI_CONNECTION_ERROR.getMessage()))
            );
        }
    }

    private ChatMessage buildUserChatMessage(String content, ChatRole chatRole) {
        return new ChatMessage()
                .setContent(content)
                .setMessageType(MessageType.TEXT)
                .setRole(chatRole);
    }
}
