package com.education.conversation.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatModel {
    GPT4O("gpt-4o"),
    GEMINI_1_5_PRO("gemini-1.5-pro");

    private String model;

}
