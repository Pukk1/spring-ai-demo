package com.springai.demo.repo;

import com.springai.demo.model.LoadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadedDocumentRepository extends JpaRepository<LoadedDocument, Long> {
    boolean existsByFilenameAndContentHash(String filename, String contentType);
}
