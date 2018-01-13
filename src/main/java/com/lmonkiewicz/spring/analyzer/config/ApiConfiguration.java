package com.lmonkiewicz.spring.analyzer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmonkiewicz.spring.analyzer.adapter.configuration.PropertiesConfigurationAdapter;
import com.lmonkiewicz.spring.analyzer.adapter.metadata.DefaultMetadataProviderAdapter;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.BeanRepository;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.DependsOnRepository;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.Neo4jGraphPortAdapter;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.domain.AnalyzerAPI;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.MetadataProviderPort;
import org.neo4j.ogm.session.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfiguration {

    @Bean
    MetadataProviderPort metadataProviderPort(AnalyzerProperties analyzerProperties, ObjectMapper objectMapper){
        return new DefaultMetadataProviderAdapter(analyzerProperties, objectMapper);
    }

    @Bean
    GraphPort graphPort(Session session, DependsOnRepository dependsOnRepository, BeanRepository beanRepository) {
        return new Neo4jGraphPortAdapter(beanRepository, dependsOnRepository, session);
    }

    @Bean
    ConfigurationPort configurationPort(AnalyzerProperties analyzerProperties) {
        return new PropertiesConfigurationAdapter(analyzerProperties);
    }

    @Bean
    AnalyzerAPI analyzerApi(MetadataProviderPort metadataProviderPort, AnalyzerProperties analyzerProperties, GraphPort graphPort) {
        return new AnalyzerAPI(graphPort, analyzerProperties, metadataProviderPort);
    }
}
