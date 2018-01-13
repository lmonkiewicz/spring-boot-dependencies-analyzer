package com.lmonkiewicz.spring.analyzer.domain;

import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.BeanNode;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.DependsOnRelation;
import com.lmonkiewicz.spring.analyzer.domain.condition.RegexpFieldCondition;
import com.lmonkiewicz.spring.analyzer.domain.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.domain.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.domain.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.MetadataProviderPort;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
class AnalyzerService {

    private final MetadataProviderPort metadataProvider;
    private final ConfigurationPort config;
    private final GraphPort graph;

    public AnalyzerService(MetadataProviderPort metadataProvider,
                           ConfigurationPort config,
                           GraphPort graph) {
        this.metadataProvider = metadataProvider;
        this.config = config;
        this.graph = graph;
    }

    public void processData() throws IOException {

        log.info("Loading beans data");
        ApplicationMetadata applicationMetadata = metadataProvider.getApplicationInfo();

        if (config.isClearGraphOnStartup()) {
            log.info("Clearing graph");
            graph.clear();
        }

        log.info("Populating database...");
        applicationMetadata.getContexts().forEach(this::processContext);

        log.info("Done");
    }

    private void processContext(ContextMetadata contextMetadata) {
        log.info("Processing context: {}", contextMetadata.getContext());
        final List<BeanMetadata> beans = contextMetadata.getBeans();

        final List<BeanNode> beanNodes = createNodes(contextMetadata, beans);

        createRelations(contextMetadata, beans, beanNodes);
        createLabels();
    }

    private List<BeanNode> createNodes(ContextMetadata contextMetadata, List<BeanMetadata> beans) {
        final List<BeanNode> beanNodes = beans.stream()
                .map(bean -> transformToNode(bean, contextMetadata))
                .collect(Collectors.toList());
        graph.createNodes(beanNodes);
        return beanNodes;
    }

    private void createRelations(ContextMetadata contextMetadata, List<BeanMetadata> beans, List<BeanNode> beanNodes) {
        log.info("Processing dependencies of context: {}", contextMetadata.getContext());

        final List<DependsOnRelation> dependencies = beans.stream()
                .filter(bean -> bean.getDependencies() != null && !bean.getDependencies().isEmpty())
                .map(bean -> createRelationships(bean, beanNodes))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        graph.createNodeRelations(dependencies);
    }

    private void createLabels() {
        log.info("Adding labels");

        final Optional<LabelingRules> labelingRules = config.getLabelingRules();

        labelingRules.map(LabelingRules::getType).ifPresent(labels -> createLabelsByRegexp("type", labels));
        labelingRules.map(LabelingRules::getName).ifPresent(labels -> createLabelsByRegexp("name", labels));
        labelingRules.map(LabelingRules::getScope).ifPresent(labels -> createLabelsByRegexp("scope", labels));
    }

    private void createLabelsByRegexp(final String field, final Map<String, String> labels) {
        labels.forEach((label, regexp) -> graph.addLabels(new RegexpFieldCondition(field, regexp), label));
    }

    private List<DependsOnRelation> createRelationships(BeanMetadata bean, List<BeanNode> allNodes) throws RuntimeException {
        final BeanNode startNode = findNode(bean.getBean(), allNodes);

        return bean.getDependencies().stream()
                .map(dependency -> findNode(dependency, allNodes))
                .filter(Objects::nonNull)
                .map(dependency -> DependsOnRelation.builder()
                        .bean(startNode)
                        .dependency(dependency)
                        .build())
                .collect(Collectors.toList());
    }

    private BeanNode findNode(String name, List<BeanNode> allNodes){
        return allNodes.stream()
                .filter(node -> node.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private BeanNode transformToNode(BeanMetadata bean, ContextMetadata context) {
        log.info("Processing model: {}", bean.getBean());

        return BeanNode.builder()
                .name(bean.getBean())
                .scope(bean.getScope())
                .type(bean.getType())
                .build();
    }
}
