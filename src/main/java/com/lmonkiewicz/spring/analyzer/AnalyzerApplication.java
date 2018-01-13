package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.config.AnalyzerApiConfiguration;
import com.lmonkiewicz.spring.analyzer.config.JacksonConfiguration;
import com.lmonkiewicz.spring.analyzer.config.Neo4jConfiguration;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties(AnalyzerProperties.class)
@EnableAutoConfiguration
@Import({
		AnalyzerApiConfiguration.class,
		JacksonConfiguration.class,
		Neo4jConfiguration.class})
public class AnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyzerApplication.class, args);
	}
}
