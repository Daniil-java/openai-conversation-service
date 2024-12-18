package com.education.conversation.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageRequestDto {
    @NotNull(message = "У сообщения должно быть содержание")
    private String content;
    @NotNull
    private Long conversationId;
    private Float temperature;
}
