package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.config.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.config.RulesProperties;
import com.lmonkiewicz.spring.analyzer.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.BeanMetadata;
import com.lmonkiewicz.spring.analyzer.metadata.ContextMetadata;
import com.lmonkiewicz.spring.analyzer.neo4j.BeanRepository;
import com.lmonkiewicz.spring.analyzer.neo4j.DependsOnRepository;
import com.lmonkiewicz.spring.analyzer.neo4j.model.BeanNode;
import com.lmonkiewicz.spring.analyzer.neo4j.model.DependsOnRelation;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.Session;
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
    private final Session session;

    @Autowired
    public AnalyzerService(MetadataProvider metadataProvider,
                           BeanRepository beanRepository,
                           DependsOnRepository dependsOnRepository,
                           AnalyzerProperties analyzerProperties,
                           Session session) {
        this.metadataProvider = metadataProvider;
        this.beanRepository = beanRepository;
        this.dependsOnRepository = dependsOnRepository;
        this.analyzerProperties = analyzerProperties;
        this.session = session;
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

        addLabels();
    }

    private void addLabels() {
        log.info("Adding labels");
        final Map<String, String> labels = Optional.ofNullable(analyzerProperties.getRules()).map(RulesProperties::getLabels).orElse(new HashMap<>());


        labels.forEach((label, regexp) -> {
            final String query = "MATCH (n) WHERE n.type =~ '"+regexp+"' SET n :"+label;
            session.query(query, new HashMap<>());
        });
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
