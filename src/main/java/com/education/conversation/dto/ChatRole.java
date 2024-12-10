package com.education.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRole {
    USER("user"), ASSISTANT("assistant");

    private String value;
}
