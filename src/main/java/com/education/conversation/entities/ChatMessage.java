package com.education.conversation.entities;

import com.education.conversation.dto.ChatRole;
import com.education.conversation.dto.MessageStatus;
import com.education.conversation.dto.MessageType;
import com.education.conversation.dto.openai.OpenAiChatCompletionResponse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRole role;
    private String content;
    private MessageType messageType;
    private String errorDetails;
    private BigDecimal inputToken;
    private BigDecimal outputToken;
    private MessageStatus status;
    private OffsetDateTime created;

    public static ChatMessage newUserMessage(String content) {
        return new ChatMessage()
                .setContent(content)
                .setMessageType(MessageType.TEXT)
                .setRole(ChatRole.USER)
                .setStatus(MessageStatus.NEW);
    }

    public static ChatMessage newAssistantMessage(OpenAiChatCompletionResponse response) {
        return new ChatMessage()
                .setContent(OpenAiChatCompletionResponse.getContent(response))
                .setMessageType(MessageType.TEXT)
                .setRole(ChatRole.ASSISTANT)
                .setInputToken(BigDecimal.valueOf(response.getUsage().getPromptTokens()))
                .setOutputToken(BigDecimal.valueOf(response.getUsage().getCompletionTokens()));
    }
}
