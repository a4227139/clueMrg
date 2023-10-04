package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.AttributionMapper;
import com.wa.cluemrg.entity.Attribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributionService {

    @Autowired
    private AttributionMapper attributionMapper;

    public List<Attribution> selectAll(Attribution attribution) {
        return attributionMapper.selectAll(attribution);
    }

    public int insert(Attribution attribution) {
        return attributionMapper.insert(attribution);
    }

    public int update(Attribution attribution) {
        return attributionMapper.update(attribution);
    }

    public int delete(int id) {
        return attributionMapper.delete(id);
    }
    
}
