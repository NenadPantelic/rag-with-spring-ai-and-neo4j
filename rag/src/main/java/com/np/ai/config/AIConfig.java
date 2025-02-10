package com.np.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AIConfig {

    @Bean("defaultRestClientBuilder")
    RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean("openAIChatClient")
    ChatClient openAIChatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor()) // advisors are called before and after interactions with
                // the chat client
                .build(); // since only OpenAI is used in this project, it's autoconfigured
        // with ChatGPT parameters
    }

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

}
