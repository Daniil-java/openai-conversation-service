package com.education.conversation.dto.models.gemini;

import com.education.conversation.dto.models.BaseResponse;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GeminiResponse extends BaseResponse {
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

    @Override
    protected String getContent() {
        return candidates.get(0).getContent().toString();
    }

    @Override
    protected int getPromptTokenCount() {
        return usageMetadata.getPromptTokenCount();
    }

    @Override
    protected int getCompletionTokenCount() {
        return usageMetadata.getCandidatesTokenCount();
    }
}
