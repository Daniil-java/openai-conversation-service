package com.education.conversation.dto.models;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.entities.Model;

import java.math.BigDecimal;

public abstract class BaseResponse {
    protected abstract String getContent();
    protected abstract int getPromptTokenCount();
    protected abstract int getCompletionTokenCount();

    public AiResponse toAiResponse(Model model) {
        BigDecimal input = model.getInputMultiplier()
                .multiply(BigDecimal.valueOf(getPromptTokenCount()));
        BigDecimal output = model.getOutputMultiplier()
                .multiply(BigDecimal.valueOf(getCompletionTokenCount()));

        return new AiResponse()
                .setContent(getContent())
                .setPromptTokens(input)
                .setCompletionTokens(output)
                .setTotalTokens(input.add(output));
    }
}
