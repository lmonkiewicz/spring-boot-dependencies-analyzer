package com.lmonkiewicz.spring.analyzer.adapter.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.config.properties.SourceProperties;
import com.lmonkiewicz.spring.analyzer.domain.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.domain.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.domain.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.domain.ports.MetadataProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultMetadataProviderAdapter implements MetadataProviderPort {


    private final AnalyzerProperties analyzerProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultMetadataProviderAdapter(AnalyzerProperties analyzerProperties, ObjectMapper objectMapper) {
        this.analyzerProperties = analyzerProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApplicationMetadata getApplicationInfo() throws IOException {
        final SourceProperties sourceProperties = Optional.ofNullable(analyzerProperties.getSource()).orElseThrow(() -> new RuntimeException("Bad configuration"));

        if (sourceProperties.getResource() != null && !sourceProperties.getResource().isEmpty()) {
            return loadFromResource(sourceProperties.getResource());
        }
        else if (sourceProperties.getFile() != null && !sourceProperties.getFile().isEmpty()) {
            return loadFromFile(sourceProperties.getFile());
        }
        else if (sourceProperties.getUrl() != null && !sourceProperties.getUrl().isEmpty()) {
            return loadFromUrl(sourceProperties.getUrl());
        }
        else {
            throw new RuntimeException("No source defined");
        }
    }

    private ApplicationMetadata loadFromResource(String resource) throws IOException {
        log.info("Loading beans metadata from resource: {}", resource);
        final InputStream inputStream = DefaultMetadataProviderAdapter.class.getResourceAsStream(resource);

        final List<ContextMetadata> contexts = objectMapper.readValue(inputStream,  new TypeReference<List<ContextMetadata>>(){});

        return ApplicationMetadata.builder()
                .contexts(contexts)
                .build();
    }

    private ApplicationMetadata loadFromFile(String file) {
        log.info("Loading beans metadata from file: {}", file);

        return mock();
    }

    private ApplicationMetadata loadFromUrl(String url) {
        log.info("Loading beans metadata from url: {}", url);

        return mock();
    }

    private ApplicationMetadata mock() {
        return ApplicationMetadata.builder()
                .context(ContextMetadata.builder()
                        .bean(BeanMetadata.builder()
                                .bean("testBean")
                                .type("com.lmonkiewicz.TestBean")
                                .scope("singleton")
                                .build())
                        .bean(BeanMetadata.builder()
                                .bean("test2Bean")
                                .type("com.lmonkiewicz.Test2Bean")
                                .scope("singleton")
                                .dependencies(Arrays.asList("test3Bean"))
                                .build())
                        .bean(BeanMetadata.builder()
                                .bean("test3Bean")
                                .type("com.lmonkiewicz.Test3Bean")
                                .scope("singleton")
                                .build())
                        .build())
                .build();
    }
}
