package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.domain.LabelingRules;

import java.util.Optional;

public interface ConfigurationPort {
    boolean isClearGraphOnStartup();

    Optional<LabelingRules> getLabelingRules();
}
