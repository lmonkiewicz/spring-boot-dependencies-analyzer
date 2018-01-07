package com.lmonkiewicz.spring.analyzer.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "DEPENDS_ON")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependsOn {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private BeanNode bean;

    @EndNode
    private BeanNode dependency;
}
