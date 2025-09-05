package com.springai.demo.repo;

import com.springai.demo.model.ChatEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<ChatEntry, Long> {
}
