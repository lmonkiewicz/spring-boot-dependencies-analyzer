package com.lmonkiewicz.spring.analyzer.domain.properties;

import lombok.Data;

import java.util.Map;

@Data
public class RulesProperties {
    private Map<String, String> tags;
    private LabelsProperties labels;
}
