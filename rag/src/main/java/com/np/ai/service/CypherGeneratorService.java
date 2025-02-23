package com.np.ai.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CypherGeneratorService {

    private static final String KEY_QUESTION = "question";

    private final PromptTemplate cypherGeneratorAlphabuzzTemplate;
    private final AIService aiService;

    public CypherGeneratorService(@Qualifier("openAIService") AIService aiService) {
        var template = new ClassPathResource("prompts/neo4j-cypher-generator-alphabuzz.st");
        this.cypherGeneratorAlphabuzzTemplate = new PromptTemplate(template);
        this.aiService = aiService;
    }

    public String createPromptFromTemplate(String originalUserPrompt) {
        var templateMap = new HashMap<String, Object>();
        templateMap.put(KEY_QUESTION, originalUserPrompt);
        return cypherGeneratorAlphabuzzTemplate.render(templateMap);
    }

    public String generateCypherQuery(String userPrompt) {
        var prompt = createPromptFromTemplate(userPrompt);
        return aiService.generateBasicResponse("", prompt);
    }

}
