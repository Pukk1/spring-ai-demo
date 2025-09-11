package com.springai.demo;

import com.springai.demo.repo.ChatRepository;
import com.springai.demo.util.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private ChatRepository chatRepository;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultOptions(
                        OllamaOptions.builder().topP(0.7).topK(20).repeatPenalty(1.1).temperature(0.3).build()
                )
                .defaultAdvisors(
                        getHistoryAdvisor(1),
                        SimpleLoggerAdvisor.builder()
                                .order(2)
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
