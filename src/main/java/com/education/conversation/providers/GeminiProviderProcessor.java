package com.education.conversation.providers;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ProviderVariant;
import com.education.conversation.dto.models.gemini.GeminiRequest;
import com.education.conversation.dto.models.gemini.GeminiResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.integrations.GeminiFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiProviderProcessor implements ProviderProcessor {
    private final GeminiFeignClient geminiFeignClient;
    private final String aiKey;

    public GeminiProviderProcessor(@Value("${GEMINI_TOKEN}") String aiKey, GeminiFeignClient geminiFeignClient) {
        this.geminiFeignClient = geminiFeignClient;
        this.aiKey = aiKey;
    }

    @Override
    public AiResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList) {

        GeminiRequest request = GeminiRequest.makeUserRequest(chatMessageList);

        GeminiResponse response = geminiFeignClient.generate(
                userMessage.getModel().getModelName(), aiKey, request);

        return response.toAiResponse(userMessage.getModel());
    }

    @Override
    public ProviderVariant getProviderName() {
        return ProviderVariant.GEMINI;
    }
}
