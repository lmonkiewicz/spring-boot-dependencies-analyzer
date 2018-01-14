package com.lmonkiewicz.spring.analyzer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmonkiewicz.spring.analyzer.adapter.metadata.DefaultMetadataProvider;
import com.lmonkiewicz.spring.analyzer.adapter.metadata.MetadataProvider;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetadataConfiguration {

    @Bean
    MetadataProvider metadataProvider(AnalyzerProperties analyzerProperties, ObjectMapper objectMapper){
        return new DefaultMetadataProvider(analyzerProperties, objectMapper);
    }
}
