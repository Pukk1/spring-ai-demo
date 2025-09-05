package com.springai.demo.model;

import com.springai.demo.enumeration.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String content;
    @CreationTimestamp
    private LocalDate createdAt;

    public Message toMessage() {
        return role.toMessage(content);
    }

    public static ChatEntry fromMessage(Message message) {
        return ChatEntry.builder()
                .role(Role.fromRoleName(message.getMessageType().getValue()))
                .content(message.getText())
                .build();
    }
}
