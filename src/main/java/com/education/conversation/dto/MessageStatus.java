package com.education.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatus {
    NEW, PROCESSING, DONE, ERROR;
}
