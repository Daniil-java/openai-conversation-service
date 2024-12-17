package com.education.conversation.controllers;

import com.education.conversation.dto.ConversationDto;
import com.education.conversation.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/text")
    public ConversationDto createNewConversation(@RequestBody ConversationDto conversationDto) {
        return conversationService.createByDto(conversationDto);
    }
}
