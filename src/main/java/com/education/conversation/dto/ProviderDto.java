package com.education.conversation.dto;

import com.education.conversation.dto.enums.ChatModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProviderDto {
    private Long id;
    private String name;
    private ChatModel model;
    private String description;

}

