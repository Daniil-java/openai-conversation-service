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

    @PostMapping("/messages/{providerVariant}")
    public MessageResponseDto sendMessage(@PathVariable ProviderVariant providerVariant,
                                          @RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessage(providerVariant, messageRequestDto);
    }

    @PostMapping("/messages/{providerVariant}/concurrent")
    public List<MessageResponseDto> sendMessageForManyResponses(@PathVariable ProviderVariant providerVariant,
                                                                @RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessageForManyResponses(providerVariant, messageRequestDto);
    }
}
