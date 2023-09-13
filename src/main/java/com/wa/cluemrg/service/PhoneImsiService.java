package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.PhoneImsi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneImsiService {

    @Autowired
    private PhoneImsiMapper phoneImsiMapper;

    public List<PhoneImsi> selectAll(PhoneImsi phoneImsi) {
        return phoneImsiMapper.selectAll(phoneImsi);
    }

    public int insert(PhoneImsi phoneImsi) {
        return phoneImsiMapper.insert(phoneImsi);
    }

    public int update(PhoneImsi phoneImsi) {
        return phoneImsiMapper.update(phoneImsi);
    }

    public int delete(int id) {
        return phoneImsiMapper.delete(id);
    }
    
}
