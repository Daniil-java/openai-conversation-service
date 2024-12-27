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

    public static GeminiRequest makeUserRequest(List<ChatMessage> chatMessages) {
        List<Content> contentList = new ArrayList<>();

        for (ChatMessage message: chatMessages) {
            ChatRole role = message.getRole() == ChatRole.ASSISTANT ? ChatRole.MODEL : ChatRole.USER;
            contentList.add(createContent(role, message.getContent()));
        }

        return new GeminiRequest().setContents(contentList);
    }

    private static Content createContent(ChatRole role, String text) {
        return new Content()
                .setRole(role.getValue())
                .setParts(List.of(new Content.Part().setText(text)));
    }

}
