package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.CapriUtilService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class InitiateDisbursalService {
  private static final Logger logger = LoggerFactory.getLogger(InitiateDisbursalService.class);
  private static final DecimalFormat df = new DecimalFormat("0.00");

  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired CapriUtilService capriUtilService;

  public ApiResponse initiate(Map<String, Object> request) throws JSONException {

    logger.info("Inside initiate disbursal api....................");
    String caseInstanceId = request.get("caseInstanceId").toString();
    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    logger.info("variables.........:- {}", variables);

    String alreadyDisbursedPercent;
    String totalDisbAmount = request.get("totalDisbAmount").toString();
    logger.info("totalDisbAmount.........:- {}", totalDisbAmount);
    cmmnRuntimeService.setVariable(caseInstanceId, "totalDisbAmount_hl", totalDisbAmount);

    String currentTranchePercent = request.getOrDefault("currentTranchePercent", "").toString();
    logger.info("currentTranchePercent.........:- {}", currentTranchePercent);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "currentTranchePercent_hl", currentTranchePercent);

    String checkPercentage = request.getOrDefault("checkPercentage", "").toString();
    logger.info("checkPercentage.........:- {}", checkPercentage);
    cmmnRuntimeService.setVariable(caseInstanceId, "checkPercentage", checkPercentage);

    String recommendedStageOfConstruction =
        request.getOrDefault("recommendedStageOfConstruction_hl", "").toString();
    logger.info("recommendedStageOfConstruction_hl.........:- {}", recommendedStageOfConstruction);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "recommendedStageOfConstruction_hl", recommendedStageOfConstruction);

    if (variables.containsKey("alreadyDisbursedPercent")) {
      alreadyDisbursedPercent =
          cmmnRuntimeService.getVariable(caseInstanceId, "alreadyDisbursedPercent").toString();
    } else {
      alreadyDisbursedPercent = "0";
    }
    logger.info("alreadyDisbursedPercent.........:- {}", alreadyDisbursedPercent);

    String disbursalAmountSummaryString =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "disbursalAmountSummary_hl"));
    logger.info("disbursalAmountSummary in string.........:- {}", disbursalAmountSummaryString);
    JSONArray disbursalAmountSummary = new JSONArray();
    try {
      disbursalAmountSummary = new JSONArray(disbursalAmountSummaryString);
    } catch (Exception e) {
    }
    logger.info("disbursalAmountSummary in array.........:- {}", disbursalAmountSummary);

    int amountToBeDisbursed =
        ((Integer.parseInt(currentTranchePercent) - Integer.parseInt(alreadyDisbursedPercent))
                * Integer.parseInt(totalDisbAmount))
            / 100;
    logger.info("amountToBeDisbursed.........:- {}", amountToBeDisbursed);

    int totalNumberOfTranches = disbursalAmountSummary.length();
    logger.info("totalNumberOfTranches.........:- {}", totalNumberOfTranches);
    //    JSONArray data = new JSONArray();
    //    JSONObject finalResponse = new JSONObject();
    //    JSONObject object = new JSONObject();
    //    object.put("totalNumberOfTranches_hl", totalNumberOfTranches);
    //    object.put("alreadyDisbursedPercent", currentTranchePercent);
    //    data.put(object);
    //    finalResponse.put("data", data);

    cmmnRuntimeService.setVariable(
        caseInstanceId, "totalNumberOfTranches2_hl", String.valueOf(totalNumberOfTranches));
    cmmnRuntimeService.setVariable(
        caseInstanceId, "alreadyDisbursedPercent", currentTranchePercent);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "amountToBeDisbursed2_hl", String.valueOf(amountToBeDisbursed));
    cmmnRuntimeService.setVariable(
        caseInstanceId, "amountToBeDisbursedTest", String.valueOf(amountToBeDisbursed));

    Map<String, Object> map = new HashMap<>();
    map.put("totalNumberOfTranches2_hl", totalNumberOfTranches);
    map.put("amountToBeDisbursed2_hl", amountToBeDisbursed);
    return new ApiResponse(HttpStatus.OK, "Success", map);
  }
}
