package com.education.conversation.dto.gemini;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.entities.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class GeminiResponse {
    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;

    @Data
    @Accessors(chain = true)
    public static class Candidate {
        private Content content;
    }

    @Data
    @Accessors(chain = true)
    public static class UsageMetadata {
        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;
    }

    public AiResponse toAiResponse(Model model) {
        BigDecimal input = model.getInputMultiplier()
                .multiply(BigDecimal.valueOf(usageMetadata.getPromptTokenCount()));
        BigDecimal output = model.getOutputMultiplier()
                .multiply(BigDecimal.valueOf(usageMetadata.getCandidatesTokenCount()));

        return new AiResponse()
                .setContent(candidates.get(0).getContent().toString())
                .setPromptTokens(input)
                .setCompletionTokens(output)
                .setTotalTokens(input.add(output));
    }
}
