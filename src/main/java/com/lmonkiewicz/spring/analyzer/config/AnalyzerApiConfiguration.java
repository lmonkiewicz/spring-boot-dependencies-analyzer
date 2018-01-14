package com.lmonkiewicz.spring.analyzer.config;

import com.lmonkiewicz.spring.analyzer.adapter.configuration.PropertiesConfigurationAdapter;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.BeanRepository;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.Neo4jGraphPortAdapter;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.domain.AnalyzerAPI;
import com.lmonkiewicz.spring.analyzer.domain.ports.ConfigurationPort;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import org.neo4j.ogm.session.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyzerApiConfiguration {

    @Bean
    GraphPort graphPort(Session session, BeanRepository beanRepository) {
        return new Neo4jGraphPortAdapter(beanRepository, session);
    }

    @Bean
    ConfigurationPort configurationPort(AnalyzerProperties analyzerProperties) {
        return new PropertiesConfigurationAdapter(analyzerProperties);
    }

    @Bean
    AnalyzerAPI analyzerApi(ConfigurationPort configurationPort, GraphPort graphPort) {
        return new AnalyzerAPI(graphPort, configurationPort);
    }
}
