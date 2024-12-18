package com.education.conversation.dto.openai;

import com.education.conversation.dto.ChatModel;
import com.education.conversation.dto.ChatRole;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OpenAiChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private float temperature;

    public static final float TEMPERATURE_DEFAULT = 0.1f;

    public static OpenAiChatCompletionRequest makeRequest(String request, ChatModel chatModel) {
        return makeRequest(request, chatModel, TEMPERATURE_DEFAULT);
    }

    public static OpenAiChatCompletionRequest makeRequest(String request, ChatModel chatModel, Float temperature) {
        //Хардкод. Меня устраивает в моём pet проекте. В промешленной экспл. вынесу в конфиг
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new Message()
                .setRole(ChatRole.USER.getValue())
                .setContent(request)
        );

        return new OpenAiChatCompletionRequest()
                .setModel(chatModel.getModel())
                .setMessages(messages)
                .setTemperature(temperature);
    }
}
