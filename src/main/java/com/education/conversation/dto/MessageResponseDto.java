package com.education.conversation.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MessageResponseDto {
    private ChatRole role;
    private String content;
    private String errorDetails;
    private BigDecimal inputToken;
    private BigDecimal outputToken;
}
