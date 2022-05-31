package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtcDocumentsService {
  private static final Logger logger = LoggerFactory.getLogger(VariableFlowService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired IbHelper ibHelper;

  @Value("${link.to.open.process}")
  private String linkToOpenProcess;

  public DelegateExecution otcTablePopulation(DelegateExecution execution) throws JSONException {
    Map<String, Object> variables = execution.getVariables();
    logger.info("otc variables : {}", variables);
    String oldCaseInstanceId =
        CommonHelperFunctions.getStringValue(execution.getVariable("oldCaseInstanceId"));
    logger.info("caseInstanceId : {}", oldCaseInstanceId);
    ArrayList otcDocumment = new ArrayList<>();
    otcDocumment =
        CommonHelperFunctions.getArrayList(
            cmmnRuntimeService.getVariable(oldCaseInstanceId, "otcDocument"));
    JSONArray otcDocumentsTable = new JSONArray();
    for (int i = 0; i < otcDocumment.size(); i++) {
      JSONObject tableData = new JSONObject();
      tableData.put("documentNameOtc_hl", otcDocumment.get(i));
      tableData.put("documentUploadOtc_hl", "");
      tableData.put("docRemarkOpsOtc_hl", "");
      tableData.put("docL2PhysicalOtc_hl", "");
      tableData.put("docL2RemarksOtc_hl", "");
      tableData.put("releasePaymentFinal_hl", "");
      otcDocumentsTable.put(tableData);
    }
    execution.setVariable("otcDocumentsTable_hl", otcDocumentsTable);
    return execution;
  }

  public DelegatePlanItemInstance notificationToRm(DelegatePlanItemInstance planItemInstance)
      throws UnirestException {
    logger.info("Inside otc notification to rm.................");
    String losId = CommonHelperFunctions.getStringValue(planItemInstance.getVariable("los_id"));
    logger.info("losId : {}", losId);
    String otcLink =
        linkToOpenProcess.concat("RmOtcProcess").concat("&applicationId=").concat(losId);
    logger.info("otcLink : {}", otcLink);
    Map<String, Object> notificationRequest = new HashMap<>();
    notificationRequest.put("title", "OTC Documents Details");
    notificationRequest.put("description", "Link contains detail of the OTC Documents");
    notificationRequest.put("lan_id", losId);
    notificationRequest.put("webpage_link", otcLink);
    notificationRequest.put("raised_by", "");
    logger.info("notificationRequest : {}", notificationRequest);
    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("notification-to-rm/notification-api"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(notificationRequest))
            .asString();
    logger.info("Response of notification:- {}", response.getBody());

    return planItemInstance;
  }
}
