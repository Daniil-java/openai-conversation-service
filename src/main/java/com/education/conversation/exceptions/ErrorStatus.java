package com.education.conversation.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    OPENAI_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "OpenAI connection error!"),
    CONVERSATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Conversation not found!"),
    CONVERSATION_SPECIFIED_ERROR(HttpStatus.BAD_REQUEST, "The conversation must be specified"),
    MODEL_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "Model is not supported");

    private HttpStatus httpStatus;
    private String message;
}
