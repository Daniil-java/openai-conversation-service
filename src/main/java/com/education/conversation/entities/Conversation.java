package com.education.conversation.entities;

import com.education.conversation.dto.ConversationDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @UpdateTimestamp
    private OffsetDateTime updated;
    @CreationTimestamp
    private OffsetDateTime created;

    public static Conversation dtoToEntity(ConversationDto conversationDto) {
        return new Conversation()
                .setId(conversationDto.getId())
                .setName(conversationDto.getName());
    }

    public static ConversationDto entityToDto(Conversation conversation) {
        return new ConversationDto()
                .setId(conversation.getId())
                .setName(conversation.getName());
    }


}
