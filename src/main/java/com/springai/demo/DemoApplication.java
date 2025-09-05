package com.springai.demo;

import com.springai.demo.repo.ChatRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private ChatRepository chatRepository;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultOptions(
                        OllamaOptions.builder().topP(0.9).topK(40).repeatPenalty(1.1).temperature(0.7).build()
                )
                .defaultAdvisors(
                        getHistoryAdvisor(),
                        SimpleLoggerAdvisor.builder().build()
                )
                .build();
    }

    private MessageChatMemoryAdvisor getHistoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
//                PostgresChatMemoryRepository.builer()
                MessageWindowChatMemory.builder()
                        .chatMemoryRepository(chatRepository)
                        .maxMessages(5)
                        .build()
        ).build();
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
