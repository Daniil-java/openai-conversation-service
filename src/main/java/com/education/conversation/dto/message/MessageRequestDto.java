package com.education.conversation.dto.message;

import com.education.conversation.dto.ConversationDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageRequestDto {
    private String content;
    private ConversationDto conversationDto;
}
