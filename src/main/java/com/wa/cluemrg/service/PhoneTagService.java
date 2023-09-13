package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.PhoneTagMapper;
import com.wa.cluemrg.entity.PhoneTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneTagService {

    @Autowired
    private PhoneTagMapper phoneTagMapper;

    public List<PhoneTag> selectAll(PhoneTag phoneTag) {
        return phoneTagMapper.selectAll(phoneTag);
    }

    public List<PhoneTag> selectAllByKeyList(List keyList) {
        return phoneTagMapper.selectAllByKeyList(keyList);
    }

    public int insert(PhoneTag phoneTag) {
        return phoneTagMapper.insert(phoneTag);
    }

    public int update(PhoneTag phoneTag) {
        return phoneTagMapper.update(phoneTag);
    }

    public int delete(int id) {
        return phoneTagMapper.delete(id);
    }
    
}
