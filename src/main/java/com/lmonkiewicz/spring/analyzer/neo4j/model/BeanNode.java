package com.lmonkiewicz.spring.analyzer.neo4j.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Data
@NodeEntity(label = "BEAN")
@Builder
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
}
