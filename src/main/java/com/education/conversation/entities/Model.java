package com.education.conversation.entities;

import com.education.conversation.dto.enums.ProviderVariant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "models")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProviderVariant provider;
    private String model;
    private String description;
    @UpdateTimestamp
    private OffsetDateTime updated;
    @CreationTimestamp
    private OffsetDateTime created;
}
