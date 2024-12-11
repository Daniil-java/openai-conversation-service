package com.education.conversation.dto.openai;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class OpenAiChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint;

    public static String getContent(OpenAiChatCompletionResponse response) {
        return response.getChoices().get(0).getMessage().getContent();
    }
}
