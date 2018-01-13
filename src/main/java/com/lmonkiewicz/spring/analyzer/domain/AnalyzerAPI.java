package com.lmonkiewicz.spring.analyzer.domain;

import com.lmonkiewicz.spring.analyzer.domain.exception.AnalyzerException;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.MetadataProviderPort;
import com.lmonkiewicz.spring.analyzer.domain.properties.AnalyzerProperties;

import java.io.IOException;

public class AnalyzerAPI {

    private final AnalyzerService analyzerService;

    public AnalyzerAPI(GraphPort graphPort, AnalyzerProperties analyzerProperties, MetadataProviderPort metadataProviderPort) {
        analyzerService = new AnalyzerService(metadataProviderPort, analyzerProperties, graphPort);
    }

    public void processData() throws AnalyzerException {
        try {
            analyzerService.processData();
        } catch (IOException e) {
            throw new AnalyzerException("Error while processing data", e);
        }
    }
}
