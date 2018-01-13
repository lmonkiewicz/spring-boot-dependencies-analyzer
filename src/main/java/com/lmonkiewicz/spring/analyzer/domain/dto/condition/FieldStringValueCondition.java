package com.lmonkiewicz.spring.analyzer.domain.dto.condition;

import lombok.Value;

@Value
public class FieldStringValueCondition implements Condition {
    private final String field;
    private final String value;
}
