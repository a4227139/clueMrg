package com.wa.cluemrg.service;

import com.wa.cluemrg.bo.GangBo;
import com.wa.cluemrg.dao.GangMapper;
import com.wa.cluemrg.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
public class GangService {

    @Autowired
    private GangMapper gangMapper;
    @Autowired
    BtClueService btClueService;
    @Autowired
    PhoneImeiService phoneImeiService;
    @Autowired
    NodeTagService nodeTagService;
    @Autowired
    LinkTagService linkTagService;
    @Autowired
    TtClueService ttClueService;

    public List<Gang> selectAll(Gang gang) {
        return gangMapper.selectAll(gang);
    }

    public List<GangBo> exportAll(GangBo gang) {
        return gangMapper.exportAll(gang);
    }

    public Gang selectByPhone(String phone) {
        return gangMapper.selectByPhone(phone);
    }

    public List<String> selectAllPhone() {
        return gangMapper.selectAllPhone();
    }

    public int insert(Gang gang) {
        return gangMapper.insert(gang);
    }

    public int update(Gang gang) {
        return gangMapper.update(gang);
    }

    public int delete(int id) {
        return gangMapper.delete(id);
    }

    int batchInsert(List<Gang> list){
        return gangMapper.batchInsert(list);
    }

    int deleteAll(){
        return gangMapper.deleteAll();
    }

    public Gang genarateGang(String phone){
        Graph graph = getGraph(phone,"phone",0,0);
        List<Node> nodeList = graph.getNodes();
        Gang gang = new Gang();
        for (Node node:nodeList){
            if (node.getSymbol().equals("rect")){
                gang.getPhoneList().add(node.getId());
            }else if(node.getSymbol().equals("circle")){
                gang.getImeiList().add(node.getId());
            }else if(node.getSymbol().equals("triangle")){
                gang.getPersonList().add(node.getId());
            }else if(node.getSymbol().equals("diamond")){
                gang.getClueList().add(node.getId());
            }else if(node.getSymbol().equals("polygon")){
                gang.getCaseNoList().add(node.getId());
            }
        }
        gang.init();
        return gang;
    }

    public Graph getGraph(String data, String type,int winWidth,int winHeight){
        Graph graph = new Graph();
        List<Node> nodes = new ArrayList<>();
        List<Link> links = new ArrayList<>();
        ArrayDeque<Node> queue = new ArrayDeque<>();
        String shape="circle";
        int category=0;
        switch (type){
            case "phone":
                shape="rect";
                category=0;
                break;
            case "imei":
                shape="circle";
                category=1;
                break;
            case "person":
                shape="triangle";
                category=2;
                break;
            case "clue":
                shape="diamond";
                category=3;
                break;
            case "case":
                shape="polygon";
                category=5;
                break;
        }
        int x =200,y=300;
        Node firstNode = new Node(data,data,data,shape,x,y,category,1);
        queue.add(firstNode);
        Set<String> visitedSet = new HashSet<>();

        int maxLevel=0;
        while (!queue.isEmpty()){
            Node currentNode = queue.pop();
            maxLevel = maxLevel>currentNode.getLevel()?maxLevel:currentNode.getLevel();
            List<Node> tempNodes = new ArrayList<>();
            List<Link> tempLinks = new ArrayList<>();
            if (currentNode.getSymbol().equals("rect")){//phone
                String phone = currentNode.getName();
                if (!visitedSet.contains(phone)){
                    visitedSet.add(phone);
                    nodes.add(new Node(phone,phone,phone,"rect",x,y,0,currentNode.getLevel()));
                }

                BtClue btClue = new BtClue();
                btClue.setPhone(phone);
                List<BtClue> btClueList = btClueService.selectAll(btClue);
                for (BtClue clue : btClueList){
                    if (!visitedSet.contains(clue.getClueId())){
                        visitedSet.add(clue.getClueId());
                        Node node = new Node(clue.getClueId(),clue.getClueId(),clue.getClueId(),"diamond",x+600,0,3,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+clue.getClueId(),phone,clue.getClueId()));
                    }

                    String ownerId = clue.getOwnerId();
                    if (!StringUtils.isBlank(ownerId)&&!visitedSet.contains(ownerId)){
                        visitedSet.add(ownerId);
                        Node node = new Node(ownerId,ownerId,ownerId,"triangle",x+600,0,2,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+ownerId,phone,ownerId));
                    }

                    String caseNo = clue.getCaseNo();
                    if (!StringUtils.isBlank(caseNo)&&!visitedSet.contains(caseNo)){
                        visitedSet.add(caseNo);
                        Node node = new Node(caseNo,caseNo,caseNo,"polygon",x+600,0,5,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+caseNo,phone,caseNo));
                    }
                }

                TtClue ttClue = new TtClue();
                ttClue.setPhone(phone);
                List<TtClue> ttClueList = ttClueService.selectAll(ttClue);
                for (TtClue clue : ttClueList){
                    String ttClueId = clue.getClueId();
                    if (!visitedSet.contains(ttClueId)){
                        if (!ttClueId.startsWith("DX")&&!ttClueId.startsWith("LT")&&!ttClueId.startsWith("YD")){
                            continue;
                        }
                        visitedSet.add(ttClueId);
                        Node node = new Node(ttClueId,ttClueId,ttClueId,"diamond",x+600,0,4,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+ttClueId,phone,ttClueId));
                    }

                    String ownerId = clue.getOwnerId();
                    if (!StringUtils.isBlank(ownerId)&&!visitedSet.contains(ownerId)){
                        visitedSet.add(ownerId);
                        Node node = new Node(ownerId,ownerId,ownerId,"triangle",x+600,0,2,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+ownerId,phone,ownerId));
                    }
                }

                PhoneImei phoneImei = new PhoneImei();
                phoneImei.setPhone(phone);
                List<PhoneImei> phoneImeiList = phoneImeiService.selectAll(phoneImei);
                for (PhoneImei phoneImeiItem : phoneImeiList){
                    if (!visitedSet.contains(phoneImeiItem.getImei())){
                        visitedSet.add(phoneImeiItem.getImei());
                        Node node = new Node(phoneImeiItem.getImei(),phoneImeiItem.getImei(),phoneImeiItem.getImei(),"circle",x+600,0,1,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+phoneImeiItem.getImei(),phone,phoneImeiItem.getImei()));
                    }
                }
            }else if (currentNode.getSymbol().equals("circle")){//imei
                String imei = currentNode.getName();
                if (!visitedSet.contains(imei)){
                    visitedSet.add(imei);
                    nodes.add(new Node(imei,imei,imei,"circle",x,y,1,currentNode.getLevel()));
                }
                PhoneImei phoneImei = new PhoneImei();
                phoneImei.setImei(imei);
                List<PhoneImei> phoneImeiList = phoneImeiService.selectAll(phoneImei);
                for (PhoneImei phoneImeiItem : phoneImeiList){
                    if (!visitedSet.contains(phoneImeiItem.getPhone())){
                        visitedSet.add(phoneImeiItem.getPhone());
                        Node node = new Node(phoneImeiItem.getPhone(),phoneImeiItem.getPhone(),phoneImeiItem.getPhone(),"rect",x,0,0,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(imei+"-"+phoneImeiItem.getPhone(),imei,phoneImeiItem.getPhone()));
                    }
                }
            }else if (currentNode.getSymbol().equals("triangle")){//person
                String personId = currentNode.getName();
                if (StringUtils.isNotBlank(personId)){
                    if (!visitedSet.contains(personId)){
                        visitedSet.add(personId);
                        nodes.add(new Node(personId,personId,personId,"triangle",x,y,2,currentNode.getLevel()));
                    }

                    BtClue btClue = new BtClue();
                    btClue.setOwnerId(personId);
                    List<BtClue> btClueList = btClueService.selectAll(btClue);
                    for (BtClue clue : btClueList){
                        String phone = clue.getPhone();
                        if (!visitedSet.contains(phone)){
                            visitedSet.add(phone);
                            Node node = new Node(phone,phone,phone,"rect",x,0,0,currentNode.getLevel()+1);
                            queue.add(node);
                            tempNodes.add(node);
                            tempLinks.add(new Link(personId+"-"+phone,personId,phone));
                        }
                    }

                    TtClue ttClue = new TtClue();
                    ttClue.setOwnerId(personId);
                    List<TtClue> ttClueList = ttClueService.selectAll(ttClue);
                    for (TtClue clue : ttClueList){
                        String phone = clue.getPhone();
                        if (!visitedSet.contains(phone)){
                            visitedSet.add(phone);
                            Node node = new Node(phone,phone,phone,"rect",x,0,0,currentNode.getLevel()+1);
                            queue.add(node);
                            tempNodes.add(node);
                            tempLinks.add(new Link(personId+"-"+phone,personId,phone));
                        }
                    }
                }
            }else if (currentNode.getSymbol().equals("diamond")){//clue
                String clueId = currentNode.getName();
                if (!visitedSet.contains(clueId)){
                    visitedSet.add(clueId);
                    nodes.add(new Node(clueId,clueId,clueId,"diamond",x,y,3,currentNode.getLevel()));
                }
                BtClue btClue = new BtClue();
                btClue.setClueId(clueId);
                List<BtClue> btClueList = btClueService.selectAll(btClue);
                for (BtClue clue : btClueList){
                    if (!visitedSet.contains(clue.getPhone())){
                        visitedSet.add(clue.getPhone());
                        Node node = new Node(clue.getPhone(),clue.getPhone(),clue.getPhone(),"rect",x,0,0,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(clueId+"-"+clue.getPhone(),clueId,clue.getPhone()));
                    }

                    String caseNo = clue.getCaseNo();
                    if (!StringUtils.isBlank(caseNo)&&!visitedSet.contains(caseNo)){
                        visitedSet.add(caseNo);
                        Node node = new Node(caseNo,caseNo,caseNo,"polygon",x+600,0,5,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(clueId+"-"+caseNo,clueId,caseNo));
                    }
                }

                TtClue ttClue = new TtClue();
                ttClue.setClueId(clueId);
                List<TtClue> ttClueList = ttClueService.selectAll(ttClue);
                for (TtClue clue : ttClueList){
                    String ttClueId = clue.getClueId();
                    if (!visitedSet.contains(ttClueId)){
                        if (!ttClueId.startsWith("DX")&&!ttClueId.startsWith("LT")&&!ttClueId.startsWith("YD")){
                            continue;
                        }
                        visitedSet.add(ttClueId);
                        Node node = new Node(clue.getPhone(),clue.getPhone(),clue.getPhone(),"rect",x,0,0,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(clueId+"-"+clue.getPhone(),clueId,clue.getPhone()));
                    }
                }
            }
            /*int size = tempNodes.size();
            int[] yIndexArray = new int[size];
            int flag=1;
            for (int i =0;i<size;i++){
                if (i==0){
                    yIndexArray[i]=y+flag*i*400;
                    continue;
                }
                if (flag>0){
                    yIndexArray[i]=y+flag*i*400;
                }else {
                    yIndexArray[i]=y+flag*(i-1)*400;
                }
                flag=-flag;
            }

            for (int i = 0;i<tempNodes.size();i++){
                tempNodes.get(i).setY(yIndexArray[i]);
            }*/

            nodes.addAll(tempNodes);
            links.addAll(tempLinks);
        }
        List<String> nodeKeyList = new ArrayList<>();
        for (Node node:nodes){
            nodeKeyList.add(node.getName());
        }
        List<NodeTag> nodeTagList = nodeTagService.selectAllByKeyList(nodeKeyList);
        for (Node node:nodes){
            for (NodeTag nodeTag : nodeTagList){
                if (node.getName().equals(nodeTag.getNode())){
                    node.setValue(nodeTag.getTag());
                }
            }
        }

        List<LinkTag> linkTagList = linkTagService.selectAllByLinkList(links);
        for (Link link:links){
            for (LinkTag linkTag:linkTagList){
                if (link.getSource().equals(linkTag.getSource())&&link.getTarget().equals(linkTag.getTarget())){
                    Label label = new Label();
                    //label.setShow(true);
                    label.setFormatter(linkTag.getTag());
                    link.setLabel(label);
                    link.setValue(linkTag.getTag());
                }
            }
        }

        List<Node> levelNodeList[] = new ArrayList[maxLevel];
        for (int i =0;i<maxLevel;i++){
            levelNodeList[i] = new ArrayList<>();
        }
        for (Node node:nodes){
            levelNodeList[node.getLevel()-1].add(node);
        }
        int yLevel = 1;
        for (List<Node> list:levelNodeList){
            yLevel=yLevel>list.size()?yLevel:list.size();
        }

        double xGap = winWidth/maxLevel;
        double yGap = winHeight/yLevel;
        double symbolSize = Math.min(xGap,yGap)-5;
        symbolSize=symbolSize>45?45:symbolSize;
        nodes.clear();
        for (List<Node> list:levelNodeList){
            int flag=1;int l=0;
            yGap = winHeight/list.size();
            for (Node node:list){
                node.setSymbolSize(symbolSize);
                node.setX(200+xGap*node.getLevel());
                if (l==0){
                    node.setY(200);
                    l++;
                    continue;
                }
                if (flag>0){
                    node.setY(200+yGap*flag*l);
                }else {
                    node.setY(200+yGap*flag*(l));
                    l++;
                }
                flag=-flag;
            }
            nodes.addAll(list);
        }
        graph.setNodes(nodes);
        graph.setLinks(links);
        return graph;
    }

    public Gang get(int id) {
        return gangMapper.get(id);
    }
}
