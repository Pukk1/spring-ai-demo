package com.springai.demo.service.impl;

import com.springai.demo.repo.ChatRepository;
import com.springai.demo.service.DeleteChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteChatServiceImpl implements DeleteChatService {

    private final ChatRepository chatRepository;

    @Transactional
    @Override
    public void execute(Long id) {
        chatRepository.deleteById(id);
    }
}
