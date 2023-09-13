package com.wa.cluemrg.controller;

import com.wa.cluemrg.entity.Link;
import com.wa.cluemrg.entity.Node;
import com.wa.cluemrg.entity.PhoneImei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wa.cluemrg.service.PhoneImeiService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/graph")
public class GraphController {

    /*@Autowired
    PhoneImeiService phoneImeiService;

    @GetMapping("/getNode")
    public List<Node> getNode(@RequestParam String phone){
        Set<String> nodeSet = new HashSet<>();
        Set<String> linkSet = new HashSet<>();
        List<Node> nodeList = new ArrayList<>();
        List<Link> linkList = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        paramList.add(phone);
        nodeSet.add(phone);
        Node node = new Node();
        node.setName(phone);
        node.setLevel(1);
        nodeList.add(node);
        getAllNode(paramList,nodeSet,linkSet,nodeList,linkList,2);
        return nodeList;
    }

    private void getAllNode(List<String> paramList, Set<String> nodeSet, Set<String> linkSet, List<Node> nodeList, List<Link> linkList,int level) {
        List<String> phoneList=new ArrayList<>();
        List<String> imeiList=new ArrayList<>();
        List<String> imsiList=new ArrayList<>();
        List<String> clueList=new ArrayList<>();
        List<String> suspectList=new ArrayList<>();
        splitParam(paramList,phoneList,imeiList,imsiList,clueList,suspectList);
        paramList.clear();
        if (!CollectionUtils.isEmpty(phoneList)){
            List<PhoneImei> phoneImeiList = phoneImeiService.selectImeiByPhone(phoneList);
            for (PhoneImei phoneImei: phoneImeiList){
                String imei = phoneImei.getImei();
                if (!nodeSet.contains(imei)){
                    Node node = new Node(imei,level);
                    String linkString=phoneImei.getPhone()+":"+phoneImei.getImei();
                    Link link = new Link(phoneImei.getPhone(),phoneImei.getImei());
                    nodeSet.add(imei);
                    nodeList.add(node);
                    linkSet.add(linkString);
                    linkList.add(link);
                    paramList.add(imei);
                }
            }
            getAllNode(paramList,nodeSet,linkSet,nodeList,linkList,level+1);
        }

        if (!CollectionUtils.isEmpty(imeiList)){
            List<PhoneImei> phoneImeiList = phoneImeiService.selectPhoneByImei(imeiList);
            for (PhoneImei phoneImei: phoneImeiList){
                String phone = phoneImei.getPhone();
                if (!nodeSet.contains(phone)){
                    Node node = new Node(phone,level);
                    String linkString=phoneImei.getPhone()+":"+phoneImei.getImei();
                    Link link = new Link(phoneImei.getPhone(),phoneImei.getImei());
                    nodeSet.add(phone);
                    nodeList.add(node);
                    linkSet.add(linkString);
                    linkList.add(link);
                    paramList.add(phone);
                }
            }
            getAllNode(paramList,nodeSet,linkSet,nodeList,linkList,level+1);
        }

    }

    private void splitParam(List<String> paramList, List<String> phoneList,List<String> imeiList, List<String> imsiList, List<String> clueList, List<String> suspectList) {
        for (String param:paramList){
            if (param.length()==11){
                phoneList.add(param);
                continue;
            }
            if (param.startsWith("46")&&param.length()==15){
                imsiList.add(param);
                continue;
            }
            if (param.length()==15||param.length()==14){
                imeiList.add(param);
                continue;
            }
            if (param.startsWith("X")||param.startsWith("GX")){
                clueList.add(param);
                continue;
            }
            if (param.length()==18){
                suspectList.add(param);
                continue;
            }

        }

    }*/


}
