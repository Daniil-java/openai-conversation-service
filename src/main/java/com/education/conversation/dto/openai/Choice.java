package com.education.conversation.dto.openai;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Choice {
    private int index;
    private Message message;
    private String finishReason;
}
