package com.np.ai.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
public class RAGVectorProcessorService {

    private static final String KEY_CUSTOM_CONTEXT = "customContext";
    private static final String KEY_QUESTION = "question";
    private static final int TOP_K = 4;
    private static final double SIMILARITY_THRESHOLD = 0.7;

    private static final String RAG_BASIC_TEMPLATE = "prompts/rag-basic-template.st";

    private final PromptTemplate basicAugmentationTemplate;
    private final Neo4jVectorStore vectorStore;

    private final AIService aiService;

    public RAGVectorProcessorService(Neo4jVectorStore vectorStore,
                                     @Qualifier("openAIService") AIService aiService) {
        this.vectorStore = vectorStore;
        var ragBasicPromptTemplate = new ClassPathResource(RAG_BASIC_TEMPLATE);
        this.basicAugmentationTemplate = new PromptTemplate(ragBasicPromptTemplate);
        this.aiService = aiService;
    }

    private String retrieveCustomContext(String userPrompt, int topK) {
        var searchRequest = SearchRequest.builder()
                .query(userPrompt)
                .topK(topK > 0 ? topK : TOP_K)
                .similarityThreshold(SIMILARITY_THRESHOLD)
                .build();

        var customContext = new StringBuilder();
        var similarDocuments = vectorStore.similaritySearch(searchRequest);

        for (var document : similarDocuments) {
            customContext.append(document.getFormattedContent()).append("\n");
        }

        return customContext.toString();
    }

    private String augmentUserPrompt(String originalUserPrompt, String customContext) {
        var templateMap = new HashMap<String, Object>();
        templateMap.put(KEY_QUESTION, originalUserPrompt);
        templateMap.put(KEY_CUSTOM_CONTEXT, customContext);

        return basicAugmentationTemplate.render(templateMap);
    }

    public String generateRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var customContext = retrieveCustomContext(userPrompt, topK);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);
        return aiService.generateBasicResponse(systemPrompt, augmentedUserPrompt);
    }

    public Flux<String> streamRAGResponse(String systemPrompt, String userPrompt, int topK) {
        var customContext = retrieveCustomContext(userPrompt, topK);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);
        return aiService.streamBasicResponse(systemPrompt, augmentedUserPrompt);
    }

}
