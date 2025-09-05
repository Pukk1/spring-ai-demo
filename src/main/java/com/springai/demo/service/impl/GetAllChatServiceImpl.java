package com.springai.demo.service.impl;

import com.springai.demo.model.Chat;
import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.GetAllChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetAllChatServiceImpl implements GetAllChatService {

    private final ChatRepository chatRepository;

    @Transactional
    @Override
    public Set<Chat> execute() {
        return new HashSet<>(chatRepository.findAll());
    }
}
