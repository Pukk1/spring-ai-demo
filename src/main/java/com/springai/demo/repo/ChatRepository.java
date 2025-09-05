package com.springai.demo.repo;

import com.springai.demo.model.Chat;
import com.springai.demo.model.ChatEntry;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Primary
public interface ChatRepository extends JpaRepository<Chat, Long>, ChatMemoryRepository {
    @Override
    default void deleteByConversationId(String conversationId) {

    }

    @Override
    default void saveAll(String conversationId, List<Message> messages) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream()
                .map(ChatEntry::fromMessage)
                .forEach(chat::addEntry);
        save(chat);
    }

    @Override
    default List<Message> findByConversationId(String conversationId) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    default List<String> findConversationIds() {
        return findAll()
                .stream()
                .map(Chat::getId)
                .map(String::valueOf)
                .toList();
    }
}
