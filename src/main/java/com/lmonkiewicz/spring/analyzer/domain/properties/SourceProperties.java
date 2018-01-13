package com.lmonkiewicz.spring.analyzer.domain.properties;

import lombok.Data;

@Data
public class SourceProperties {
    private String resource;
    private String file;
    private String url;
}
