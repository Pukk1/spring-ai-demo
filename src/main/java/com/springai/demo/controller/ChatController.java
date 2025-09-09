package com.springai.demo.controller;

import com.springai.demo.service.CreateChatService;
import com.springai.demo.service.DeleteChatService;
import com.springai.demo.service.GetAllChatService;
import com.springai.demo.service.GetChatByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final GetAllChatService getAllChatService;
    private final GetChatByIdService getChatByIdService;
    private final CreateChatService createChatService;
    private final DeleteChatService deleteChatService;

    @GetMapping
    public String index(ModelMap model) {
        var chats = getAllChatService.execute();
        model.addAttribute("chats", chats);
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String chat(@PathVariable("chatId") Long chatId, ModelMap model) {
        var chats = getAllChatService.execute();
        var chat = getChatByIdService.execute(chatId);
        model.addAttribute("chats", chats);
        model.addAttribute("chat", chat);
        return "chat";
    }

    @PostMapping("/chat/new")
    public String createChat(@RequestParam String title, ModelMap model) {
        var chat = createChatService.execute(title);
        model.addAttribute("chat", chat);
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable("chatId") Long chatId, ModelMap model) {
        deleteChatService.execute(chatId);
        return "redirect:/";
    }
}
