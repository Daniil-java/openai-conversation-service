package com.education.conversation.dto.gemini;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ProviderVariant;
import lombok.Data;
import lombok.experimental.Accessors;

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

    public AiResponse toAiResponse() {
        return new AiResponse()
                .setContent(candidates.get(0).getContent().toString())
                .setPromptTokens(usageMetadata.getPromptTokenCount())
                .setCompletionTokens(usageMetadata.getCandidatesTokenCount())
                .setTotalTokens(usageMetadata.getTotalTokenCount());
    }
}
