package com.education.conversation.dto.openai;

import com.education.conversation.entities.ChatMessage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Message {
    private String role;
    private String content;

    public static List<Message> convertFromChatMessages(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> new Message()
                        .setRole(chatMessage.getRole().getValue())
                        .setContent(chatMessage.getContent()))
                .toList();
    }

}
