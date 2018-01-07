package com.lmonkiewicz.spring.analyzer.config;

import lombok.Data;

import java.util.Map;

@Data
public class RulesProperties {
    private Map<String, String> tags;
}
