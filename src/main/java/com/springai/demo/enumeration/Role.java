package com.springai.demo.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Role {
    USER("user") {
        @Override
        public Message toMessage(String prompt) {
            return new UserMessage(prompt);
        }
    },
    SYSTEM("system") {
        @Override
        public Message toMessage(String prompt) {
            return new SystemMessage(prompt);
        }
    },
    ASSISTANT("assistant") {
        @Override
        public Message toMessage(String prompt) {
            return new AssistantMessage(prompt);
        }
    };

    @Getter
    private final String role;

    public static Role fromRoleName(String roleName) {
        return Arrays.stream(values())
                .filter(role -> role.getRole().equals(roleName))
                .findAny()
                .orElseThrow();
    }

    abstract public Message toMessage(String prompt);
}
