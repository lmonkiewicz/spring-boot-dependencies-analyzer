package com.lmonkiewicz.spring.analyzer.config;

import lombok.Data;

@Data
public class LabelsProperties {
    private String repository = ".*Repository";
    private String controller = ".*Controller";
    private String service = ".*Service";
    private String configuration = ".*Configuration";
}
