package com.education.conversation.dto.openai;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.entities.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
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

    public AiResponse toAiResponse(Model model) {
        BigDecimal input = model.getInputMultiplier()
                .multiply(BigDecimal.valueOf(usage.getPromptTokens()));
        BigDecimal output = model.getOutputMultiplier()
                .multiply(BigDecimal.valueOf(usage.getCompletionTokens()));

        return new AiResponse()
                .setContent(getContent())
                .setPromptTokens(input)
                .setCompletionTokens(output)
                .setTotalTokens(input.add(output));
    }

}
