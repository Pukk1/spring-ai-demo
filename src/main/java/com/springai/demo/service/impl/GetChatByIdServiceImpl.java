package com.springai.demo.service.impl;

import com.springai.demo.model.Chat;
import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.GetChatByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetChatByIdServiceImpl implements GetChatByIdService {

    private final ChatRepository chatRepository;

    @Transactional
    @Override
    public Chat execute(Long id) {
        return chatRepository.findById(id).orElse(null);
    }
}
