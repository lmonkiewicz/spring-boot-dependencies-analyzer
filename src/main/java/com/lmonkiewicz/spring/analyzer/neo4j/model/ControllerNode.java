package com.lmonkiewicz.spring.analyzer.neo4j.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "CONTROLLER")
public class ControllerNode extends BeanNode {
}