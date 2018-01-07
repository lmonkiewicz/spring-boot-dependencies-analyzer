package com.lmonkiewicz.spring.analyzer.neo4j;


import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface BeanRepository extends Neo4jRepository<BeanNode, Long> {

}
