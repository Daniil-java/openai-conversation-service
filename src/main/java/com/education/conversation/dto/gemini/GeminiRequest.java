package com.education.conversation.dto.gemini;

import com.education.conversation.dto.enums.ChatRole;
import com.education.conversation.entities.ChatMessage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class GeminiRequest {
    private List<Content> contents;

    public static GeminiRequest makeUserRequest(ChatMessage chatMessage, List<ChatMessage> chatMessages) {
        List<Content> contentList = new ArrayList<>();
        for (ChatMessage message: chatMessages) {
            contentList.add(new Content()
                    .setRole(ChatRole.USER.getValue())
                    .setParts(List.of(new Content.Part().setText(message.getContent()))));
        }

        return new GeminiRequest().setContents(contentList);
    }

}
