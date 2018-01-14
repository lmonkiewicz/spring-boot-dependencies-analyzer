package com.lmonkiewicz.spring.analyzer.adapter.metadata;

import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ApplicationMetadata;

import java.io.IOException;

public interface MetadataProvider {
    ApplicationMetadata getApplicationInfo() throws IOException;
}
