package com.springai.demo;

import com.springai.demo.repo.ChatRepository;
import com.springai.demo.util.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    private final String MY_DEFAULT_PROMPT_TEMPLATE = """
            {query}
            
            Ответ должен быть коротким - не более двух предложений.
            
            Если вопрос не про Евгения Борисова, вначале ответа добавь: "Вообще-то я тут не за этим, но могу и ответить."
            
            Контекстная информация приведена ниже, выделена ---------------------
            
            ---------------------
            {question_answer_context}
            ---------------------
            
            Учитывая контекст и предоставленную историческую информацию, а не предварительные знания,
            ответьте на комментарий пользователя. Если ответа нет в контексте, сообщите
            пользователю, что вы не можете ответить на вопрос.
            """;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private VectorStore vectorStore;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultOptions(
                        OllamaOptions.builder().topP(0.7).topK(20).repeatPenalty(1.1).temperature(0.3).build()
                )
                .defaultAdvisors(
                        getHistoryAdvisor(1),
                        getRagAdvisor(2),
                        SimpleLoggerAdvisor.builder()
                                .order(3)
                                .build()
                )
                .build();
    }

    private Advisor getRagAdvisor(int order) {
        return QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(PromptTemplate.builder().template(MY_DEFAULT_PROMPT_TEMPLATE).build())
                .order(order)
                .searchRequest(
                        SearchRequest.builder()
                                .similarityThreshold(0.5)
                                .topK(4)
                                .build()
                )
                .build();
    }

    private MessageChatMemoryAdvisor getHistoryAdvisor(int order) {
        return MessageChatMemoryAdvisor.builder(
                PostgresChatMemory.builder()
                        .chatMemoryRepository(chatRepository)
                        .maxMessages(5)
                        .build()
        )
                .order(order)
                .build();
    }

    public static void main(String[] args) {
        var context = SpringApplication.run(DemoApplication.class, args);
//        var client = context.getBean(ChatClient.class);
//        var response = client.prompt("дай первую строчку багемской рапсодии")
//                .options(OllamaOptions.builder().build())
//                .call()
//                .content();
//        System.out.println(response);
    }

}
