package com.education.conversation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AiResponse {
    private String content;
    private BigDecimal promptTokens;
    private BigDecimal completionTokens;
    private BigDecimal totalTokens;
}
