package com.lmonkiewicz.spring.analyzer.adapter.neo4j;

import com.lmonkiewicz.spring.analyzer.domain.dto.condition.Condition;
import com.lmonkiewicz.spring.analyzer.domain.dto.condition.FieldStringValueCondition;
import com.lmonkiewicz.spring.analyzer.domain.dto.condition.RegexpFieldCondition;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.BeanDTO;
import com.lmonkiewicz.spring.analyzer.domain.dto.graph.DependencyDTO;
import com.lmonkiewicz.spring.analyzer.domain.ports.GraphPort;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.Session;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class Neo4jGraphPortAdapter implements GraphPort {

    private final String DEFAULT_NODE_NAME = "n";
    private final String BEAN_NAME_FIELD = "name";


    private final BeanRepository beanRepository;
    private final Session session;
    private final BeanNodeMapper beanNodeMapper;
    private final String REL_DEPENDS_ON = "DEPENDS_ON";

    public Neo4jGraphPortAdapter(BeanRepository beanRepository, Session session) {
        this.beanRepository = beanRepository;
        this.session = session;
        this.beanNodeMapper = new BeanNodeMapper();
    }

    @Override
    public void clear() {
        session.query("MATCH (n)-[r]->(m) DELETE r, n, m", Collections.emptyMap());
        session.query("MATCH (n) DELETE n", Collections.emptyMap());
    }

    @Override
    public List<BeanDTO> createNodes(List<BeanDTO> nodes) {
        final List<BeanNode> beanNodes = nodes.stream()
                .map(beanNodeMapper::from)
                .collect(Collectors.toList());

        final Iterable<BeanNode> saved = beanRepository.saveAll(beanNodes);

        return StreamSupport.stream(saved.spliterator(), false)
                .map(beanNodeMapper::to)
                .collect(Collectors.toList());
    }

    @Override
    public void createNodeRelations(List<DependencyDTO> relations) {
        relations.forEach(relation -> {
            final String FIRST_NODE = DEFAULT_NODE_NAME;
            final String SECOND_NODE = "m";

            final String query = String.join(" ",
                    createConditionWhereClause(new FieldStringValueCondition(BEAN_NAME_FIELD, relation.getBean()), FIRST_NODE),
                    createConditionWhereClause(new FieldStringValueCondition(BEAN_NAME_FIELD, relation.getDependsOn()), SECOND_NODE),
                    createCreateRelationClause(FIRST_NODE, REL_DEPENDS_ON, SECOND_NODE));

            session.query(query, Collections.emptyMap());
        });

    }

    private String createCreateRelationClause(String firstNode, String relation, String secondNode) {
        return String.format("CREATE (%s)-[:%s]->(%s)", firstNode, relation, secondNode);
    }

    @Override
    public void addLabels(Condition condition, String label) {
        final String query = String.join(" ", createConditionWhereClause(condition, DEFAULT_NODE_NAME), createSetLabelClause(DEFAULT_NODE_NAME, label));

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
        else if (condition instanceof FieldStringValueCondition) {
            final FieldStringValueCondition stringValueCondition = (FieldStringValueCondition) condition;
            return String.format("MATCH (%s) WHERE %s.%s = '%s'", nodeName, nodeName, stringValueCondition.getField(), stringValueCondition.getValue());
        }
        else {
            return String.format("MATCH (%s)", nodeName);
        }
    }


}
