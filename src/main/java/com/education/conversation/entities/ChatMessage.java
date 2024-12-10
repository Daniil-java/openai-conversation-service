package com.education.conversation.entities;

import com.education.conversation.dto.ChatRole;
import com.education.conversation.dto.MessageType;
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
    private OffsetDateTime created;
}
