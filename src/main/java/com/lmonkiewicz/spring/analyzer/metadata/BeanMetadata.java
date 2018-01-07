package com.lmonkiewicz.spring.analyzer.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeanMetadata {
    private String bean;
    private List<String> aliases;
    private String scope;
    private String type;
    private String resource;
    private List<String> dependencies;
}
