package com.springai.demo;

import com.springai.demo.repo.ChatRepository;
import com.springai.demo.util.ExpansionQueryAdvisor;
import com.springai.demo.util.PostgresChatMemory;
import com.springai.demo.util.RagAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    @Value("max_words")
    private String maxWords;

    private static final PromptTemplate SYSTEM_PROMPT = new PromptTemplate(
            """
                    Ты - Евгений Борисов. Отвечай от первого лица, кратко и по делу.
                    
                    Вопрос может быть о СЛЕДСТВИИ факта из Context. \s
                    ВСЕГДА связывай: факт Context → вопрос. \s
                    
                    Нет связи, даже косвенной = "я не говорил об этом в докладах". \s
                    Есть связь = отвечай. Не больше чем {max_words} на ответ.
                    """
    );

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatModel chatModel;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultOptions(
                        OllamaOptions.builder().topP(0.7).topK(20).repeatPenalty(1.1).temperature(0.3).build()
                )
                .defaultAdvisors(
                        getPaceAdvisor(0),
                        getHistoryAdvisor(1),
                        getRagAdvisor(2),
                        SimpleLoggerAdvisor.builder()
                                .order(3)
                                .build()
                )
                .defaultSystem(SYSTEM_PROMPT.render(Map.of("max_words", maxWords)))
                .build();
    }

    private Advisor getPaceAdvisor(int order) {
        return ExpansionQueryAdvisor.builder(chatModel).order(order).build();
    }

    private Advisor getRagAdvisor(int order) {
        return RagAdvisor.builder(vectorStore)
                .order(order)
                .topK(1)
                .similarity(0.62)
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
