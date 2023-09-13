package com.wa.cluemrg.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.bo.CallLogExportBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.*;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.*;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;


@RestController
@RequestMapping("/callLog")
public class CallLogController {

    @Autowired
    CallLogService callLogService;
    @Autowired
    BtClueService btClueService;
    @Autowired
    PhoneImeiService phoneImeiService;
    @Autowired
    PhoneImsiService phoneImsiService;
    @Autowired
    NodeTagService nodeTagService;
    @Autowired
    LinkTagService linkTagService;

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.CallLog>
     */
    @PostMapping("/getCallLogList")
    public JsGridVO getCallLogList(@RequestBody PageBO<CallLogBo> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }
        pageBo.getData().setPageIndex(pageBo.getPageIndex());
        pageBo.getData().setPageSize(pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        if (StringUtils.isEmpty(sortField)) {
            sortField="PHONE";
        }
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortOrder)) {
            sortOrder="ASC";
        }
        pageBo.getData().setSortField(sortField);
        pageBo.getData().setSortOrder(sortOrder);
        pageBo.getData().setOffset(pageBo.getPageSize()*(pageBo.getPageIndex()-1));
        //PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        //String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        //String sortOrder = pageBo.getSortOrder();
        //PageHelper.orderBy(sortField+" "+sortOrder);

        List<CallLogBo> list = callLogService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        int total = callLogService.selectCount(pageBo.getData());
        int pages = (total/ pageBo.getPageSize())+1;
        page.setPages(pages);
        page.setTotal(total);
        JsGridVO<CallLogBo> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody CallLog callLog) {
        return callLogService.insert(callLog);
    }

    @PutMapping("/update")
    public int update(@RequestBody CallLog callLog) {
        return callLogService.update(callLog);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam String id) {
        return callLogService.delete(id);
    }

    @PostMapping("/upload")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return callLogService.dealUpload(file);
    }

    @GetMapping("exportExcel")
    public void exportExcel(CallLogBo callLogBo,HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(callLogBo.getPhone()+"话单", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<CallLogBo> list = callLogService.exportAll(callLogBo);
            /*for (CallLogBo callLog:list){
                callLog.setId(null);
            }*/
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), CallLogExportBo.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(Boolean.FALSE).sheet("话单")
                    .doWrite(list);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }



    @GetMapping("/getGraph")
    public Graph getGraph(@RequestParam("data") String data,@RequestParam("type") String type,
                          @RequestParam("winWidth") int winWidth,@RequestParam("winHeight") int winHeight){
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
            case "imsi":
                shape="triangle";
                category=2;
                break;
            case "clue":
                shape="diamond";
                category=3;
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
            if (currentNode.getSymbol().equals("rect")){
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

                PhoneImsi phoneImsi = new PhoneImsi();
                phoneImsi.setPhone(phone);
                List<PhoneImsi> phoneImsiList = phoneImsiService.selectAll(phoneImsi);
                for (PhoneImsi phoneImsiItem : phoneImsiList){
                    if (!visitedSet.contains(phoneImsiItem.getImsi())){
                        visitedSet.add(phoneImsiItem.getImsi());
                        Node node = new Node(phoneImsiItem.getImsi(),phoneImsiItem.getImsi(),phoneImsiItem.getImsi(),"circle",x+600,0,2,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(phone+"-"+phoneImsiItem.getImsi(),phone,phoneImsiItem.getImsi()));
                    }

                }
            }else if (currentNode.getSymbol().equals("circle")){
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
            }else if (currentNode.getSymbol().equals("triangle")){
                String imsi = currentNode.getName();
                if (!visitedSet.contains(imsi)){
                    visitedSet.add(imsi);
                    nodes.add(new Node(imsi,imsi,imsi,"triangle",x,y,2,currentNode.getLevel()));
                }
                PhoneImsi phoneImsi = new PhoneImsi();
                phoneImsi.setImsi(imsi);
                List<PhoneImsi> phoneImsiList = phoneImsiService.selectAll(phoneImsi);
                for (PhoneImsi phoneImsiItem : phoneImsiList){
                    if (!visitedSet.contains(phoneImsiItem.getPhone())){
                        visitedSet.add(phoneImsiItem.getPhone());
                        Node node = new Node(phoneImsiItem.getPhone(),phoneImsiItem.getPhone(),phoneImsiItem.getPhone(),"rect",x,0,0,currentNode.getLevel()+1);
                        queue.add(node);
                        tempNodes.add(node);
                        tempLinks.add(new Link(imsi+"-"+phoneImsiItem.getPhone(),imsi,phoneImsiItem.getPhone()));
                    }

                }
            }else if (currentNode.getSymbol().equals("diamond")){
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

    @Data
    class Item{
        private String data;
        private String type;

        Item(String data,String type){
            this.data=data;
            this.type=type;
        }
    }

}
