package com.np.ai.api.server;

import com.np.ai.api.request.AIPromptRequest;
import com.np.ai.service.KnowledgeGraphProcessorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/kg")
@Validated
public class AIKnowledgeGraphApi {

    private final KnowledgeGraphProcessorService knowledgeGraphProcessorService;

    public AIKnowledgeGraphApi(KnowledgeGraphProcessorService knowledgeGraphProcessorService) {
        this.knowledgeGraphProcessorService = knowledgeGraphProcessorService;
    }

    @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> knowledgeGraphRAG(@RequestBody @Valid AIPromptRequest request,
                                          @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
        var response = knowledgeGraphProcessorService.generateRAGResponse(
                request.systemPrompt(), request.userPrompt(), topK
        );

        return Mono.just(response);
    }

    @PostMapping(path = "/ask/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> knowledgeGraphStreamRAG(@RequestBody @Valid AIPromptRequest request,
                                                @RequestParam(name = "top-k", required = false, defaultValue = "0") int topK) {
        return knowledgeGraphProcessorService.streamRAGResponse(request.systemPrompt(), request.userPrompt(), topK);
    }
}
