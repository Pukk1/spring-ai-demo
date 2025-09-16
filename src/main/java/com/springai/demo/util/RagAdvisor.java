package com.springai.demo.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Map;
import java.util.stream.Collectors;

import static com.springai.demo.util.ExpansionQueryAdvisor.EXPANSION_QUERY_ADVISOR;

@Builder
public class RagAdvisor implements BaseAdvisor {

    @Builder.Default
    private static final PromptTemplate template = PromptTemplate.builder().template("""
            CONTEXT: {context}
            Question: {question}
            """).build();

    @Builder.Default
    private int topK = 4;
    @Builder.Default
    private double similarity = 64;
    @Getter
    private int order;

    private VectorStore vectorStore;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String originalUserQuery = chatClientRequest.prompt().getUserMessage().getText();
        String queryToRag = chatClientRequest.context().getOrDefault(EXPANSION_QUERY_ADVISOR, originalUserQuery).toString();
        SearchRequest searchRequest = SearchRequest.builder().topK(topK).similarityThreshold(similarity).query(queryToRag).build();
        var documents = vectorStore.similaritySearch(searchRequest);
        String llmContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String finalUserPrompt = template.render(Map.of("context", llmContext, "question", originalUserQuery));
        return chatClientRequest.mutate().prompt(chatClientRequest.prompt().augmentUserMessage(finalUserPrompt)).build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }

    public static RagAdvisorBuilder builder(VectorStore vectorStore) {
        return new RagAdvisorBuilder().vectorStore(vectorStore);
    }
}
