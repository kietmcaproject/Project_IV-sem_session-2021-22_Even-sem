package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("FoirCalculationService")
public class FoirCalculationService {
  private static final Logger logger = LoggerFactory.getLogger(FoirCalculationService.class);

  @Autowired RuntimeService runtimeService;

  public DelegateExecution calculate(DelegateExecution execution) throws JSONException {
    logger.info("Inside the foirCalculation service ----------------");
    String processInstanceId = execution.getProcessInstanceId();
    double applicantCount =
        Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    double proposedEMI = Double.parseDouble(execution.getVariable("proposed_EMI").toString());
    double monthlyObligation_EMI = 0d;
    double totalEligibleIncome = 0d;
    double foir = 0d;

    for (int i = 1; i <= applicantCount; i++) {
      String applicantType =
          CommonHelperFunctions.getStringValue(
              execution.getVariable("applicant" + i + "ApplicantType_hl"));
      if (applicantType.equalsIgnoreCase("Primary Applicant")
          || applicantType.equalsIgnoreCase("Financial Co-Applicant")) {
        Object mOTableObj =
            runtimeService.getVariable(processInstanceId, "applicant1MonthlyObligationTable_hl");
        logger.info("monthlyObligationObj : {}", mOTableObj);
        JSONArray obligationTable = null;
        try {
          obligationTable =
              CommonHelperFunctions.objectToJSONArray(
                  runtimeService.getVariable(
                      processInstanceId, "applicant1MonthlyObligationTable_hl"));
          logger.info("applicant1obligationTable :- {}", obligationTable);
        } catch (Exception e) {
          logger.info("applicant1obligationTable printstacktrace start....");
          e.printStackTrace();
        }

        try {
          JSONArray obligationTableExe =
              CommonHelperFunctions.objectToJSONArray(
                  execution.getVariable("applicant1MonthlyObligationTable_hl"));
          logger.info("applicant1obligationTableExe :- {}", obligationTableExe);
        } catch (Exception e) {
          logger.info("applicant1obligationTableExe printstacktrace start...");
          e.printStackTrace();
        }

        try {
          String obligationTableStr =
              execution.getVariable("applicant1MonthlyObligationTable_hl").toString();
          JSONArray obligationTableFStr = new JSONArray(obligationTableStr);
          logger.info("obligationTableFStr :- {}", obligationTableFStr);
        } catch (Exception e) {
          logger.info("obligationTableFStr printstacktrace start....");
          e.printStackTrace();
        }

        if (!obligationTable.toString().equals("")
            && !obligationTable.toString().equals("[]")
            && obligationTable.length() > 0) {
          for (int j = 1; j <= obligationTable.length(); j++) {
            JSONObject row = obligationTable.getJSONObject(j - 1);
            if (row.get("applicant" + j + "ObligLoanStatus_hl").toString().equalsIgnoreCase("LIVE")
                && row.get("applicant" + j + "ObligConsider_hl")
                    .toString()
                    .equalsIgnoreCase("YES")) {
              double emiAmount =
                  Double.parseDouble(
                      execution.getVariable("applicant" + j + "ObligEmi_hl").toString());
              monthlyObligation_EMI += emiAmount;
            }
          }
        }
      }
      double eligibleIncome =
          Double.parseDouble(execution.getVariable("totalEligibleIncome" + i).toString());
      totalEligibleIncome += eligibleIncome;
    }

    logger.info("------------ proposedEMI: {}", proposedEMI);
    logger.info("------------ monthlyObligation_EMI: {}", monthlyObligation_EMI);
    logger.info("------------ totalEligibleIncome: {}", totalEligibleIncome);

    foir = (proposedEMI + monthlyObligation_EMI) / totalEligibleIncome;

    logger.info("------------ foir: {}", foir);

    execution.setVariable("Foir_hl", foir);
    execution.setVariable("monthlyObligation_EMI", monthlyObligation_EMI);

    return execution;
  }

  public static void main(String[] args) throws JSONException {
    Map<String, Object> map = new HashMap<>();
    map.put("a", 11);
    map.put("b", 11);
    map.put("c", 11);
    Map<String, Object> list = new HashMap<>();
    list.put("1", "1");
    list.put("3", "1");
    list.put("2", "1");
    map.put("list", list);
    map.put("d", 11);
    System.out.println(map);

    String stringToBeConverted =
        "[\n"
            + "  {\n"
            + "    applicant1FinancerName_hl=NOTDISCLOSED,\n"
            + "    applicant1ObligLoanNumber_hl=28914802,\n"
            + "    applicant1ObligLoanType_hl=10,\n"
            + "    applicant1ObligLoanAmount_hl=0,\n"
            + "    applicant1ObligLoanYear_hl=2022-05-03,\n"
            + "    applicant1ObligEmi_hl=10000,\n"
            + "    applicant1ObligTenure_hl=0,\n"
            + "    applicant1obligEmiNumber_hl=0,\n"
            + "    applicant1ObligPendingEmi_hl=0,\n"
            + "    applicant1ObligLoanStatus_hl=Live,\n"
            + "    applicant1ObligConsider_hl=Yes,\n"
            + "    applicant1ReasonForNotObligating_hl=null,\n"
            + "    applicant1ObligEmiDate_hl=2022-05-11,\n"
            + "    applicant1Roi_hl=1,\n"
            + "    applicant1Pos_hl=1770,\n"
            + "    applicant1TotalBounce_hl=0,\n"
            + "    applicant1Remark_hl=Na,\n"
            + "    key=0\n"
            + "  },\n"
            + "  {\n"
            + "    applicant1FinancerName_hl=NOTDISCLOSED,\n"
            + "    applicant1ObligLoanNumber_hl=28914802,\n"
            + "    applicant1ObligLoanType_hl=10,\n"
            + "    applicant1ObligLoanAmount_hl=1002,\n"
            + "    applicant1ObligLoanYear_hl=2022-05-04,\n"
            + "    applicant1ObligEmi_hl=10000,\n"
            + "    applicant1ObligTenure_hl=0,\n"
            + "    applicant1obligEmiNumber_hl=0,\n"
            + "    applicant1ObligPendingEmi_hl=0,\n"
            + "    applicant1ObligLoanStatus_hl=Live,\n"
            + "    applicant1ObligConsider_hl=Yes,\n"
            + "    applicant1ReasonForNotObligating_hl=null,\n"
            + "    applicant1ObligEmiDate_hl=2022-05-12,\n"
            + "    applicant1Roi_hl=1,\n"
            + "    applicant1Pos_hl=1002,\n"
            + "    applicant1TotalBounce_hl=0,\n"
            + "    applicant1Remark_hl=Na,\n"
            + "    key=1\n"
            + "  }\n"
            + "]";

    System.out.println("String to be converted : " + stringToBeConverted);

    String gsonString = new Gson().toJson(stringToBeConverted, String.class);
    String unescapeString = StringEscapeUtils.unescapeJavaScript(gsonString);
    System.out.println("gsonString :" + gsonString);
    System.out.println("unescapeString : " + unescapeString);
    JSONArray jsonArray = new JSONArray(gsonString);
    System.out.println(gsonString);
  }
}
