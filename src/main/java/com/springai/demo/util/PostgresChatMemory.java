package com.springai.demo.util;

import com.springai.demo.model.Chat;
import com.springai.demo.model.ChatEntry;
import com.springai.demo.repo.ChatRepository;
import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@Builder
public class PostgresChatMemory implements ChatMemory {

    private ChatRepository chatMemoryRepository;
    private int maxMessages;

    @Override
    public void add(String conversationId, List<Message> messages) {
        Chat chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream()
                .map(ChatEntry::fromMessage)
                .forEach(chat::addEntry);
        chatMemoryRepository.save(chat);
    }

    @Override
    public List<Message> get(String conversationId) {
        Chat chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        int messagesToSkip = Math.max(chat.getHistory().size() - maxMessages, 0);
        return chat.getHistory().stream()
                .skip(messagesToSkip)
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        //TODO(not need to implement)
    }
}
