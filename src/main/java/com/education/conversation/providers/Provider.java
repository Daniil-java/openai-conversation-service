package com.education.conversation.providers;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ProviderVariant;
import com.education.conversation.entities.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface Provider {

    AiResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList);

    ProviderVariant getProviderName();

    @Autowired
    default void registerMyself(ProviderHandler providerHandler) {
        providerHandler.register(getProviderName(), this);
    }
}
