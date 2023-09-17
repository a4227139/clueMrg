package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.CaseMapper;
import com.wa.cluemrg.entity.Case;
import com.wa.cluemrg.entity.SimpleIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseService {

    @Autowired
    private CaseMapper caseMapper;

    public Case select(String clueId) {
        return caseMapper.select(clueId);
    }

    public List<Case> selectAll(Case case1) {
        return caseMapper.selectAll(case1);
    }

    public List<Case> selectAllSolve(Case case1) {
        return caseMapper.selectAllSolve(case1);
    }

    public List<Case> selectAllHistory(Case case1) {
        return caseMapper.selectAllHistory(case1);
    }

    public int insert(Case case1) {
        return caseMapper.insert(case1);
    }

    public int update(Case case1) {
        return caseMapper.update(case1);
    }

    public int delete(String clueId) {
        return caseMapper.delete(clueId);
    }

    public String getLatestDay() {
        return caseMapper.getLatestCase();
    }

    public int batchInsert(List<Case> list) {
        return caseMapper.batchInsert(list);
    }

    public int batchInsertOrUpdate(List<Case> list) {
        return caseMapper.batchInsertOrUpdate(list);
    }

    public int batchInsertOrUpdateSolve(List<Case> list) {
        return caseMapper.batchInsertOrUpdateSolve(list);
    }

    public int batchDelete(List<String> list) {
        return caseMapper.batchDelete(list);
    }
    public int batchDeleteSolve(List<String> list) {
        return caseMapper.batchDeleteSolve(list);
    }

    public List<SimpleIndex> getCaseCountByDate(Case caseObj) {
        return caseMapper.getCaseCountByDate(caseObj);
    }

    public List<SimpleIndex> getCaseSolveCountByDate(Case caseObj) {
        return caseMapper.getCaseSolveCountByDate(caseObj);
    }
}
