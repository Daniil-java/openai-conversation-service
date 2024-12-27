package com.education.conversation.controllers;

import com.education.conversation.dto.enums.ProviderVariant;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/messages/")
    public MessageResponseDto sendMessage(@RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessage(messageRequestDto);
    }

    @PostMapping("/messages/concurrent")
    public List<MessageResponseDto> sendMessageForManyResponses(@RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessageForManyResponses(messageRequestDto);
    }
}
