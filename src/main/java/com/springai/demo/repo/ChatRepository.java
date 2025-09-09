package com.springai.demo.repo;

import com.springai.demo.model.Chat;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
