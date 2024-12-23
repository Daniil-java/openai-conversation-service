package com.education.conversation.dto.message;

import com.education.conversation.dto.enums.ChatRole;
import com.education.conversation.entities.ChatMessage;
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

    public static MessageResponseDto makeMessageResponseDto(ChatMessage chatMessage) {
        if (chatMessage == null) return null;
        return new MessageResponseDto()
                .setRole(chatMessage.getRole())
                .setContent(chatMessage.getContent())
                .setErrorDetails(chatMessage.getErrorDetails())
                .setInputToken(chatMessage.getInputToken())
                .setOutputToken(chatMessage.getOutputToken());
    }
}
