package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.model.TechAppRequest;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TechnicalAppApiService {
  private static final Logger logger = LoggerFactory.getLogger(TechnicalAppApiService.class);

  @Autowired CmmnTaskService cmmnTaskService;

  @Autowired HistoryService historyService;
  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired RuntimeService runtimeService;

  public ApiResponse fetchCases(Map<String, Object> request) throws JSONException {
    logger.info("------------ IN : fetchCases() method of TechnicalAppApiService ----------------");

    if (request == null
        || request.get("activity_code") == null
        || request.get("activity_code").toString().equals("")) {
      Map<String, Object> response = new HashMap<>();
      response.put("APPLICATIONS_INFO", "[]");
      response.put("ERROR", "Y");
      response.put("ERROR_MESSAGE", "ACTIVITY CODE IS NULL");
      return new ApiResponse(HttpStatus.BAD_REQUEST, "Bad request", response);
    }

    String activityCode = CommonHelperFunctions.getStringValue(request.get("activity_code"));
    logger.info("----------- activityCode : {} ", activityCode);

    Set<String> activityCodes = new HashSet<>();
    activityCodes.add("TECHNICAL_VERIFICATION_I");
    activityCodes.add("TECHNICAL_VERIFICATION_E");
    activityCodes.add("TECHNICAL_VERIFICATION2_I");
    activityCodes.add("TECHNICAL_VERIFICATION2_E");

    if (!activityCodes.contains(activityCode)) {
      Map<String, Object> response = new HashMap<>();
      response.put("APPLICATIONS_INFO", "[]");
      response.put("ERROR", "Y");
      response.put("ERROR_MESSAGE", "ACTIVITY CODE IS INCORRECT");
      return new ApiResponse(HttpStatus.BAD_REQUEST, "Bad request", response);
    }

    JSONArray applicationsInfo = new JSONArray();
    if (activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION_I")
        || activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION_E")) {
      List<CaseInstance> tasks =
          cmmnRuntimeService
              .createCaseInstanceQuery()
              .variableValueEquals("TechnicalCoordinatorStatus", "history")
              .variableValueEquals("isSentToTechApp_hl", "no")
              .list();

      logger.info("------- tasks: {}", tasks);
      List<String> caseIds = new ArrayList<>();
      if (tasks != null) {
        tasks.forEach(
            task -> {
              caseIds.add(task.getId());
            });
      }
      logger.info("------ caseIds : {}", caseIds);

      applicationsInfo = new JSONArray();
      if (tasks != null) {
        for (String caseId : caseIds) {
          Map<String, Object> variables = cmmnRuntimeService.getVariables(caseId);
          String caseInstanceId = variables.get("caseInstanceId").toString();
          String losId = variables.get("loanId_hl").toString();
          logger.info("losId : {}", losId);
          String quadrant = CommonHelperFunctions.getStringValue(variables.get("quadrant"));
          JSONObject object = new JSONObject();
          if (!quadrant.equals("")) {
            if (activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION_I")) {
              if (quadrant.equalsIgnoreCase("first") || quadrant.equalsIgnoreCase("third")) {
                String btmEmail = variables.get("btmEmail").toString();
                if (!btmEmail.equals("")) {
                  object = new JSONObject();
                  object.put("losId", losId);
                  object.put("btmEmail", btmEmail);
                  applicationsInfo.put(object);
                  cmmnRuntimeService.setVariable(caseInstanceId, "tv1Status", "Initiated");
                }
              }
            } else if (activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION_E")) {
              if (quadrant.equalsIgnoreCase("second")
                  || quadrant.equalsIgnoreCase("third")
                  || quadrant.equalsIgnoreCase("fourth")) {
                String etv1Email = variables.get("etv1Email").toString();
                if (!etv1Email.equals("")) {
                  object = new JSONObject();
                  object.put("losId", losId);
                  object.put("etv1Email", etv1Email);
                  applicationsInfo.put(object);
                  cmmnRuntimeService.setVariable(caseInstanceId, "tv1Status", "Initiated");
                }
              }
            }
            cmmnRuntimeService.setVariable(caseInstanceId, "isSentToTechApp_hl", "yes");
          }
        }
      }
    } else {
      List<CaseInstance> secondEvalTasks =
          cmmnRuntimeService
              .createCaseInstanceQuery()
              .variableValueEquals("TechnicalCoordinatorStatus", "history")
              .variableValueEquals("secondEvaluation", "yes")
              .list();

      logger.info("------- tasksSecondEval: {}", secondEvalTasks);
      List<String> secondEvalCaseIds = new ArrayList<>();
      if (secondEvalTasks != null) {
        secondEvalTasks.forEach(
            task -> {
              secondEvalCaseIds.add(task.getId());
            });
      }
      logger.info("------ caseIdsSecondEval : {}", secondEvalCaseIds);

      if (secondEvalTasks != null) {
        for (String caseId : secondEvalCaseIds) {
          Map<String, Object> variables = cmmnRuntimeService.getVariables(caseId);
          String caseInstanceId = variables.get("caseInstanceId").toString();
          String losId = variables.get("loanId_hl").toString();
          String quadrant = variables.get("quadrant").toString();
          JSONObject object = new JSONObject();

          if (!quadrant.equals("")) {
            if (activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION2_I")) {
              if (quadrant.equalsIgnoreCase("third")) {
                String btmEmail = variables.get("btmEmail").toString();
                if (!btmEmail.equals("")) {
                  object = new JSONObject();
                  object.put("losId", losId);
                  object.put("btmEmail", btmEmail);
                  applicationsInfo.put(object);
                  cmmnRuntimeService.setVariable(caseInstanceId, "tv2Status", "Initiated");
                }
              }
            } else if (activityCode.equalsIgnoreCase("TECHNICAL_VERIFICATION2_E")) {
              if (quadrant.equalsIgnoreCase("fourth")) {
                String etv2Email = variables.get("etv2Email").toString();
                if (!etv2Email.equals("")) {
                  object = new JSONObject();
                  object.put("losId", losId);
                  object.put("etv2Email", etv2Email);
                  applicationsInfo.put(object);
                  cmmnRuntimeService.setVariable(caseInstanceId, "tv2Status", "Initiated");
                }
              }
            }
            cmmnRuntimeService.setVariable(caseInstanceId, "isSentToTechApp_hl", "yes");
          }
        }
      }
    }

    JSONObject response = new JSONObject();
    response.put("APPLICATIONS_INFO", applicationsInfo);
    response.put("ERROR", "N");
    response.put("ERROR_MESSAGE", "");
    logger.info("------- response sent to TechApp : {}", response);
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.toString());
    return new ApiResponse(HttpStatus.OK, "Success", res);
  }

  public ApiResponse receiveDetailsFromTechApp(TechAppRequest techAppRequest) throws JSONException {
    logger.info("--------------- IN : receiveDetailsFromTechApp ---------------");

    String applicationNumber = techAppRequest.getApplication_number();
    String collateralId = techAppRequest.getCollateral_id();
    PragatiAppDetails pragatiAppDetails = new PragatiAppDetails();
    try {
      pragatiAppDetails = pragatiAppDetailsRepository.findByKulizaAppId(applicationNumber);
      logger.info("-------- pragatiAppDetails : {}", pragatiAppDetails);
    } catch (Exception e) {
      logger.info("Error while querying pragati app details table for {}", applicationNumber);
    }

    try {
      String processInstanceId = pragatiAppDetails.getProcessInstanceId();
      // TODO: variable is created ( isTechValuationCompletedInLos ) but logic to set 'yes' is not
      // complete from config side
      String techValuationStatus =
          CommonHelperFunctions.getStringValue(
              runtimeService.getVariable(processInstanceId, "isTechValuationCompletedInLos"));
      if (techValuationStatus.equalsIgnoreCase("yes")) {
        Map<String, Object> response =
            createTechAppResponse(
                applicationNumber,
                collateralId,
                "FAILURE",
                "Y",
                "Technical valuation activity has been completed in LOS application");
        return new ApiResponse(HttpStatus.OK, "FAILURE", response);
      }
    } catch (JSONException e) {
    }

    Map<String, Object> response =
        createTechAppResponse(applicationNumber, collateralId, "SUCCESS", "N", "");
    return new ApiResponse(HttpStatus.OK, "Success", response);
  }

  private Map<String, Object> createTechAppResponse(
      String applicationNumber,
      String collateralId,
      String status,
      String error,
      String errorMessage)
      throws JSONException {
    Map<String, Object> response = new HashMap<>();
    response.put("APPLICATION_NUMBER", applicationNumber);
    response.put("COLLATERAL_ID", collateralId); // TODO: What is collateral id? (Rishabh sir)
    response.put("STATUS", status);
    response.put("ERROR", error);
    response.put("ERROR_MESSAGE", errorMessage);
    return response;
  }

  //  private String checkForMandatoryFields(Map<String, Object> request) {
  //    Map<String, String> map = new HashMap<>();
  //    map.put("val_date", "Valuation date is mandatory");
  //    map.put("val_report", "Valuation Report no is mandatory");
  //    map.put("val_name", "Valuer name is mandatory");
  //    map.put("final_status", "Final status is mandatory");
  //    map.put("land_area_sqft", "Land Area  (in sq. ft.) is mandatory");
  //    map.put("land_rate_sqft", "Land Rate (Rs/sq.ft.) is mandatory");
  //    map.put("carpet_area_sqft", "Carpet Area (in sq. ft.) is mandatory");
  //    map.put(
  //        "con_builtup_area_sqft",
  //        "Construction Area / Built Up / Super Built Up Area (in sq.ft.) is mandatory");
  //    map.put("other_amenities_value", "Other amenities Value / Car parking / PLC is mandatory");
  //    map.put(
  //        "con_builtup_area_value",
  //        "Construction Area / Built Up / Super Built Up Area Rate (Rs/(sq.ft.) is mandatory");
  //    map.put("stage_of_construction", "Stage of Construction is mandatory");
  //    map.put("total_valuation", "Total Valuation is mandatory");
  //    map.put("perc_recomm", "% of Recommendation is mandatory");
  //    map.put("perc_compl", "% of completion is mandatory");
  //    map.put("any_deviation", "Any Deviation is mandatory");
  //    map.put("property_visited", "Property Visited is mandatory");
  //
  //    Set<String> keySet = request.keySet();
  //    for (String key : map.keySet()) {
  //      if (!keySet.contains(key)) {
  //        return map.get(key);
  //      }
  //    }
  //    logger.info("-------- Mandatory Fields are present -----------");
  //    return "MANDATORY FIELDS ARE PRESENT";
  //  }

  //  private boolean isValidData(Map<String, Object> request) {
  //
  //    Object valuationDate = request.get("val_date");
  //    if (valuationDate.toString().length() == 10) {
  //      try {
  //        String dateStr = valuationDate.toString();
  //        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  //        LocalDate.parse(dateStr, dateFormatter);
  //      } catch (DateTimeParseException e) {
  //        logger.info("------ valuationDate : INVALID {}", valuationDate);
  //        return false;
  //      }
  //    } else {
  //      logger.info("------ valuationDate : INVALID {}", valuationDate);
  //      return false;
  //    }
  //
  //    Object valuationReportNo = request.get("val_report");
  //    if (!(valuationReportNo instanceof String) || valuationReportNo.toString().length() > 30) {
  //      logger.info("------ valuationReportNo : INVALID {}", valuationReportNo);
  //      return false;
  //    }
  //
  //    Object valuerName = request.get("val_name");
  //    if (!(valuerName instanceof String) || valuerName.toString().length() > 50) {
  //      logger.info("------ valuerName : INVALID {}", valuerName);
  //      return false;
  //    }
  //
  //    Object remarks = request.get("remarks");
  //    if (!(remarks instanceof String) || remarks.toString().length() > 3000) {
  //      logger.info("------ remarks : INVALID {}", remarks);
  //      return false;
  //    }
  //
  //    Object landAreaSqFt = request.get("land_area_sqft");
  //    if (!isValidFloat(landAreaSqFt, 9, 2)) {
  //      logger.info("------ landAreaSqFt : INVALID {}", landAreaSqFt);
  //      return false;
  //    }
  //
  //    Object landRateSqFt = request.get("land_rate_sqft");
  //    if (!isValidFloat(landRateSqFt, 15, 2)) {
  //      logger.info("------ landRateSqFt : INVALID {}", landRateSqFt);
  //
  //      return false;
  //    }
  //
  //    Object carpetAreaSqFt = request.get("carpet_area_sqft");
  //    if (!isValidFloat(carpetAreaSqFt, 9, 2)) {
  //      logger.info("------ carpetAreaSqFt : INVALID {}", carpetAreaSqFt);
  //      return false;
  //    }
  //
  //    Object conBuiltupAreaSqFt = request.get("con_builtup_area_sqft");
  //    if (!isValidFloat(conBuiltupAreaSqFt, 9, 2)) {
  //      logger.info("------ conBuiltupAreaSqFt : INVALID {}", conBuiltupAreaSqFt);
  //      return false;
  //    }
  //
  //    Object otherAmenitiesValue = request.get("other_amenities_value");
  //    if (!isValidFloat(otherAmenitiesValue, 15, 2)) {
  //      logger.info("------ otherAmenitiesValue : INVALID {}", otherAmenitiesValue);
  //      return false;
  //    }
  //
  //    Object conBuiltupAreaValue = request.get("con_builtup_area_value");
  //    if (!isValidFloat(conBuiltupAreaValue, 15, 2)) {
  //      logger.info("------ conBuiltupAreaValue : INVALID {}", conBuiltupAreaValue);
  //      return false;
  //    }
  //
  //    Object stageOfConstruction = request.get("stage_of_construction");
  //    if (!(stageOfConstruction instanceof String) || stageOfConstruction.toString().length() >
  // 150) {
  //      logger.info("------ stageOfConstruction : INVALID {}", stageOfConstruction);
  //      return false;
  //    }
  //
  //    Object totalValuation = request.get("total_valuation");
  //    if (!isValidFloat(totalValuation, 15, 2)) {
  //      logger.info("------ totalValuation : INVALID {}", totalValuation);
  //      return false;
  //    }
  //
  //    Object percRecomm = request.get("perc_recomm");
  //    if (!isValidFloat(percRecomm, 5, 2)) {
  //      logger.info("------ percRecomm : INVALID {}", percRecomm);
  //      return false;
  //    }
  //
  //    Object percCompl = request.get("perc_compl");
  //    if (!isValidFloat(percCompl, 5, 2)) {
  //      logger.info("------ percCompl : INVALID {}", percCompl);
  //      return false;
  //    }
  //
  //    Object anyDeviation = request.get("any_deviation");
  //    if (!(anyDeviation instanceof String) || anyDeviation.toString().length() > 300) {
  //      logger.info("------ anyDeviation : INVALID {}", anyDeviation);
  //      return false;
  //    }
  //
  //    Object propertyVisited = request.get("property_visited");
  //    if (!(propertyVisited instanceof String) || propertyVisited.toString().length() > 100) {
  //      logger.info("------ propertyVisited : INVALID {}", propertyVisited);
  //      return false;
  //    }
  //
  //    logger.info("--------- data validation completed with 'valid data' status -----------");
  //    return true;
  //  }

  //  public boolean isValidFloat(Object landAreaSqFt, int integerPart, int decimalPart) {
  //    if (!NumberUtils.isCreatable(landAreaSqFt.toString())) {
  //      return false;
  //    } else {
  //      String landArea = landAreaSqFt.toString();
  //      if (landArea.contains(".")) {
  //        int integerPlaces = landArea.indexOf(".");
  //        int decimalPlaces = landArea.length() - integerPlaces - 1;
  //        if (integerPlaces > integerPart || decimalPlaces > decimalPart) {
  //          return false;
  //        }
  //      } else {
  //        if (landArea.length() > integerPart) {
  //          return false;
  //        }
  //      }
  //    }
  //    return true;
  //  }
}
