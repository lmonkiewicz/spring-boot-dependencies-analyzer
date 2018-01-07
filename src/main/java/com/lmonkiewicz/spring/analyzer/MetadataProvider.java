package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.metadata.ApplicationMetadata;

import java.io.IOException;

public interface MetadataProvider {
    ApplicationMetadata getApplicationInfo() throws IOException;
}
