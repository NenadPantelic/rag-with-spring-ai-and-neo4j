package com.np.ai.api.server;

import com.np.ai.api.request.AIPromptRequest;
import com.np.ai.api.request.BasicIndexingRequestFromFilesystem;
import com.np.ai.api.request.BasicIndexingRequestFromURL;
import com.np.ai.api.response.BasicIndexingResponse;
import com.np.ai.service.RAGBasicIndexingService;
import com.np.ai.service.RAGBasicProcessorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/basic")
@Validated
public class AIBasicRAGApi {

    private final RAGBasicIndexingService ragBasicIndexingService;
    private final RAGBasicProcessorService ragBasicProcessorService;

    public AIBasicRAGApi(RAGBasicIndexingService ragBasicIndexingService,
                         RAGBasicProcessorService ragBasicProcessorService) {
        this.ragBasicIndexingService = ragBasicIndexingService;
        this.ragBasicProcessorService = ragBasicProcessorService;
    }

    /**
     * curl --location 'http://localhost:8080/api/ai/rag/basic/indexing/document/fs' \
     * --header 'Content-Type: application/json' \
     * --data '{
     * "path": "company-financial-statement.html",
     * "outputFilename": "processed-html.txt",
     * "appendIfFileExists": true,
     * "keywords": ["html", "rag-test"]
     * }'
     */
    @PostMapping(
            path = "/indexing/document/fs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BasicIndexingResponse> indexDocumentFromFilesystem(@RequestBody @Valid BasicIndexingRequestFromFilesystem request) {
        var indexedDocuments = ragBasicIndexingService.indexDocumentFromFilesystem(
                request.path(), request.outputFilename(), request.appendIfFileExists(), request.keywords()
        );

        return ResponseEntity.ok(
                new BasicIndexingResponse(
                        true,
                        "Document successfully indexed as " + indexedDocuments.size() + " chunks"
                )
        );
    }

    /**
     * curl --location 'http://localhost:8080/api/ai/rag/basic/indexing/document/url' \
     * --header 'Content-Type: application/json' \
     * --data '{
     * "url": "https://raw.githubusercontent.com/itsfoss/text-script-files/refs/heads/master/sherlock.txt",
     * "outputFilename": "processed-file-from-url.txt",
     * "appendIfFileExists": true,
     * "keywords": ["url", "rag-test"]
     * }'
     */
    @PostMapping(
            path = "/indexing/document/url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BasicIndexingResponse> indexDocumentFromURL(@RequestBody @Valid BasicIndexingRequestFromURL request) {
        var indexedDocuments = ragBasicIndexingService.indexDocumentFromUrl(
                request.url(), request.outputFilename(), request.appendIfFileExists(), request.keywords()
        );

        return ResponseEntity.ok(
                new BasicIndexingResponse(
                        true,
                        "Document successfully indexed as " + indexedDocuments.size() + " chunks"
                )
        );
    }

    // https://github.com/spring-projects/spring-ai/issues/981
    @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> basicRag(@RequestBody @Valid AIPromptRequest request,
                                 @RequestParam(name = "filename") @NotBlank String filenameForCustomContext) {
        var response = ragBasicProcessorService.generateRAGResponse(
                request.systemPrompt(),
                request.userPrompt(),
                filenameForCustomContext
        );
        return Mono.just(response);
    }

    /**
     * curl --location 'http://localhost:8080/api/ai/rag/basic/ask/stream?filename=indexed-company-financial.statement.txt' \
     * --header 'Content-Type: application/json' \
     * --data '{
     * "systemPrompt": "You are an accountant",
     * "userPrompt": "How much is our marketing expense this year? Is marketing our biggest expense?"
     * }'
     */
    @PostMapping(path = "/ask/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_NDJSON_VALUE)
    // application/x-ndjson -> newline delimited JSON = DataWeave represents the Newline Delimited JSON format (ndjson)
    // as an array of objects. Each line of the ndjson format is mapped to one object in the array.
    public Flux<String> streamRag(@RequestBody @Valid AIPromptRequest request,
                                  @RequestParam(name = "filename") @NotBlank String filenameForCustomContext) {
        return ragBasicProcessorService.streamRAGResponse(
                request.systemPrompt(),
                request.userPrompt(),
                filenameForCustomContext
        );
    }

}
