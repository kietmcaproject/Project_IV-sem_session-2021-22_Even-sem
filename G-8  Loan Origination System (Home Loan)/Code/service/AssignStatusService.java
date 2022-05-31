package com.kuliza.workbench.service;

import com.google.gson.Gson;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("AssignStatusService")
public class AssignStatusService {
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  private static final Logger logger = LoggerFactory.getLogger(AssignStatusService.class);

  public void statusUpdate(DelegatePlanItemInstance planItemInstance) throws JSONException {

    Map<String, Object> variables = planItemInstance.getVariables();
    logger.info("Variables :-{}", variables);
    JSONObject object = new JSONObject(new Gson().toJson(variables));
    JSONArray addressDetails = object.getJSONArray("applicant1AddressDetails_hl");
    JSONObject currentAddressObject = (JSONObject) addressDetails.get(0);
    String currentAddress = currentAddressObject.get("applicant1CurrentAddress_hl").toString();

    JSONArray techTable = new JSONArray();
    JSONObject techRow = new JSONObject();

    String decisionRTM = variables.getOrDefault("decisionRTM", "").toString();
    logger.info("DecisionRTM :-{}", decisionRTM);

    String decisionATM = variables.getOrDefault("decisionATM", "").toString();
    logger.info("DecisionATM :-{}", decisionATM);

    if (!decisionRTM.equals("")) {
      techRow.put("techVerificationStatus_hl", decisionRTM);
    }

    if (!decisionATM.equals("")) {
      techRow.put("techVerificationStatus_hl", decisionATM);
    }
    techRow.put("applicant1CurrentAddress_hl", currentAddress);

    techTable.put(techRow);
    logger.info("----------- techTable : {} ", techTable);
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "propertyTechVerification_hl", techTable.toString());

    JSONArray legalTable = new JSONArray();
    JSONObject legalRow = new JSONObject();

    String decisionILM = variables.getOrDefault("decisionILM", "").toString();
    logger.info("DecisionILM :-{}", decisionILM);

    if (!decisionILM.equals("")) {
      legalRow.put("legalVerificationStatus_hl", decisionILM);
    }
    legalRow.put("applicant1CurrentAddress_hl", currentAddress);

    legalTable.put(legalRow);
    logger.info("----------- legalTable : {} ", legalTable);
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(),
        "propertyLegalVerification_hl",
        legalTable.toString());

    JSONArray fcuArray = new JSONArray();
    JSONObject fcuObject = new JSONObject();
    String decisionFCU = variables.getOrDefault("decisionFCU", "").toString();
    logger.info("DecisionFCU :-{}", decisionFCU);

    if (!decisionFCU.equals("")) {
      fcuObject.put("applicant1FcuStatus_hl", decisionFCU);
    }
    logger.info("----------- fcuTable : {} ", fcuArray);

    fcuObject.put("applicant1CurrentAddress_hl", currentAddress);
    fcuArray.put(fcuObject);
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "applicant1FcuVerification_hl", fcuArray.toString());
  }

  public void statusAssign(String caseInstanceId) throws JSONException {
    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    JSONObject object = new JSONObject(new Gson().toJson(variables));

    logger.info("Variables :-{}", variables);

    JSONArray techTable = new JSONArray();
    JSONObject techRow = new JSONObject();

    String decisionRTM = variables.getOrDefault("decisionRTM", "").toString();
    logger.info("DecisionRTM :-{}", decisionRTM);

    String decisionATM = variables.getOrDefault("decisionATM", "").toString();
    logger.info("DecisionATM :-{}", decisionATM);

    if (!decisionRTM.equals("")) {
      techRow.put("techVerificationStatus_hl", decisionRTM);
    }

    if (!decisionATM.equals("")) {
      techRow.put("techVerificationStatus_hl", decisionATM);
    }
    techTable.put(techRow);
    logger.info("----------- techTable : {} ", techTable);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "propertyTechVerification_hl", techTable.toString());

    JSONArray legalTable = new JSONArray();
    JSONObject legalRow = new JSONObject();

    String decisionILM = variables.getOrDefault("decisionILM", "").toString();
    logger.info("DecisionILM :-{}", decisionILM);

    if (!decisionILM.equals("")) {
      legalRow.put("legalVerificationStatus_hl", decisionILM);
    }
    legalTable.put(legalRow);
    logger.info("----------- legalTable : {} ", legalTable);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "propertyLegalVerification_hl", legalTable.toString());

    JSONArray fcuArray = new JSONArray();
    JSONObject fcuObject = new JSONObject();
    String decisionFCU = variables.getOrDefault("decisionFCU", "").toString();
    logger.info("DecisionFCU :-{}", decisionFCU);

    if (!decisionFCU.equals("")) {
      fcuObject.put("applicant1FcuStatus_hl", decisionFCU);
    }

    JSONArray addressDetails = object.getJSONArray("applicant1AddressDetails_hl");
    JSONObject currentAddressObject = (JSONObject) addressDetails.get(0);
    String currentAddress = currentAddressObject.get("applicant1CurrentAddress_hl").toString();
    fcuObject.put("applicant1CurrentAddress_hl", currentAddress);
    fcuArray.put(fcuObject);
    logger.info("----------- fcuTable : {} ", fcuArray);

    cmmnRuntimeService.setVariable(
        caseInstanceId, "applicant1FcuVerification_hl", fcuArray.toString());
  }
}
