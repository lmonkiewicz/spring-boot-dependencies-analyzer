package com.lmonkiewicz.spring.analyzer.domain.dto.configuration;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class LabelingRules {
    private final Map<String,String> type;
    private final Map<String,String> name;
    private final Map<String,String> scope;
}
