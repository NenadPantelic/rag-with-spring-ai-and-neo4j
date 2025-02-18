package com.np.ai.api.server;

import com.np.ai.api.request.*;
import com.np.ai.api.response.BasicIndexingResponse;
import com.np.ai.service.RAGVectorIndexingService;
import com.np.ai.service.RAGVectorProcessorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai/rag/vector")
@Validated
public class AIVectorRAGApi {

    private final RAGVectorIndexingService ragVectorIndexingService;
    private final RAGVectorProcessorService ragVectorProcessorService;

    public AIVectorRAGApi(RAGVectorIndexingService ragVectorIndexingService,
                          RAGVectorProcessorService ragVectorProcessorService) {
        this.ragVectorIndexingService = ragVectorIndexingService;
        this.ragVectorProcessorService = ragVectorProcessorService;
    }

    @PostMapping(
            path = "/indexing/document/fs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BasicIndexingResponse> indexDocumentFromFilesystem(@RequestBody @Valid VectorIndexingRequestFromFilesystem request) {
        var indexedDocuments = ragVectorIndexingService.indexDocumentFromFilesystem(
                request.path(), request.keywords()
        );

        return ResponseEntity.ok(
                new BasicIndexingResponse(
                        true,
                        "Document successfully indexed as " + indexedDocuments.size() + " chunks"
                )
        );
    }

    @PostMapping(
            path = "/indexing/document/url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BasicIndexingResponse> indexDocumentFromURL(@RequestBody @Valid VectorIndexingRequestFromURL request) {
        var indexedDocuments = ragVectorIndexingService.indexDocumentFromUrl(
                request.url(), request.keywords()
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
                                 @RequestParam(name = "topk", defaultValue = "0") int topK) {
        var response = ragVectorProcessorService.generateRAGResponse(
                request.systemPrompt(),
                request.userPrompt(),
                topK
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
                                  @RequestParam(name = "topk", defaultValue = "0") int topK) {
        return ragVectorProcessorService.streamRAGResponse(
                request.systemPrompt(),
                request.userPrompt(),
                topK
        );
    }

    @PostMapping(
            path = "/indexing/document/fs/reactive",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<BasicIndexingResponse>> indexDocumentFromFilesystemReactive(@RequestBody @Valid VectorIndexingRequestFromFilesystem request) {
        var indexedDocuments = ragVectorIndexingService.indexDocumentsFromFilesystemReactive(
                request.path(), request.keywords()
        );

        return indexedDocuments.map(indexedDocument ->
                ResponseEntity.ok(new BasicIndexingResponse(
                        true,
                        "Document successfully indexed as " + indexedDocument.size() + " chunks"
                ))
        ).defaultIfEmpty(
                ResponseEntity.ok(new BasicIndexingResponse(
                        false,
                        "Document indexing failed"
                ))
        );
    }

    @PostMapping(
            path = "/indexing/document/url/reactive",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<BasicIndexingResponse>> indexDocumentFromURLReactive(@RequestBody @Valid VectorIndexingRequestFromURL request) {
        var indexedDocuments = ragVectorIndexingService.indexDocumentsFromURLReactive(
                request.url(), request.keywords()
        );

        return indexedDocuments.map(indexedDocument ->
                ResponseEntity.ok(new BasicIndexingResponse(
                        true,
                        "Document successfully indexed as " + indexedDocument.size() + " chunks"
                ))
        ).defaultIfEmpty(
                ResponseEntity.ok(new BasicIndexingResponse(
                        false,
                        "Document indexing failed"
                ))
        );
    }
}
