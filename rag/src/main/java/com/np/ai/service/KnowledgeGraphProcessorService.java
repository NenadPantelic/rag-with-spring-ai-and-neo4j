package com.np.ai.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Service
public class KnowledgeGraphProcessorService {

    private static final String KEY_VECTOR_CONTEXT = "vectorContext";
    private static final String KEY_KNOWLEDGE_GRAPH_CONTEXT = "knowledgeGraphContext";
    private static final String KEY_QUESTION = "question";

    private static final int TOP_K = 4;
    private static final double SIMILARITY_THRESHOLD = 0.7;


    private final PromptTemplate knowledgeGraphAugmentationTemplate;
    private final Neo4jVectorStore vectorStore;
    private final AIService aiService;
    private final ReactiveNeo4jClient neo4jClient;
    private final CypherGeneratorService cypherGeneratorService;

    public KnowledgeGraphProcessorService(Neo4jVectorStore vectorStore,
                                          AIService aiService,
                                          ReactiveNeo4jClient neo4jClient,
                                          CypherGeneratorService cypherGeneratorService) {
        var template = new ClassPathResource("prompts/rag-knowledge-graph-template.st");
        knowledgeGraphAugmentationTemplate = new PromptTemplate(template);
        this.vectorStore = vectorStore;
        this.aiService = aiService;
        this.neo4jClient = neo4jClient;
        this.cypherGeneratorService = cypherGeneratorService;
    }

    private String retrieveVectorStoreContext(String userPrompt, int topK) {
        var similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userPrompt)
                        .topK(topK)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()
        );
        if (similarDocuments == null) {
            return StringUtils.EMPTY;
        }

        var customContext = new StringBuilder();
        similarDocuments.forEach(doc -> customContext.append(doc.getFormattedContent()).append("\n"));
        return customContext.toString();
    }

    private Mono<String> queryFromKnowledgeGraph(String cypher) {
        var strBuilder = new StringBuilder();

        return neo4jClient.query(cypher)
                .fetch()
                .all()
                .collectList()
                .flatMap(result -> {
                    if (result.isEmpty()) {
                        return Mono.just(StringUtils.EMPTY);
                    }

                    var resultHeader = String.join(",", result.get(0).keySet());
                    strBuilder.append(resultHeader).append("\n");

                    return Flux.fromIterable(result).flatMap(record -> {
                                try (var writer = new StringWriter();
                                     var csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                                    csvPrinter.printRecord(record.values());
                                    strBuilder.append(writer);
                                } catch (Exception e) {
                                    return Mono.error(e);
                                }

                                return Mono.empty();
                            }).collectList()
                            .map(list -> strBuilder.toString());
                })
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just(StringUtils.EMPTY);
                });
    }

    private String retrieveKnowledgeGraphContext(String userPrompt) {
        var cypher = cypherGeneratorService.generateCypherQuery(userPrompt);
        String result = "";

        try {
            result = queryFromKnowledgeGraph(cypher).toFuture().get(3, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String augmentUserPrompt(String originalUserPrompt, String vectorContext, String knowledgeGraphContext) {
        var templateMap = new HashMap<String, Object>();

        templateMap.put(KEY_QUESTION, originalUserPrompt);
        templateMap.put(KEY_VECTOR_CONTEXT, vectorContext);
        templateMap.put(KEY_KNOWLEDGE_GRAPH_CONTEXT, knowledgeGraphContext);

        return knowledgeGraphAugmentationTemplate.render(templateMap);
    }

    public String generateRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var vectorContext = retrieveVectorStoreContext(userPrompt, topK);
        var knowledgeGraphContext = retrieveKnowledgeGraphContext(userPrompt);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, vectorContext, knowledgeGraphContext);

        return aiService.generateBasicResponse(systemPrompt, augmentedUserPrompt);
    }

    public Flux<String> streamRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var vectorContext = retrieveVectorStoreContext(userPrompt, topK);
        var knowledgeGraphContext = retrieveKnowledgeGraphContext(userPrompt);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, vectorContext, knowledgeGraphContext);

        return aiService.streamBasicResponse(systemPrompt, augmentedUserPrompt);
    }
}
