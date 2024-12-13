package com.education.conversation.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    OPENAI_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "OpenAI connection error!");

    private HttpStatus httpStatus;
    private String message;
}
