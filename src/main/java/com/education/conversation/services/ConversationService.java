package com.education.conversation.services;

import com.education.conversation.dto.ConversationDto;
import com.education.conversation.entities.Conversation;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationDto createByDto(ConversationDto conversationDto) {
        return Conversation.entityToDto(create(conversationDto));
    }

    public Conversation create(ConversationDto conversationDto) {
        return conversationRepository.save(Conversation.dtoToEntity(conversationDto));
    }

    public Conversation getById(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.CONVERSATION_NOT_FOUND));
    }
}
