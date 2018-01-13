package com.lmonkiewicz.spring.analyzer.app;

import com.lmonkiewicz.spring.analyzer.domain.properties.AnalyzerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AnalyzerProperties.class)
public class AnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyzerApplication.class, args);
	}
}