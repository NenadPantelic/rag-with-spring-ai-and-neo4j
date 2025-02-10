package com.np.ai.api.server;

import com.np.ai.api.request.AIPromptRequest;
import com.np.ai.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.awt.*;

@RestController
@RequestMapping("/api/v1")
@Validated
public class AIBasicApi {

    private final AIService aiService;

    public AIBasicApi(@Autowired @Qualifier("openAIService") AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/basic", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> basicAI(@RequestBody @Valid AIPromptRequest request) {
        var response = aiService.generateBasicResponse(request.systemPrompt(), request.userPrompt());
        return Mono.just(response);
    }

    @PostMapping(value = "/basic/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_NDJSON_VALUE
    )
    public Flux<String> basicStreamAI(@RequestBody @Valid AIPromptRequest request) {
        return aiService.streamBasicResponse(request.systemPrompt(), request.userPrompt());
    }
}
