package com.lmonkiewicz.spring.analyzer.config;

import com.lmonkiewicz.spring.analyzer.adapter.neo4j.BeanRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = BeanRepository.class)
public class Neo4jConfiguration {
}
