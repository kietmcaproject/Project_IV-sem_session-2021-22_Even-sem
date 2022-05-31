package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("FactoringDeviationsService")
public class FactoringDeviationsService {
  private static final Logger logger = LoggerFactory.getLogger(FactoringDeviationsService.class);

  private static final HashMap<String, Double> DEVIATION_GRID = new HashMap<>();
  private static final double MAXIMUM_PREMIUM = 0.75;
  @Autowired RuntimeService runtimeService;

  static {
    DEVIATION_GRID.put("D1", 0.50);
    DEVIATION_GRID.put("D2", 0.25);
    DEVIATION_GRID.put("D3", 0.25);
    DEVIATION_GRID.put("D4", 0.25);
    DEVIATION_GRID.put("D5", 0.50);
    DEVIATION_GRID.put("D6", 0.25);
  }

  public DelegateExecution deviationCheck(DelegateExecution execution) throws JSONException {
    String processInstanceId = execution.getProcessInstanceId();
    Map<String, Object> variables = execution.getVariables();
    double priceUptickSummation = 0d;

    String d1PropertyInCommDomArea = "";
    String d2EmiChequeBounce = "No";
    String d3AgeOfPropertyBeyond40Years = "";
    String d4PropertyLocatedInLessThan50PerDevArea = "";
    String d5LoanAmountExceeding200PerOfLandValue = "";
    String d6FcuOutputIsReferToCredit = "";

    String communityDominatedArea =
        variables.getOrDefault("communityDominatedArea_hl", "Yes").toString();
    String ageOfProperty = variables.getOrDefault("ageOfProperty_hl", 41).toString();
    String propertyLowDevelopment =
        variables.getOrDefault("propertyLowDevelopment_hl", "Yes").toString();
    String averageSurroundingDevelopment =
        variables.getOrDefault("averageSurroundingDevelopment_hl", 45).toString();
    String propertyUnitType = variables.getOrDefault("propertyUnitType_hl", "Apartment").toString();
    String totalValuation = variables.getOrDefault("totalValuation", 100000).toString();
    String loanAmount = variables.getOrDefault("loanAmount_hl", "").toString();
    String finalStatusFcuVendor =
        variables.getOrDefault("finalStatusFcuVendor", "Fraud").toString();
    String decisionFCU = variables.getOrDefault("decisionFCU", "Refer to Credit").toString();

    String applicant1MonthlyObligationTable =
        CommonHelperFunctions.getStringValue(
            runtimeService.getVariable(processInstanceId, "applicant1MonthlyObligationTable_hl"));
    logger.info("applicant1MonthlyObligationTable_hl :- {}", applicant1MonthlyObligationTable);
    //    JSONArray jsonArray = new JSONArray(applicant1MonthlyObligationTable);
    JSONArray jsonArray =
        CommonHelperFunctions.objectToJSONArray(
            runtimeService.getVariable(processInstanceId, "applicant1MonthlyObligationTable_hl"));
    logger.info("applicant1MonthlyObligationTable_hl :- {}", jsonArray);

    if (!applicant1MonthlyObligationTable.equalsIgnoreCase("")) {
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        String applicant1ReasonForNotObligating = "";
        String applicant1TotalBounce = "";
        try {
          applicant1ReasonForNotObligating =
              CommonHelperFunctions.getStringValue(
                  jsonObject.get("applicant1ReasonForNotObligating_hl"));
        } catch (JSONException e) {
        }
        try {
          applicant1TotalBounce =
              CommonHelperFunctions.getStringValue(jsonObject.get("applicant1TotalBounce_hl"));
        } catch (JSONException e) {
        }
        if (!applicant1TotalBounce.equalsIgnoreCase("")) {
          if (applicant1ReasonForNotObligating.equalsIgnoreCase("BT Loan")
              && Integer.parseInt(applicant1TotalBounce) > 0) {
            d2EmiChequeBounce = "Yes";
          }
        }
      }
    }

    logger.info("Checking condition for deviation....................");

    if (communityDominatedArea.equalsIgnoreCase("Yes")) {
      d1PropertyInCommDomArea = "Yes";
    }

    if (Integer.parseInt(ageOfProperty) > 40) {
      d3AgeOfPropertyBeyond40Years = "Yes";
    }

    if (propertyLowDevelopment.equalsIgnoreCase("Yes")
        && Integer.parseInt(averageSurroundingDevelopment) < 50) {
      d4PropertyLocatedInLessThan50PerDevArea = "Yes";
    }

    if (!propertyUnitType.equalsIgnoreCase("Apartment")) {
      if (Double.parseDouble(loanAmount) > (Double.parseDouble(totalValuation) * 2)) {
        d5LoanAmountExceeding200PerOfLandValue = "Yes";
      }
    }

    if (finalStatusFcuVendor.equalsIgnoreCase("Fraud")
        && decisionFCU.equalsIgnoreCase("Refer to Credit")) {
      d6FcuOutputIsReferToCredit = "Yes";
    }

    logger.info("Checking if condition is true for deviation....................");
    StringBuilder deviationsShown = new StringBuilder();
    if (d1PropertyInCommDomArea.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D1");
      deviationsShown.append(" 1 ");
    }

    if (d2EmiChequeBounce.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D2");
      deviationsShown.append(" 2 ");
    }

    if (d3AgeOfPropertyBeyond40Years.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D3");
      deviationsShown.append(" 3 ");
    }

    if (d4PropertyLocatedInLessThan50PerDevArea.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D4");
      deviationsShown.append(" 4 ");
    }

    if (d5LoanAmountExceeding200PerOfLandValue.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D5");
      deviationsShown.append(" 5 ");
    }

    if (d6FcuOutputIsReferToCredit.equalsIgnoreCase("Yes")) {
      priceUptickSummation += DEVIATION_GRID.get("D6");
      deviationsShown.append(" 6 ");
    }

    logger.info("priceUptickSummation :- {} ", priceUptickSummation);
    logger.info("deviationsShown : {}", deviationsShown);
    runtimeService.setVariable(processInstanceId, "deviationsShown_hl", deviationsShown);

    priceUptickSummation = Math.min(priceUptickSummation, MAXIMUM_PREMIUM);
    logger.info("priceUptickSummationAfterMin :- {} ", priceUptickSummation);

    String roiPL = variables.getOrDefault("roiPriceLoan_hl", "").toString();
    logger.info("roiPL from case : {}", roiPL);

    double roiFD = Double.parseDouble(roiPL);
    logger.info("roiFD from case : {}", roiFD);

    roiFD += priceUptickSummation;
    logger.info("roiPL :- {} ", roiFD);

    runtimeService.setVariable(processInstanceId, "roiFD_hl", roiFD);
    return execution;
  }

  //  public static void main(String[] args) {
  //    double priceUptickSummation = 0d;
  //
  //    String d1PropertyInCommDomArea = "";
  //    String d2EmiChequeBounce = "";
  //    String d3AgeOfPropertyBeyond40Years = "";
  //    String d4PropertyLocatedInLessThan50PerDevArea = "";
  //    String d5LoanAmountExceeding200PerOfLandValue = "";
  //    String d6FcuOutputIsReferToCredit = "";
  //
  //    String communityDominatedArea = "Yes";
  //    String ageOfProperty = "50";
  //    String propertyLowDevelopment = "Yes";
  //    String averageSurroundingDevelopment = "70";
  //    String propertyUnitType = "Aparment";
  //    String totalValuation = "240000";
  //    String loanAmount = "500000";
  //    String finalStatusFcuVendor = "Fraud";
  //    String decisionFCU = "Refer to Credit";
  //
  //    logger.info("Checking condition for deviation....................");
  //
  //    if (communityDominatedArea.equalsIgnoreCase("Yes")) {
  //      d1PropertyInCommDomArea = "Yes";
  //    }
  //
  //    if (Integer.parseInt(ageOfProperty) > 40) {
  //      d3AgeOfPropertyBeyond40Years = "Yes";
  //    }
  //
  //    if (propertyLowDevelopment.equalsIgnoreCase("Yes") &&
  // Integer.parseInt(averageSurroundingDevelopment) < 50) {
  //      d4PropertyLocatedInLessThan50PerDevArea = "Yes";
  //    }
  //
  //    if (!propertyUnitType.equalsIgnoreCase("Apartment")) {
  //      if (Double.parseDouble(loanAmount) > (Double.parseDouble(totalValuation) * 2)) {
  //        d5LoanAmountExceeding200PerOfLandValue = "Yes";
  //      }
  //
  //    }
  //
  //    if (finalStatusFcuVendor.equalsIgnoreCase("Fraud")
  //            && decisionFCU.equalsIgnoreCase("Refer to Credit")) {
  //      d6FcuOutputIsReferToCredit = "Yes";
  //    }
  //
  //
  //    logger.info("Checking if condition is true for deviation....................");
  //
  //    if (d1PropertyInCommDomArea.equalsIgnoreCase("Yes")) {
  //      priceUptickSummation += DEVIATION_GRID.get("D1");
  //    }
  //
  //    if (d3AgeOfPropertyBeyond40Years.equalsIgnoreCase("Yes")) {
  //      priceUptickSummation += DEVIATION_GRID.get("D3");
  //    }
  //
  //    if (d4PropertyLocatedInLessThan50PerDevArea.equalsIgnoreCase("Yes")) {
  //      priceUptickSummation += DEVIATION_GRID.get("D4");
  //    }
  //
  //    if (d5LoanAmountExceeding200PerOfLandValue.equalsIgnoreCase("Yes")) {
  //      priceUptickSummation += DEVIATION_GRID.get("D5");
  //    }
  //
  //    if (d6FcuOutputIsReferToCredit.equalsIgnoreCase("Yes")) {
  //      priceUptickSummation += DEVIATION_GRID.get("D6");
  //    }
  //
  ////    if (d6FcuOutputIsReferToCredit.equalsIgnoreCase("Yes")) {
  ////      double d6_priceUptick = DEVIATION_GRID.get("D6");
  ////      priceUptickSummation += d6_priceUptick;
  ////    }
  //    logger.info("priceUptickSummation :- {} ", priceUptickSummation);
  //
  //    priceUptickSummation = Math.min(priceUptickSummation, MAXIMUM_PREMIUM);
  //    logger.info("priceUptickSummationAfterMin :- {} ", priceUptickSummation);
  //
  //    String roiPL = "12";
  //    double roiFD = Double.parseDouble(roiPL);
  //    roiFD += priceUptickSummation;
  //    logger.info("roiPL :- {} ", roiFD);
  //
  //    System.out.println(roiFD);
  //  }

}
