package com.education.conversation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AiResponse {
    private String content;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}
