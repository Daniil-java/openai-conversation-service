package com.education.conversation.services;

import com.education.conversation.dto.openai.OpenAiChatCompletionRequest;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import com.education.conversation.integrations.OpenAiFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final OpenAiFeignClient openAiFeignClient;
    private final String aiKey;

    public OpenAiService(@Value("${GENERATION_TOKEN}") String aiKey, OpenAiFeignClient openAiFeignClient) {
        this.aiKey = aiKey;
        this.openAiFeignClient = openAiFeignClient;
    }

    //Отправка запроса и получение ответа OpenAI
    public OpenAiChatCompletionResponse fetchResponse(String request) {
        return openAiFeignClient.generate(
                "Bearer " + aiKey,
                OpenAiChatCompletionRequest.makeRequest(request)
        );
    }

    // Читаем JSON в указанный тип T
    private<T> T fetchResponseAndReadJson(String request, Class<T> responseType) throws JsonProcessingException {
        return new ObjectMapper().readValue(getContent(fetchResponse(request)), responseType);
    }


    //Получения содержания сообщения
    public static String getContent(OpenAiChatCompletionResponse response) {
        return OpenAiChatCompletionResponse.getContent(response);
    }


}
