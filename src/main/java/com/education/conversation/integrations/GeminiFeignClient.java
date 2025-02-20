package com.education.conversation.integrations;

import com.education.conversation.dto.models.gemini.GeminiRequest;
import com.education.conversation.dto.models.gemini.GeminiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value = "gemini-feign-client",
        url = "https://generativelanguage.googleapis.com/v1beta/models/"
)
public interface GeminiFeignClient {

    @PostMapping("/{model}:generateContent")
    GeminiResponse generate(@PathVariable String model,
                            @RequestParam String key,
                            @RequestBody GeminiRequest request);
}
