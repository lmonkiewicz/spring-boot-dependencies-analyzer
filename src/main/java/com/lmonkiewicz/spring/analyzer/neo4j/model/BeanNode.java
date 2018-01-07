package com.lmonkiewicz.spring.analyzer.neo4j.model;

import lombok.Data;
import lombok.Singular;
import org.neo4j.ogm.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@NodeEntity(label = "BEAN")
public class BeanNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String type;
    private String scope;

    @Relationship(type = "DEPENDS_ON")
    @Singular
    private Set<BeanNode> dependencies;

    @Properties
    private Map<String, Boolean> tags = new HashMap<>();
}
