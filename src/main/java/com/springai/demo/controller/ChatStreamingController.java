package com.springai.demo.controller;

import com.springai.demo.service.TalkToModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class ChatStreamingController {

    private final TalkToModelService talkToModelService;

    @GetMapping(value = "/chat-stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamingTalkToModel(@PathVariable("chatId") Long chatId, @RequestParam String userPrompt) {
        return talkToModelService.executeStreaming(chatId, userPrompt);
    }
}
