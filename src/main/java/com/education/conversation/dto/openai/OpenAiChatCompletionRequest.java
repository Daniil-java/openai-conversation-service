package com.education.conversation.dto.openai;

import com.education.conversation.entities.ChatMessage;
import com.education.conversation.entities.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OpenAiChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private float temperature;

    public static final float TEMPERATURE_DEFAULT = 0.1f;

    public static OpenAiChatCompletionRequest makeRequest(
            List<ChatMessage> chatMessageList, Model model, Float temperature) {

        if (temperature == null) temperature = OpenAiChatCompletionRequest.TEMPERATURE_DEFAULT;
        return new OpenAiChatCompletionRequest()
                        .setTemperature(temperature)
                        .setModel(model.getModelName())
                        .setMessages(Message.convertFromChatMessages(chatMessageList));
    }
}
