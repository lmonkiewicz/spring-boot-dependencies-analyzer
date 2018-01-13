package com.lmonkiewicz.spring.analyzer.adapter.neo4j;

import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.BeanNode;
import com.lmonkiewicz.spring.analyzer.adapter.neo4j.model.DependsOnRelation;
import com.lmonkiewicz.spring.analyzer.domain.condition.Condition;
import com.lmonkiewicz.spring.analyzer.domain.condition.RegexpFieldCondition;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.Session;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class Neo4jGraphPortAdapter implements GraphPort {

    private final BeanRepository beanRepository;
    private final DependsOnRepository dependsOnRepository;
    private final Session session;

    public Neo4jGraphPortAdapter(BeanRepository beanRepository, DependsOnRepository dependsOnRepository, Session session) {
        this.beanRepository = beanRepository;
        this.dependsOnRepository = dependsOnRepository;
        this.session = session;
    }

    @Override
    public void clear() {
        dependsOnRepository.deleteAll();
        beanRepository.deleteAll();
    }

    @Override
    public void createNodes(List<BeanNode> nodes) {
        beanRepository.saveAll(nodes);
    }

    @Override
    public void createNodeRelations(List<DependsOnRelation> relations) {
        dependsOnRepository.saveAll(relations);
    }

    @Override
    public void addLabels(Condition condition, String label) {
        final String nodeName = "n";

        final String query = String.join(" ", createConditionWhereClause(condition, nodeName), createSetLabelClause(nodeName, label));

        log.info("Setting labels with query: {}", query);
        session.query(query, new HashMap<>());
    }

    private String createSetLabelClause(String nodeName, String label) {
        return String.format("SET %s :%s", nodeName, label);
    }


    private String createConditionWhereClause(Condition condition, String nodeName) {
        if (condition instanceof RegexpFieldCondition) {
            final RegexpFieldCondition regexpFieldCondition = (RegexpFieldCondition) condition;
            return String.format("MATCH (%s) WHERE %s.%s =~ '%s'", nodeName, nodeName, regexpFieldCondition.getField(), regexpFieldCondition.getRegexp());
        }
        else {
            return String.format("MATCH (%s)", nodeName);
        }
    }


}
