package com.education.conversation.dto.openai;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ProviderVariant;
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

    public String getContent() {
        return choices.get(0).getMessage().getContent();
    }

    public AiResponse toAiResponse() {
        return new AiResponse()
                .setContent(getContent())
                .setPromptTokens(usage.getPromptTokens())
                .setCompletionTokens(usage.getCompletionTokens())
                .setTotalTokens(usage.getTotalTokens());
    }
}
