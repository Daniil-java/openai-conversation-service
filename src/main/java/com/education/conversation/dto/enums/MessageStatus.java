package com.education.conversation.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatus {
    NEW, PROCESSING, DONE, ERROR;
}
