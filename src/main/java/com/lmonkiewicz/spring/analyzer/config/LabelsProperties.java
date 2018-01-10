package com.lmonkiewicz.spring.analyzer.config;

import lombok.Data;

import java.util.Map;

@Data
public class LabelsProperties {
    private Map<String,String> type;
    private Map<String,String> name;
    private Map<String,String> scope;
}
