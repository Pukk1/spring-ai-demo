package com.springai.demo.service.impl;


import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.TalkToModelStreamingService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class TalkToModelStreamingServiceImpl implements TalkToModelStreamingService {

    private final ChatRepository chatRepository;
    private final ChatClient chatClient;

    @Override
    @Transactional
    public SseEmitter execute(Long chatId, String prompt) {
        SseEmitter emitter = new SseEmitter(0L);

        chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(prompt).stream().chatResponse()
                .subscribe(
                        chatResponse -> processToken(chatResponse, emitter),
                        emitter::completeWithError,
                        emitter::complete
                );

        return emitter;
    }

    @SneakyThrows
    private void processToken(ChatResponse chatResponse, SseEmitter emitter) {
        emitter.send(chatResponse.getResult().getOutput());
    }
}
