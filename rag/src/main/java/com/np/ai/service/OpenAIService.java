package com.np.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
public class OpenAIService implements AIService {

    private final ChatClient chatClient;

    public OpenAIService(@Qualifier("openAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateBasicResponse(String systemPrompt, String userPrompt) {
        Assert.hasText(userPrompt, "User prompt must not be blank");

        return chatClient.prompt()
                .system(Optional.ofNullable(systemPrompt).orElse(""))
                .user(userPrompt)
//                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }

    @Override
    public Flux<String> streamBasicResponse(String systemPrompt, String userPrompt) {
        Assert.hasText(userPrompt, "User prompt must not be blank");

        return chatClient.prompt()
                .system(Optional.ofNullable(systemPrompt).orElse(""))
                .user(userPrompt)
                .stream()
                .content();
    }
}
