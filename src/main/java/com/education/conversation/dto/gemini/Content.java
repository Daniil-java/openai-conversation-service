package com.education.conversation.dto.gemini;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Content {
    private String role;
    private List<Part> parts;

    @Data
    @Accessors(chain = true)
    public static class Part {
        private String text;
    }
}
