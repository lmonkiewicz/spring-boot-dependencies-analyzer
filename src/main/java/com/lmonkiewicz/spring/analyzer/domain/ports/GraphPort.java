package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.BeanNode;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.DependsOnRelation;
import com.lmonkiewicz.spring.analyzer.domain.condition.Condition;

import java.util.List;

public interface GraphPort {
    void clear();

    void createNodes(List<BeanNode> nodes);

    void createNodeRelations(List<DependsOnRelation> relations);

    void addLabels(Condition condition, String label);
}
