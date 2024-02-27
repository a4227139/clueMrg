package com.wa.cluemrg.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllDbQuery {
    @JsonProperty("parentapplicationid")
    private String parentApplicationId;

    @JsonProperty("recorderorgname")
    private String recorderOrgName;

    @JsonProperty("applicationorgid")
    private String applicationOrgId;

    @JsonProperty("failurecause")
    private String failureCause;

    @JsonProperty("txcode")
    private String txCode;

    @JsonProperty("starttime")
    private String startTime;

    @JsonProperty("startaccountbalance")
    private String startAccountBalance;

    @JsonProperty("cam")
    private String cam;

    @JsonProperty("result")
    private String result;

    @JsonProperty("orderStr")
    private String orderStr;

    @JsonProperty("checktype")
    private String checkType;

    @JsonProperty("bankid")
    private String bankId;

    @JsonProperty("casenumber")
    private String caseNumber;

    @JsonProperty("accountname")
    private String accountName;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("datatype")
    private String datatype;

    @JsonProperty("txcodename")
    private String txCodeName;

    @JsonProperty("recountenable")
    private boolean recountEnable;

    @JsonProperty("casefrom")
    private String caseFrom;

    @JsonProperty("extra")
    private String extra;

    @JsonProperty("token1")
    private String token1;

    @JsonProperty("querystate")
    private String queryState;

    @JsonProperty("beginTime")
    private String beginTime;

    @JsonProperty("applicationid")
    private String applicationId;

    @JsonProperty("querytype")
    private String queryType;

    @JsonProperty("operatororgname")
    private String operatorOrgName;

    @JsonProperty("accountnumber")
    private String accountNumber;

    @JsonProperty("manager")
    private boolean manager;

    @JsonProperty("feedbackremark")
    private String feedbackRemark;

    @JsonProperty("relistenable")
    private boolean relistEnable;

    @JsonProperty("creattime")
    private String createTime;

    @JsonProperty("endabalance")
    private String endAbalance;

    @JsonProperty("endtime")
    private String endTime;

    @JsonProperty("endaccountbalance")
    private String endAccountBalance;

    @JsonProperty("queryorgid")
    private String queryOrgId;

    @JsonProperty("queryid")
    private String queryId;

    @JsonProperty("queryendtime")
    private String queryEndTime;

    @JsonProperty("startbalance")
    private String startBalance;

    @JsonProperty("querystarttime")
    private String queryStartTime;

    @JsonProperty("recordername")
    private String recorderName;

    @JsonProperty("bankname")
    private String bankName;

    @JsonProperty("detailflag")
    private String detailFlag;

    @JsonProperty("operatorname")
    private String operatorName;

    @JsonProperty("datafrom")
    private String dataFrom;

}

