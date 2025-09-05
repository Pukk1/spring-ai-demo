package com.springai.demo.service.impl;


import com.springai.demo.enumeration.Role;
import com.springai.demo.model.Chat;
import com.springai.demo.model.ChatEntry;
import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.TalkToModelService;
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
public class TalkToModelServiceImpl implements TalkToModelService {

    private final ChatRepository chatRepository;
    private final ChatClient chatClient;

    @Transactional
    @Override
    public Chat execute(Long chatId, String prompt) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        var entry = addChatEntry(chat, Role.USER, prompt);

        var resp = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        entry = addChatEntry(chat, Role.ASSISTANT, resp);

        chat.getHistory().add(entry);
        return chat;
    }

    @Override
    @Transactional
    public SseEmitter executeStreaming(Long chatId, String prompt) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        var entry = addChatEntry(chat, Role.USER, prompt);

        StringBuilder answer = new StringBuilder();

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

    private ChatEntry addChatEntry(Chat chat, Role role, String prompt) {
        var entry = new ChatEntry();
        entry.setContent(prompt);
        entry.setRole(role);
        chat.getHistory().add(entry);
        return entry;
    }
}
