package com.lmonkiewicz.spring.analyzer.domain;

import com.lmonkiewicz.spring.analyzer.domain.dto.condition.RegexpFieldCondition;
import com.lmonkiewicz.spring.analyzer.domain.dto.configuration.LabelingRules;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.BeanDTO;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.DependencyDTO;
import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class LoaderService {

    private final ConfigurationPort config;
    private final GraphPort graph;

    public LoaderService(ConfigurationPort config, GraphPort graph) {
        this.config = config;
        this.graph = graph;
    }

    public void clearGraph() {
        log.info("Clearing graph");
        graph.clear();
    }

    public void loadIntoGraph(ApplicationMetadata applicationMetadata, boolean clearGraphBeforeLoad) {
        if (clearGraphBeforeLoad) {
            clearGraph();
        }
        log.info("Populating graph...");
        applicationMetadata.getContexts().forEach(this::processContext);
    }

    private void processContext(ContextMetadata contextMetadata) {
        log.info("Processing context: {}", contextMetadata.getContext());
        final List<BeanMetadata> beans = contextMetadata.getBeans();

        final List<BeanDTO> beanNodes = createNodes(contextMetadata, beans);

        createRelations(contextMetadata, beans, beanNodes);
        createLabels();
    }

    private List<BeanDTO> createNodes(ContextMetadata contextMetadata, List<BeanMetadata> beans) {
        final List<BeanDTO> beanNodes = beans.stream()
                .map(bean -> transformToBeanDTO(bean, contextMetadata))
                .collect(Collectors.toList());
        return graph.createNodes(beanNodes);
    }

    private void createRelations(ContextMetadata contextMetadata, List<BeanMetadata> beans, List<BeanDTO> beanNodes) {
        log.info("Processing dependencies of context: {}", contextMetadata.getContext());

        final List<DependencyDTO> dependencies = beans.stream()
                .map(this::createRelationships)
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
        labelingRules.map(LabelingRules::getContext).ifPresent(labels -> createLabelsByRegexp("context", labels));
    }

    private void createLabelsByRegexp(final String field, final Map<String, String> labels) {
        labels.forEach((label, regexp) -> graph.addLabels(new RegexpFieldCondition(field, regexp), label));
    }

    private List<DependencyDTO> createRelationships(BeanMetadata bean) {
        return Optional.ofNullable(bean.getDependencies())
                .map(Collection::stream).orElseGet(Stream::empty)
                .map(dependency -> DependencyDTO.builder()
                        .bean(bean.getBean())
                        .dependsOn(dependency)
                        .build())
                .collect(Collectors.toList());
    }

    private BeanDTO transformToBeanDTO(BeanMetadata bean, ContextMetadata context) {
        if (log.isTraceEnabled()) {
            log.trace("Processing model: {}", bean.getBean());
        }

        return BeanDTO.builder()
                .name(bean.getBean())
                .scope(bean.getScope())
                .type(bean.getType())
                .context(context.getContext())
                .build();
    }
}
