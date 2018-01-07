package com.lmonkiewicz.spring.analyzer.metadata;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextMetadata {
    private String context;
    private String parent;
    @Singular
    private List<BeanMetadata> beans;
}
