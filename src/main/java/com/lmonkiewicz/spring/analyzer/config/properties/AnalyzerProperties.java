package com.lmonkiewicz.spring.analyzer.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("analyzer")
public class AnalyzerProperties {
    private boolean clearOnStart = false;
    private SourceProperties source;
    private RulesProperties rules;
}
