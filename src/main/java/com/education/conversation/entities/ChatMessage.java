package com.education.conversation.entities;

import com.education.conversation.dto.AiResponse;
import com.education.conversation.dto.enums.ChatModel;
import com.education.conversation.dto.enums.ChatRole;
import com.education.conversation.dto.message.MessageRequestDto;
import com.education.conversation.dto.enums.MessageStatus;
import com.education.conversation.dto.enums.MessageType;
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
    private ChatModel model;
    private Float temperature;
    private MessageType messageType;
    private String errorDetails;
    private BigDecimal inputToken;
    private BigDecimal outputToken;
    private MessageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    private OffsetDateTime created;

    public static ChatMessage newUserMessage(MessageRequestDto messageRequestDto, Conversation conversation) {
        return new ChatMessage()
                .setContent(messageRequestDto.getContent())
                .setMessageType(MessageType.TEXT)
                .setRole(ChatRole.USER)
                .setStatus(MessageStatus.NEW)
                .setModel(messageRequestDto.getModel())
                .setTemperature(messageRequestDto.getTemperature())
                .setConversation(conversation);
    }

    public static ChatMessage newAssistantMessage(AiResponse response, Conversation conversation) {
        return new ChatMessage()
                .setContent(response.getContent())
                .setMessageType(MessageType.TEXT)
                .setRole(response.getProviderVariant().getAssistant())
                .setInputToken(BigDecimal.valueOf(response.getPromptTokens()))
                .setOutputToken(BigDecimal.valueOf(response.getCompletionTokens()))
                .setConversation(conversation);
    }
}
