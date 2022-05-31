package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.CapriUtilService;
import java.text.DecimalFormat;
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

@Service("MultiTrancheTable")
public class MultiTrancheTable {
  private static final Logger logger = LoggerFactory.getLogger(MultiTrancheTable.class);
  private static final DecimalFormat df = new DecimalFormat("0.00");

  @Autowired CapriUtilService capriUtilService;
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public DelegatePlanItemInstance tablePopulation(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    logger.info("Inside multi tranche table....................");
    Map<String, Object> variables = planItemInstance.getVariables();
    String alreadyDisbursedPercent;
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    String totalDisbAmount =
        cmmnRuntimeService.getVariable(caseInstanceId, "totalDisbAmount_hl").toString();
    String currentTranchePercent =
        CommonHelperFunctions.getStringValue(
            planItemInstance.getVariable("currentTranchePercent_hl"));
    logger.info("currentTranchePercent.........:- {}", currentTranchePercent);

    double disbursedPercentageTillDateSummary = 0;
    String disbursalAmountSummaryString =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "disbursalAmountSummary_hl"));
    logger.info("disbursalAmountSummary in string.........:- {}", disbursalAmountSummaryString);
    JSONArray disbursalAmountSummary = new JSONArray();
    try {
      disbursalAmountSummary = new JSONArray(disbursalAmountSummaryString);
    } catch (Exception e) {
    }

    logger.info("disbursalAmountSummary_hl.........:- {}", disbursalAmountSummary);
    JSONObject object = new JSONObject();
    String recommendedStageOfConstruction =
        CommonHelperFunctions.getStringValue(
            planItemInstance.getVariable("recommendedStageOfConstruction_hl"));
    String finalLoanAmount =
        CommonHelperFunctions.getStringValue(planItemInstance.getVariable("finalLoanAmount"));
    logger.info("finalLoanAmount.........:- {}", finalLoanAmount);
    logger.info("recommended Stage Of Construction.........:- {}", recommendedStageOfConstruction);
    object.put("recommendedStageOfConstSummary_hl", recommendedStageOfConstruction);
    object.put("disbursalDate_hl", capriUtilService.getCurrentDate("dd-MM-yyyy"));
    double sumAmountDisbursed =
        (Double.parseDouble(currentTranchePercent) / 100) * Integer.parseInt(finalLoanAmount);
    object.put("disbursedAmountSummary_hl", sumAmountDisbursed);
    logger.info("sumAmountDisbursed 1st........:- {}", sumAmountDisbursed);

    if (!disbursalAmountSummaryString.equalsIgnoreCase("")) {
      logger.info("Inside if condition....................");
      for (int i = 0; i < disbursalAmountSummary.length(); i++) {
        JSONObject object1 = disbursalAmountSummary.getJSONObject(i);
        String amountDisbursed =
            CommonHelperFunctions.getStringValue(object1.get("disbursedAmountSummary_hl"));
        sumAmountDisbursed = sumAmountDisbursed + Integer.parseInt(amountDisbursed);
      }
    }
    logger.info("sumAmountDisbursed.........:- {}", sumAmountDisbursed);

    disbursedPercentageTillDateSummary =
        sumAmountDisbursed / (Double.parseDouble(finalLoanAmount) * 100);
    logger.info(
        "disbursedPercentageTillDateSummary.........:- {}", disbursedPercentageTillDateSummary);

    object.put("disbursedAmountTillDateSummary_hl", sumAmountDisbursed);
    logger.info("after amount disbursed...................");

    try {
      object.put(
          "disbursedPercentageTillDateSummary_hl", df.format(disbursedPercentageTillDateSummary));
    } catch (Exception e) {
    }
    logger.info("after percentage....................");

    try {
      object.put(
          "amountPendingDisbursedSummary_hl",
          Integer.parseInt(finalLoanAmount) - sumAmountDisbursed);
    } catch (Exception e) {
    }
    logger.info("after pending summary....................:- {}", object);

    disbursalAmountSummary.put(object);
    logger.info("disbursalAmountSummary_hl after population.........:- {}", disbursalAmountSummary);
    logger.info("after everything....................");

    cmmnRuntimeService.setVariable(
        caseInstanceId, "disbursalAmountSummary_hl", disbursalAmountSummary.toString());

    return planItemInstance;
  }
}
