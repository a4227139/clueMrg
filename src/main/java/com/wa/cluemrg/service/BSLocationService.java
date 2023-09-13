package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.BSLocationMapper;
import com.wa.cluemrg.entity.BSLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BSLocationService {

    @Autowired
    private BSLocationMapper bsLocationMapper;

    public List<BSLocation> selectAll(BSLocation bsLocation) {
        return bsLocationMapper.selectAll(bsLocation);
    }

    public int insert(BSLocation bsLocation) {
        return bsLocationMapper.insert(bsLocation);
    }

    public int update(BSLocation bsLocation) {
        return bsLocationMapper.update(bsLocation);
    }

    public int delete(String bsLocationId) {
        return bsLocationMapper.delete(bsLocationId);
    }

}
