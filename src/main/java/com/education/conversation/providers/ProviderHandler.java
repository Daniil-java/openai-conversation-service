package com.education.conversation.providers;

import com.education.conversation.dto.enums.ProviderVariant;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProviderHandler {
    private Map<ProviderVariant, Provider> map = new ConcurrentHashMap<>();

    public void register(ProviderVariant providerVariant, Provider provider) {
        map.put(providerVariant, provider);
    }

    public Provider getProvider(ProviderVariant providerVariant) {
        return map.get(providerVariant);
    }
}
