package com.lmonkiewicz.spring.analyzer.domain.dto.metadata;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationMetadata {
    @Singular
    private List<ContextMetadata> contexts;
}
