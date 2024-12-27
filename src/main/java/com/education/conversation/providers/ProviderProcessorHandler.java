package com.education.conversation.providers;

import com.education.conversation.dto.enums.ProviderVariant;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProviderProcessorHandler {
    private Map<ProviderVariant, ProviderProcessor> map = new ConcurrentHashMap<>();

    public void register(ProviderVariant providerVariant, ProviderProcessor providerProcessor) {
        map.put(providerVariant, providerProcessor);
    }

    public ProviderProcessor getProvider(ProviderVariant providerVariant) {
        return map.get(providerVariant);
    }
}
