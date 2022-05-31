package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.Posidex;
import com.kuliza.workbench.repository.PosidexRepository;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PosiedexDedupeService")
public class PosiedexDedupeService {
  private static final Logger logger = LoggerFactory.getLogger(PosiedexDedupeService.class);
  @Autowired IbHelper ibHelper;
  @Autowired CapriUtilService capriUtilService;
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired PosidexRepository posidexRepository;

  public DelegatePlanItemInstance searchProperty(DelegatePlanItemInstance planItemInstance)
      throws JSONException, UnirestException {
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    Posidex posidex = new Posidex();
    Map<String, Object> variables = planItemInstance.getVariables();
    logger.info("variables.. :- {}", variables);
    String assetSr = assetSrNo(variables, planItemInstance);
    logger.info("Asset Serial No :- {}", assetSr);
    cmmnRuntimeService.setVariable(caseInstanceId, "assetSrNo_hl", assetSr);
    posidex.setAssetSrNo(assetSr);
    posidex.setCaseInstanceId(caseInstanceId);
    Map<String, Object> payload = payload(assetSr, planItemInstance);
    logger.info("Payload :- {}", payload);
    logger.info("Payload in jsonobject :- {}", new JSONObject(payload));

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("posidex/search-property"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(payload))
            .asString();
    logger.info("Response of posidex:- {}", response.getBody());
    posidex.setPosidexResponse(response.getBody());
    setVariables(response.getBody(), planItemInstance, caseInstanceId);
    return planItemInstance;
  }

  private void setVariables(
      String body, DelegatePlanItemInstance planItemInstance, String caseInstanceId)
      throws JSONException {
    JSONObject responseObject = new JSONObject(body);
    JSONArray propertyBaseMatches = null;
    try {
      propertyBaseMatches = responseObject.getJSONArray("PROPERTY_BASE_MATCHES");
    } catch (JSONException e) {
    }
    logger.info("PROPERTY_BASE_MATCHES :- {}", propertyBaseMatches);

    String technicalStatus =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("technicalStatus"));
    logger.info("Technical Status :- {}", technicalStatus);

    if (!propertyBaseMatches.equals(null)) {
      boolean flagForScaleType = false;
      for (int i = 0; i < propertyBaseMatches.length(); i++) {
        JSONObject object = (JSONObject) propertyBaseMatches.get(i);
        String scaleType = CommonHelperFunctions.getStringValue(object.get("SCALE_TYPE"));
        if (scaleType.equalsIgnoreCase("CGCL_Good")) {
          flagForScaleType = true;
        }
      }
      if (flagForScaleType) {
        cmmnRuntimeService.setVariable(caseInstanceId, "dedupeResult", "Match Found");
        if (technicalStatus.equalsIgnoreCase("Reject")) {
          cmmnRuntimeService.setVariable(caseInstanceId, "isTechnicalRejection", "True");
        } else {
          cmmnRuntimeService.setVariable(caseInstanceId, "isTechnicalRejection", "False");
        }
      }

      if (!flagForScaleType || !(technicalStatus.equalsIgnoreCase("Reject"))) {
        cmmnRuntimeService.setVariable(caseInstanceId, "dedupeResult", "Match Not Found");
        cmmnRuntimeService.setVariable(caseInstanceId, "isTechnicalRejection", "False");
      }
    } else {
      cmmnRuntimeService.setVariable(caseInstanceId, "dedupeResult", "Match Not Found");
      cmmnRuntimeService.setVariable(caseInstanceId, "isTechnicalRejection", "False");
    }
  }

  private Map<String, Object> payload(String assetSr, DelegatePlanItemInstance planItemInstance)
      throws JSONException {

    String lanNo = CommonHelperFunctions.getStringValue(planItemInstance.getVariable("loanId_hl"));
    logger.info("Lan No :- {}", lanNo);
    String appNo = "AP" + lanNo.substring(2);
    logger.info("App No :- {}", appNo);
    Map<String, Object> request = new HashMap<>();
    Map<String, Object> propertyInformation = new HashMap<>();
    request.put("ASSIGN_UPID", "N");
    request.put("SOURCE_SYSTEM_NAME", "CGCL");
    request.put("SOURCE_AUTHENTICATION_TOKEN", "100");
    request.put("MATCHING_RULE_PROFILE", "1");

    //    String finalSubmitRequestBody =
    //        CommonHelperFunctions.getStringValue(
    //            planItemInstance.getVariable("finalSubmitRequestBody"));
    //    logger.info("------------- finalSubmitRequestBody : {} ", finalSubmitRequestBody);
    //    JSONObject finalSubmitJsonObj = new JSONObject(finalSubmitRequestBody);
    String propertyCity =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("propertyCity"));
    String propertyState =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("propertyState"));
    String propertyPinCode =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("propertyPinCode"));
    String customerNo =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("applicant1UcicId_hl"));

    propertyInformation.put("I_ASSET_SR_NO", assetSr);
    propertyInformation.put("APPLICATION_NO", appNo);
    propertyInformation.put("LAN_NO", lanNo);
    propertyInformation.put("CUSTOMER_NO", customerNo);
    propertyInformation.put(
        "CUSTOMER_NAME",
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("applicant1Name_hl")));
    propertyInformation.put(
        "PROPERTY_OWNER_NAME",
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("applicant1Name_hl")));
    propertyInformation.put(
        "PROPERTY_ADDRESS_1",
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("propertyAddress_hl")));
    propertyInformation.put(
        "BRANCH_NAME",
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("loanBranch_hl")));
    propertyInformation.put("PRODUCT_DESCRIPTION", "Home Loan");
    propertyInformation.put("PROPERTY_CITY", propertyCity);
    propertyInformation.put("PROPERTY_STATE", propertyState);
    propertyInformation.put("PROPERTY_PINCODE", propertyPinCode);
    request.put("PROPERTY_INFORMATION", propertyInformation);
    return request;
  }

  public String assetSrNo(
      Map<String, Object> variables, DelegatePlanItemInstance planItemInstance) {
    Posidex previousAssetNo = posidexRepository.findTop1ByOrderByCreatedDesc();
    String assetNo;
    String currentDate = capriUtilService.getCurrentDate("ddMMyy");
    if (previousAssetNo == null) {
      assetNo = "HL" + currentDate + "0001";
    } else {
      assetNo = previousAssetNo.getAssetSrNo();
      String previousDate = assetNo.substring(2, 8);
      int difference = Integer.parseInt(currentDate) - Integer.parseInt(previousDate);
      if (difference != 0) {
        assetNo = "HL" + currentDate + "0001";
      } else {
        long lastFourDigits = Long.parseLong(assetNo.substring(assetNo.length() - 4)) + 1;
        assetNo = "HL" + currentDate + String.format("%04d", lastFourDigits);
      }
    }

    return assetNo;
  }

  public DelegatePlanItemInstance getMatches(DelegatePlanItemInstance planItemInstance)
      throws UnirestException {
    Map<String, Object> variables = planItemInstance.getVariables();
    logger.info("variables.. :- {}", variables);
    Map<String, Object> request = new HashMap<>();
    request.put("REQUEST_ID", "");
    request.put("SOURCE_SYSTEM_NAME", "CGCL");
    request.put("SOURCE_AUTHENTICATION_TOKEN", "100");
    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("posidex/get-matches"))
            .body(new JSONObject(request))
            .asString();
    logger.info("Response :- {}", response.getBody());

    return planItemInstance;
  }

  public DelegatePlanItemInstance assignUpid(DelegatePlanItemInstance planItemInstance)
      throws UnirestException {
    Map<String, Object> variables = planItemInstance.getVariables();
    logger.info("variables.. :- {}", variables);
    Map<String, Object> request = new HashMap<>();
    request.put("REQUEST_ID", "");
    request.put("SOURCE_SYSTEM_NAME", "CGCL");
    request.put("SOURCE_AUTHENTICATION_TOKEN", "100");
    request.put("U_PID", "0");
    request.put("U_PID_TYPE", "NEW");

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("posidex/assign-upid"))
            .body(new JSONObject(request))
            .asString();
    logger.info("Response :- {}", response.getBody());

    return planItemInstance;
  }

  //  public static void main(String[] args) {
  //    String previousAssetNo = "HL16042210001";
  //    String assetNo;
  //    String currentDate = "170422";
  //    String a = previousAssetNo.substring(2, 8);
  //    int b = Integer.parseInt(currentDate) - Integer.parseInt(a);
  //    if (previousAssetNo.equalsIgnoreCase("") || b != 0) {
  //      assetNo = "HL" + currentDate + "0001";
  //    } else {
  //      long lastFourDigits =
  //          Long.parseLong(previousAssetNo.substring(previousAssetNo.length() - 4)) + 1;
  //      assetNo = "HL" + currentDate + String.format("%04d", lastFourDigits);
  //    }
  //
  //    System.out.println(assetNo);
  //  }
}
