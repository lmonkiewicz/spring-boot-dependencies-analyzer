package com.lmonkiewicz.spring.analyzer.domain.dto.condition;

import lombok.Value;

@Value
public class RegexpFieldCondition implements Condition {

    private final String field;
    private final String regexp;

    public RegexpFieldCondition(String field, String regexp) {
        this.field = field;
        this.regexp = regexp;
    }
}
