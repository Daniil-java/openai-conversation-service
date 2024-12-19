package com.education.conversation.services;

import com.education.conversation.dto.openai.OpenAiChatCompletionRequest;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.integrations.OpenAiFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiService {

    private final OpenAiFeignClient openAiFeignClient;
    private final String aiKey;

    public OpenAiService(@Value("${GENERATION_TOKEN}") String aiKey, OpenAiFeignClient openAiFeignClient) {
        this.aiKey = aiKey;
        this.openAiFeignClient = openAiFeignClient;
    }

    public OpenAiChatCompletionResponse fetchResponse(
            ChatMessage userMessage, List<ChatMessage> chatMessageList, Float temperature) {

        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.makeRequest(
                chatMessageList, userMessage.getModel(), temperature);
        return openAiFeignClient.generate("Bearer " + aiKey, request);
    }
}
