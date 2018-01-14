package com.lmonkiewicz.spring.analyzer.adapter.configuration;

import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.config.properties.LabelsProperties;
import com.lmonkiewicz.spring.analyzer.config.properties.RulesProperties;
import com.lmonkiewicz.spring.analyzer.domain.dto.configuration.LabelingRules;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PropertiesConfigurationAdapter implements ConfigurationPort {

    private final AnalyzerProperties analyzerProperties;

    public PropertiesConfigurationAdapter(AnalyzerProperties analyzerProperties) {
        this.analyzerProperties = analyzerProperties;
    }

    @Override
    public Optional<LabelingRules> getLabelingRules() {
        return Optional.ofNullable(analyzerProperties.getRules())
                .map(RulesProperties::getLabels)
                .map(this::convertToLabelingRules);
    }

    private LabelingRules convertToLabelingRules(LabelsProperties labelsProperties) {
        return LabelingRules.builder()
                .name(makeMapCopy(labelsProperties.getName()))
                .scope(makeMapCopy(labelsProperties.getScope()))
                .type(makeMapCopy(labelsProperties.getType()))
                .build();
    }

    private Map<String, String> makeMapCopy(Map<String, String> input) {
        return Optional.ofNullable(input)
                .map(HashMap::new)
                .orElseGet(HashMap::new);
    }
}
