package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.domain.dto.configuration.LabelingRules;

import java.util.Optional;

public interface ConfigurationPort {
    boolean isClearGraphOnStartup();

    Optional<LabelingRules> getLabelingRules();
}
