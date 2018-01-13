package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ApplicationMetadata;

import java.io.IOException;

public interface MetadataProviderPort {
    ApplicationMetadata getApplicationInfo() throws IOException;
}
