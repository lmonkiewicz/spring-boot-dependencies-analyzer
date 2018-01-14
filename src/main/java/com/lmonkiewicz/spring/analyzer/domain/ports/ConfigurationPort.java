package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.domain.dto.configuration.LabelingRules;

import java.util.Optional;

public interface ConfigurationPort {
    /**
     * Returns labeling rules for graph nodes.
     *
     * Each node can have assigned multiple labels, according to regexp expression on their name, type, scope or context.
     *
     * @return labeling rules if configured
     */
    Optional<LabelingRules> getLabelingRules();
}
