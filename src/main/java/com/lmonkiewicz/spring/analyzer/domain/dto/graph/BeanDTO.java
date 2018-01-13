package com.lmonkiewicz.spring.analyzer.domain.dto.graph;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BeanDTO {
    private final Long id;
    private final String name;
    private final String type;
    private final String scope;
    private final String context;
}
