package com.lmonkiewicz.spring.analyzer.neo4j;

import lombok.*;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.Properties;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
