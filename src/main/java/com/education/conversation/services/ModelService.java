package com.education.conversation.services;

import com.education.conversation.dto.enums.ChatModel;
import com.education.conversation.entities.Model;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.repositories.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {
    private final ModelRepository modelRepository;

    public Model findModelOrThrowError(ChatModel chatModel) {
        return modelRepository.findByModelName(chatModel.getModel())
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.PROVIDER_NOT_FOUND));
    }
}
