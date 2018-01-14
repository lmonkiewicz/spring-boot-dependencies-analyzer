package com.lmonkiewicz.spring.analyzer.domain;

import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.domain.exception.AnalyzerException;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;

public class AnalyzerAPI {

    private final LoaderService loaderService;

    public AnalyzerAPI(GraphPort graphPort, ConfigurationPort configurationPort) {
        loaderService = new LoaderService(configurationPort, graphPort);
    }

    /**
     * Removes all nodes and relations from graph
     */
    public void clearGraph() {
        loaderService.clearGraph();
    }

    /**
     * Loads specified metadata into graph
     *
     * @param applicationMetadata - metadata to load
     * @param clearGraphBeforeLoad - flag indicating should all existing nodes and relations to be removed
     * @throws AnalyzerException
     */
    public void loadIntoGraph(ApplicationMetadata applicationMetadata, boolean clearGraphBeforeLoad) throws AnalyzerException {
        loaderService.loadIntoGraph(applicationMetadata, clearGraphBeforeLoad);
    }
}
