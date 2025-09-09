package com.springai.demo.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface TalkToModelStreamingService {
    SseEmitter execute(Long chatId, String prompt);
}
