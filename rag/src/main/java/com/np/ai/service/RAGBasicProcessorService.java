package com.np.ai.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
public class RAGBasicProcessorService {

    private static final String KEY_CUSTOM_CONTEXT = "customContext";
    private static final String KEY_QUESTION = "question";

    private static final String RAG_BASIC_TEMPLATE = "prompts/rag-basic-template.st";

    private final PromptTemplate basicAugmentationTemplate;

    private final AIService aiService;

    public RAGBasicProcessorService(@Qualifier("openAIService") AIService aiService) {
        var ragBasicPromptTemplate = new ClassPathResource(RAG_BASIC_TEMPLATE);
        this.basicAugmentationTemplate = new PromptTemplate(ragBasicPromptTemplate);
        this.aiService = aiService;
    }

    private String retrieveCustomContext(String fromFilename) {
        try {
            return new String(Files.readAllBytes(Paths.get(fromFilename)));
        } catch (Exception e) {
            return "";
        }
    }

    private String augmentUserPrompt(String originalUserPrompt, String customContext) {
        var templateMap = new HashMap<String, Object>();
        templateMap.put(KEY_QUESTION, originalUserPrompt);
        templateMap.put(KEY_CUSTOM_CONTEXT, customContext);

        return basicAugmentationTemplate.render(templateMap);
    }

    public String generateRAGResponse(String systemPrompt, String userPrompt, String filenameForCustomContext) {
        var customContext = retrieveCustomContext(filenameForCustomContext);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);
        return aiService.generateBasicResponse(systemPrompt, augmentedUserPrompt);
    }

    public Flux<String> streamRAGResponse(String systemPrompt, String userPrompt, String filenameForCustomContext) {
        var customContext = retrieveCustomContext(filenameForCustomContext);
        var augmentedUserPrompt = augmentUserPrompt(userPrompt, customContext);
        return aiService.streamBasicResponse(systemPrompt, augmentedUserPrompt);
    }

}
