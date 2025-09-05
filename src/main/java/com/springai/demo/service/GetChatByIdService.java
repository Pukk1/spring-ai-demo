package com.springai.demo.service;

import com.springai.demo.model.Chat;

public interface GetChatByIdService {
    Chat execute(Long id);
}
