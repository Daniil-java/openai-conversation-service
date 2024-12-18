package com.education.conversation.services;

import com.education.conversation.dto.ChatModel;
import com.education.conversation.dto.openai.Message;
import com.education.conversation.dto.openai.OpenAiChatCompletionRequest;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.entities.ChatMessage;
import com.education.conversation.integrations.OpenAiFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        if (temperature == null) temperature = OpenAiChatCompletionRequest.TEMPERATURE_DEFAULT;

        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest
                .makeRequest(userMessage.getContent(),
                        userMessage.getConversation().getModel(),
                        temperature
                );

        request.setMessages(Message.convertFromChatMessages(chatMessageList));

        return openAiFeignClient.generate("Bearer " + aiKey, request);
    }

    //Отправка запроса и получение ответа OpenAI
    public OpenAiChatCompletionResponse fetchResponse(String request, ChatModel chatModel) {
        return openAiFeignClient.generate(
                "Bearer " + aiKey,
                OpenAiChatCompletionRequest.makeRequest(request, chatModel)
        );
    }

    //Получения содержания сообщения
    public static String getContent(OpenAiChatCompletionResponse response) {
        return OpenAiChatCompletionResponse.getContent(response);
    }
}
