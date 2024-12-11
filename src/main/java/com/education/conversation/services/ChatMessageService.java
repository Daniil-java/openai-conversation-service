package com.education.conversation.services;

import com.education.conversation.dto.ChatRole;
import com.education.conversation.dto.MessageRequestDto;
import com.education.conversation.dto.MessageResponseDto;
import com.education.conversation.dto.MessageType;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAiService openAiService;

    public MessageResponseDto handleTextMessage(MessageRequestDto messageRequestDto) {
        String response = openAiService.fetchResponse(messageRequestDto.getContent());

        chatMessageRepository.save(new ChatMessage()
                .setContent(messageRequestDto.getContent())
                .setRole(ChatRole.USER)
                .setMessageType(MessageType.TEXT)
        );

        return MessageResponseDto.makeMessageResponseDto(
                chatMessageRepository.save(new ChatMessage()
                        .setMessageType(MessageType.TEXT)
                        .setContent(response)
                        .setRole(ChatRole.ASSISTANT)
                ));
    }
}
