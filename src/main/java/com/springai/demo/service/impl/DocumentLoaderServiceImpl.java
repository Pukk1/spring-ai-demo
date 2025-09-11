package com.springai.demo.service.impl;

import com.springai.demo.model.LoadedDocument;
import com.springai.demo.repo.LoadedDocumentRepository;
import com.springai.demo.service.DocumentLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentLoaderServiceImpl implements DocumentLoaderService {

    private final LoadedDocumentRepository loadedDocumentRepository;
    private final ResourcePatternResolver resourcePatternResolver;
    private final VectorStore vectorStore;

    @SneakyThrows
    @EventListener(ContextRefreshedEvent.class)
    public void loadNewDocuments() {
        List<Resource> resources = Arrays.stream(resourcePatternResolver.getResources("classpath:/knowledgebase/**/*.txt")).toList();
        resources.stream()
                .filter(resource -> !loadedDocumentRepository.existsByFilenameAndContentHash(resource.getFilename(), calcContentHash(resource)))
                .forEach(resource -> {
                    List<Document> documents = new TextReader(resource).get();
                    TokenTextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(200).build();
                    List<Document> chunks = textSplitter.apply(documents);
                    vectorStore.accept(chunks);

                    LoadedDocument loadedDocument = LoadedDocument.builder()
                            .documentType("txt")
                            .chunkCount(chunks.size())
                            .filename(resource.getFilename())
                            .contentHash(calcContentHash(resource))
                            .build();
                    loadedDocumentRepository.save(loadedDocument);

                });
    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }
}
