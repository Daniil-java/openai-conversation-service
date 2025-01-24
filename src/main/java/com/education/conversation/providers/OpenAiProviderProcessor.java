package com.education.conversation.providers;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ProviderVariant;
import com.education.conversation.dto.openai.OpenAiChatCompletionRequest;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.integrations.OpenAiFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiProviderProcessor implements ProviderProcessor {

    private final OpenAiFeignClient openAiFeignClient;
    private final String aiKey;

    public OpenAiProviderProcessor(@Value("${GENERATION_TOKEN}") String aiKey, OpenAiFeignClient openAiFeignClient) {
        this.aiKey = aiKey;
        this.openAiFeignClient = openAiFeignClient;
    }


    @Override
    public AiResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList) {
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.makeRequest(
                chatMessageList, userMessage.getModel(), userMessage.getTemperature());

        OpenAiChatCompletionResponse response =
                openAiFeignClient.generate("Bearer " + aiKey, request);

        return response.toAiResponse(userMessage.getModel());
    }

    @Override
    public ProviderVariant getProviderName() {
        return ProviderVariant.OPENAI;
    }
}
