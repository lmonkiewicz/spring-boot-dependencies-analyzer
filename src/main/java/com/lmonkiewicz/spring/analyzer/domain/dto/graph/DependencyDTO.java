package com.lmonkiewicz.spring.analyzer.domain.dto.graph;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DependencyDTO {
    private final String bean;
    private final String dependsOn;
}
