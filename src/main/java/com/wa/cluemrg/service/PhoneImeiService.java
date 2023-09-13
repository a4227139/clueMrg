package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.entity.PhoneImei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneImeiService {

    @Autowired
    private PhoneImeiMapper phoneImeiMapper;

    public List<PhoneImei> selectAll(PhoneImei phoneImei) {
        return phoneImeiMapper.selectAll(phoneImei);
    }

    public int insert(PhoneImei phoneImei) {
        return phoneImeiMapper.insert(phoneImei);
    }

    public int update(PhoneImei phoneImei) {
        return phoneImeiMapper.update(phoneImei);
    }

    public int delete(int id) {
        return phoneImeiMapper.delete(id);
    }
    
}
