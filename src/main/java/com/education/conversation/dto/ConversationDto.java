package com.education.conversation.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationDto {
    private Long id;
    private String name;
}
