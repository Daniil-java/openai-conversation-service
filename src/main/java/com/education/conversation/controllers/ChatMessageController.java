package com.education.conversation.controllers;

import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.message.MessageResponseDto;
import com.education.conversation.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/messages")
    public MessageResponseDto sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessage(messageRequestDto);
    }

    @PostMapping("/messages/concurrent")
    public List<MessageResponseDto> sendMessageForManyResponses(@RequestBody MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessageForManyResponses(messageRequestDto);
    }
}
