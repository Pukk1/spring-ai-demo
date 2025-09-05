package com.springai.demo.service;

import com.springai.demo.model.Chat;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface TalkToModelService {
    Chat execute(Long chatId, String prompt);

    SseEmitter executeStreaming(Long chatId, String prompt);
}
