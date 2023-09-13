package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.NodeTagMapper;
import com.wa.cluemrg.entity.NodeTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeTagService {

    @Autowired
    private NodeTagMapper nodeTagMapper;

    public List<NodeTag> selectAll(NodeTag nodeTag) {
        return nodeTagMapper.selectAll(nodeTag);
    }

    public List<NodeTag> selectAllByKeyList(List keyList) {
        return nodeTagMapper.selectAllByKeyList(keyList);
    }

    public int insert(NodeTag nodeTag) {
        return nodeTagMapper.insert(nodeTag);
    }

    public int update(NodeTag nodeTag) {
        return nodeTagMapper.update(nodeTag);
    }

    public int delete(int id) {
        return nodeTagMapper.delete(id);
    }
    
}
