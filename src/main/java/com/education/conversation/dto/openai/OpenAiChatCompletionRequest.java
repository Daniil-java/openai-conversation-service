package com.education.conversation.dto.openai;

import com.education.conversation.dto.enums.ChatModel;
import com.education.conversation.entities.ChatMessage;
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
            List<ChatMessage> chatMessageList, ChatModel chatModel, Float temperature) {

        if (temperature == null) temperature = OpenAiChatCompletionRequest.TEMPERATURE_DEFAULT;
        return new OpenAiChatCompletionRequest()
                        .setTemperature(temperature)
                        .setModel(chatModel.getModel())
                        .setMessages(Message.convertFromChatMessages(chatMessageList));
    }
}
