package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.LinkTagMapper;
import com.wa.cluemrg.entity.LinkTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LinkTagService {

    @Autowired
    private LinkTagMapper linkTagMapper;

    public List<LinkTag> selectAll(LinkTag linkTag) {
        return linkTagMapper.selectAll(linkTag);
    }

    public List<LinkTag> selectAllByLinkList(List linkList) {
        if (linkList.size()<2){
            return new ArrayList<>();
        }
        return linkTagMapper.selectAllByLinkList(linkList);
    }

    public int insert(LinkTag linkTag) {
        return linkTagMapper.insert(linkTag);
    }

    public int update(LinkTag linkTag) {
        return linkTagMapper.update(linkTag);
    }

    public int delete(int id) {
        return linkTagMapper.delete(id);
    }
    
}
