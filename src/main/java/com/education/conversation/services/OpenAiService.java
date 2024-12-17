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

    public OpenAiChatCompletionResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList) {
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest
                .makeRequest(userMessage.getContent(),
                        userMessage.getConversation().getModel(),
                        userMessage.getConversation().getTemperature());
        request.setMessages(Message.convertFromChatMessages(chatMessageList));

        return openAiFeignClient.generate("Bearer " + aiKey, request);
    }

    //Отправка запроса и получение ответа OpenAI
    public OpenAiChatCompletionResponse fetchResponse(String request, ChatModel chatModel, Float temperature) {
        return openAiFeignClient.generate(
                "Bearer " + aiKey,
                OpenAiChatCompletionRequest.makeRequest(request, chatModel, temperature)
        );
    }

    public OpenAiChatCompletionResponse fetchResponseWithConversation(
            String request, ChatModel chatModel, Float temperature) {
        return openAiFeignClient.generate(
                "Bearer " + aiKey,
                OpenAiChatCompletionRequest.makeRequest(request, chatModel, temperature)
        );
    }

    // Читаем JSON в указанный тип T
    private<T> T fetchResponseAndReadJson(String request, ChatModel chatModel,
                                          Float temperature, Class<T> responseType) throws JsonProcessingException {
        return new ObjectMapper().readValue(getContent(fetchResponse(request, chatModel, temperature)), responseType);
    }


    //Получения содержания сообщения
    public static String getContent(OpenAiChatCompletionResponse response) {
        return OpenAiChatCompletionResponse.getContent(response);
    }
}
