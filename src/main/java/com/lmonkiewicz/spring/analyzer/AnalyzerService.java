package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.config.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.config.LabelsProperties;
import com.lmonkiewicz.spring.analyzer.config.RulesProperties;
import com.lmonkiewicz.spring.analyzer.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.neo4j.BeanRepository;
import com.lmonkiewicz.spring.analyzer.neo4j.DependsOnRepository;
import com.lmonkiewicz.spring.analyzer.neo4j.model.*;
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

        if (analyzerProperties.isClearOnStart()) {
            log.info("Clearing database");
            dependsOnRepository.deleteAll();
            beanRepository.deleteAll();
        }

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

        final List<DependsOnRelation> dependencies = beans.stream()
                .filter(bean -> bean.getDependencies() != null && !bean.getDependencies().isEmpty())
                .map(bean -> createRelationships(bean, beanNodes))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        dependsOnRepository.saveAll(dependencies);

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

        final BeanNode node = createNode(bean);
        node.setName(bean.getBean());
        node.setScope(bean.getScope());
        node.setType(bean.getType());
        node.setTags(createTags(bean));

        return node;
    }

    private BeanNode createNode(BeanMetadata bean) {
        final LabelsProperties labelsProperties = Optional.ofNullable(analyzerProperties.getRules()).map(RulesProperties::getLabels).orElseGet(LabelsProperties::new);

        final String type = bean.getType();
        if (type.matches(labelsProperties.getConfiguration())) {
            return new ConfigurationNode();
        }
        else if (type.matches(labelsProperties.getService())) {
            return new ServiceNode();
        }
        else if (type.matches(labelsProperties.getController())) {
            return new ControllerNode();
        }
        else if (type.matches(labelsProperties.getRepository())) {
            return new RepositoryNode();
        }
        else {
            return new BeanNode();
        }
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

}
