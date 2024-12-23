package com.education.conversation.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatModel {
    GPT4O("gpt-4o"),
    GEMINI_2_0_FLASH_EXP("gemini-2.0-flash-exp"),
    GEMINI_1_5_PRO("gemini-1.5-pro"),
    GEMINI_1_5_FLASH_LATEST("gemini-1.5-flash-latest");

    private String model;

}
