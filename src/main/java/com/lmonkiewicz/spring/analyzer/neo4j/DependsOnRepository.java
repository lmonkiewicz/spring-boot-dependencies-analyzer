package com.lmonkiewicz.spring.analyzer.neo4j;

import com.lmonkiewicz.spring.analyzer.neo4j.model.DependsOnRelation;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DependsOnRepository extends Neo4jRepository<DependsOnRelation, Long> {
}