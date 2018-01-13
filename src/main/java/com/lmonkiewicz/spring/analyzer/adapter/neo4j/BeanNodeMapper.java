package com.lmonkiewicz.spring.analyzer.adapter.neo4j;

import com.lmonkiewicz.spring.analyzer.domain.dto.graph.BeanDTO;

class BeanNodeMapper {

    public BeanNode from(BeanDTO dto){
        return BeanNode.builder()
                .id(dto.getId())
                .name(dto.getName())
                .scope(dto.getScope())
                .type(dto.getType())
                .context(dto.getContext())
                .build();
    }

    public BeanDTO to(BeanNode node) {
        return BeanDTO.builder()
                .id(node.getId())
                .name(node.getName())
                .scope(node.getScope())
                .type(node.getType())
                .context(node.getContext())
                .build();
    }
}
