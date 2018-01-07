package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.config.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.config.RulesProperties;
import com.lmonkiewicz.spring.analyzer.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.neo4j.BeanNode;
import com.lmonkiewicz.spring.analyzer.neo4j.BeanRepository;
import com.lmonkiewicz.spring.analyzer.neo4j.DependsOn;
import com.lmonkiewicz.spring.analyzer.neo4j.DependsOnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
class AnalyzerService {

    private final MetadataProvider metadataProvider;
    private final BeanRepository beanRepository;
    private final DependsOnRepository dependsOnRepository;
    private final AnalyzerProperties analyzerProperties;

    @Autowired
    public AnalyzerService(MetadataProvider metadataProvider,
                           BeanRepository beanRepository,
                           DependsOnRepository dependsOnRepository,
                           AnalyzerProperties analyzerProperties) {
        this.metadataProvider = metadataProvider;
        this.beanRepository = beanRepository;
        this.dependsOnRepository = dependsOnRepository;
        this.analyzerProperties = analyzerProperties;
    }

    public void processData() throws IOException {
        log.info("Loading beans data");
        ApplicationMetadata applicationMetadata = metadataProvider.getApplicationInfo();

        log.info("Populating database...");
        applicationMetadata.getContexts().forEach(this::processContext);

        log.info("Done");
    }

    private void processContext(ContextMetadata contextMetadata) {
        log.info("Processing context: {}", contextMetadata.getContext());
        final List<BeanMetadata> beans = contextMetadata.getBeans();

        final List<BeanNode> beanNodes = beans.stream()
                .map(bean -> transformToNode(bean, contextMetadata))
                .collect(Collectors.toList());

        beanRepository.saveAll(beanNodes);

        log.info("Processing dependencies of context: {}", contextMetadata.getContext());

        final List<DependsOn> dependencies = beans.stream()
                .filter(bean -> bean.getDependencies() != null && !bean.getDependencies().isEmpty())
                .map(bean -> createRelationships(bean, beanNodes))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        dependsOnRepository.saveAll(dependencies);

    }

    private List<DependsOn> createRelationships(BeanMetadata bean, List<BeanNode> allNodes) throws RuntimeException {
        final BeanNode startNode = findNode(bean.getBean(), allNodes);

        return bean.getDependencies().stream()
                .map(dependency -> findNode(dependency, allNodes))
                .filter(Objects::nonNull)
                .map(dependency -> DependsOn.builder()
                        .bean(startNode)
                        .dependency(dependency)
                        .build())
                .collect(Collectors.toList());
    }

    private BeanNode findNode(String name, List<BeanNode> allNodes){
        return allNodes.stream()
                .filter(node -> node.getName().equals(name))
                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Bean not found: " + name));
                .orElse(null);
    }

    private BeanNode transformToNode(BeanMetadata bean, ContextMetadata context) {
        log.info("Processing bean: {}", bean.getBean());

        return BeanNode.builder()
                .name(bean.getBean())
                .scope(bean.getScope())
                .type(bean.getType())
                .tags(createTags(bean))
//                .labels(createLabels(bean))
                .build();
    }

    private Map<String, Boolean> createTags(BeanMetadata bean) {
        final Map<String, Boolean> beanTags = new HashMap<>();

        final RulesProperties rulesProperties = Optional.ofNullable(analyzerProperties.getRules()).orElseGet(RulesProperties::new);

        final Map<String, String> tags = rulesProperties.getTags();
        if (tags != null) {
            tags.forEach((tagName, regexp) -> beanTags.put(tagName, bean.getType().matches(regexp)));
        }
        return beanTags;
    }

    private List<String> createLabels(BeanMetadata bean) {
        final List<String> labels = new ArrayList<>();

        Optional.ofNullable(bean.getScope()).map(scope -> "SCOPE_"+scope.toUpperCase()).ifPresent(labels::add);


        return labels;
    }

    private void processBean(BeanMetadata bean, ContextMetadata contextMetadata) {
        log.info("Processing bean: {}", bean.getBean());
        final BeanNode node = BeanNode.builder()
                .name(bean.getBean())
                .scope(bean.getScope())
                .type(bean.getType())
                .build();

        beanRepository.save(node);
    }
}
