package com.springai.demo.service.impl;

import com.springai.demo.model.Chat;
import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.CreateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateChatServiceImpl implements CreateChatService {

    private final ChatRepository chatRepository;

    @Transactional
    @Override
    public Chat execute(String title) {
        var chat = new Chat();
        chat.setTitle(title);
        return chatRepository.save(chat);
    }
}
