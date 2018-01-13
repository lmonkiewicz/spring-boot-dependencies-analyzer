package com.lmonkiewicz.spring.analyzer.domain.ports;

import com.lmonkiewicz.spring.analyzer.domain.dto.condition.Condition;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.BeanDTO;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.DependencyDTO;

import java.util.List;

public interface GraphPort {
    void clear();

    List<BeanDTO> createNodes(List<BeanDTO> nodes);

    void createNodeRelations(List<DependencyDTO> relations);

    void addLabels(Condition condition, String label);
}
