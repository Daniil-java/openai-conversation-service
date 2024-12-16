package com.education.conversation.controllers;

import com.education.conversation.dto.MessageRequestDto;
import com.education.conversation.dto.MessageResponseDto;
import com.education.conversation.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/text")
    public MessageResponseDto sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessage(messageRequestDto);
    }

    @PostMapping("/concurrency/text")
    public List<MessageResponseDto> sendMessageForManyResponses(@RequestBody MessageRequestDto messageRequestDto) {
        return chatMessageService.handleTextMessageForManyResponses(messageRequestDto);
    }
}
