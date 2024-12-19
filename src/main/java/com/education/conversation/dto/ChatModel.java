package com.education.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatModel {
    GPT4O("gpt-4o");

    private String model;

    public static ChatModel getEnumOrNull(String value) {
        try {
            return ChatModel.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
