package com.lmonkiewicz.spring.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("analyzer")
public class AnalyzerProperties {

    private SourceProperties source;
    private RulesProperties rules;
}
