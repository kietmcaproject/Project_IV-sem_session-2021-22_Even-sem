package com.kuliza.workbench.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("IncomePopulationService")
public class IncomePopulationService {
  private static final Logger logger = LoggerFactory.getLogger(FinalSubmitApiService.class);

  public DelegateExecution rentalAndPensionIncome(DelegateExecution execution)
      throws JSONException {
    logger.info(" ----------------- IN method : rentalAndPensionIncome --------------------");

    Double aDouble = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();

    String finalSubmitRequestBody = execution.getVariable("finalSubmitRequestBody").toString();
    logger.info("------------- finalSubmitRequestBody : {} ", finalSubmitRequestBody);
    JSONObject finalSubmitJsonObj;
    JSONArray applicants = null;

    try {
      finalSubmitJsonObj = new JSONObject(finalSubmitRequestBody);
      applicants = finalSubmitJsonObj.getJSONArray("applicants");
    } catch (Exception e) {
      e.printStackTrace();
    }
    logger.info("------------ applicants: " + applicants);

    for (int i = 1; i <= applicantCount; i++) {
      JSONArray salarySlipDetailsTable = createSalarySlipTable(i);

      logger.info(
          "------------- applicant{}salarySlipDetailsTable : {}", i, salarySlipDetailsTable);

      for (int j = 0; j < applicants.length(); j++) {
        if (execution
            .getVariable("applicant" + i + "_id")
            .toString()
            .equalsIgnoreCase(applicants.getJSONObject(j).get("applicant_id").toString())) {
          boolean hasRentalIncome =
              Boolean.parseBoolean(
                  applicants
                      .getJSONObject(j)
                      .getJSONObject("secondary_income")
                      .get("has_rental_income")
                      .toString());
          boolean hasPensionIncome =
              Boolean.parseBoolean(
                  applicants
                      .getJSONObject(j)
                      .getJSONObject("secondary_income")
                      .get("has_pension_income")
                      .toString());

          if (hasPensionIncome) {
            Double monthlyPensionIncome =
                Double.parseDouble(
                    applicants
                        .getJSONObject(j)
                        .getJSONObject("secondary_income")
                        .getJSONObject("pension_income")
                        .get("monthly_pension_income")
                        .toString());

            for (int k = 0; k < salarySlipDetailsTable.length(); k++) {
              if (salarySlipDetailsTable
                  .getJSONObject(k)
                  .get("applicant" + i + "SalaryHead_hl")
                  .toString()
                  .equalsIgnoreCase("Pension Income")) {
                salarySlipDetailsTable
                    .getJSONObject(k)
                    .put("applicant" + i + "GrossMonthlyValueEditable_hl", monthlyPensionIncome);
                double grossMonthlyValue =
                    Double.parseDouble(
                        salarySlipDetailsTable
                            .getJSONObject(k)
                            .get("applicant" + i + "GrossMonthlyValueEditable_hl")
                            .toString());
                int percentToConsider =
                    Integer.parseInt(
                        salarySlipDetailsTable
                            .getJSONObject(k)
                            .get("applicant" + i + "PercentageToBeConsidered_hl")
                            .toString());
                double eligibleIncome = grossMonthlyValue * ((double) percentToConsider / 100);
                salarySlipDetailsTable
                    .getJSONObject(k)
                    .put("applicant" + i + "EligibleIncome_hl", eligibleIncome);
              }
            }
          }

          if (hasRentalIncome) {
            Double monthlyRentalIncome =
                Double.parseDouble(
                    applicants
                        .getJSONObject(j)
                        .getJSONObject("secondary_income")
                        .getJSONObject("rental_income")
                        .get("monthly_rental_income")
                        .toString());
            for (int k = 0; k < salarySlipDetailsTable.length(); k++) {
              if (salarySlipDetailsTable
                  .getJSONObject(k)
                  .get("applicant" + i + "SalaryHead_hl")
                  .toString()
                  .equalsIgnoreCase("Rental Income as per current year")) {
                salarySlipDetailsTable
                    .getJSONObject(k)
                    .put("applicant" + i + "GrossMonthlyValueEditable_hl", monthlyRentalIncome);
                double grossMonthlyValue =
                    Double.parseDouble(
                        salarySlipDetailsTable
                            .getJSONObject(k)
                            .get("applicant" + i + "GrossMonthlyValueEditable_hl")
                            .toString());
                int percentToConsider =
                    Integer.parseInt(
                        salarySlipDetailsTable
                            .getJSONObject(k)
                            .get("applicant" + i + "PercentageToBeConsidered_hl")
                            .toString());
                double eligibleIncome = grossMonthlyValue * ((double) percentToConsider / 100);
                salarySlipDetailsTable
                    .getJSONObject(k)
                    .put("applicant" + i + "EligibleIncome_hl", eligibleIncome);
              }
            }
          }
        }
      }
      logger.info(
          "--------------- salarySlipDetails table before set : {}", salarySlipDetailsTable);
      execution.setVariable(
          "applicant" + i + "SalarySlipDetails_hl", salarySlipDetailsTable.toString());
    }
    return execution;
  }

  public JSONArray createSalarySlipTable(int i) throws JSONException {
    JSONArray salarySlipTable = new JSONArray();

    JSONObject row1 = new JSONObject();
    row1.put("applicant" + i + "SalaryHead_hl", "Basic");
    row1.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row2 = new JSONObject();
    row2.put("applicant" + i + "SalaryHead_hl", "HRA");
    row2.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row3 = new JSONObject();
    row3.put("applicant" + i + "SalaryHead_hl", "DA");
    row3.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row4 = new JSONObject();
    row4.put("applicant" + i + "SalaryHead_hl", "CCA");
    row4.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row5 = new JSONObject();
    row5.put("applicant" + i + "SalaryHead_hl", "LTA");
    row5.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row6 = new JSONObject();
    row6.put("applicant" + i + "SalaryHead_hl", "Medical");
    row6.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row7 = new JSONObject();
    row7.put("applicant" + i + "SalaryHead_hl", "Special Allowances");
    row7.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row8 = new JSONObject();
    row8.put("applicant" + i + "SalaryHead_hl", "Conveyance");
    row8.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row9 = new JSONObject();
    row9.put("applicant" + i + "SalaryHead_hl", "Other (Fixed Components)");
    row9.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row10 = new JSONObject();
    row10.put(
        "applicant" + i + "SalaryHead_hl",
        "Gross incentives avg of last 3 months subject to 6 months validation");
    row10.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    JSONObject row11 = new JSONObject();
    row11.put(
        "applicant" + i + "SalaryHead_hl",
        "Annual/half-yearly/ quarterly/ bonus/incentive avg of last 2 years subject to validation from salary slip");
    row11.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    JSONObject row12 = new JSONObject();
    row12.put(
        "applicant" + i + "SalaryHead_hl",
        "Average of 6 months flying allowance (for airline employees)");
    row12.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row13 = new JSONObject();
    row13.put(
        "applicant" + i + "SalaryHead_hl",
        "Fixed reimbursement (cheque) subject to validation from bank credits");
    row13.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row14 = new JSONObject();
    row14.put(
        "applicant" + i + "SalaryHead_hl",
        "Fixed reimbursement (cash) subject to validation from salary slip");
    row14.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    JSONObject row15 = new JSONObject();
    row15.put("applicant" + i + "SalaryHead_hl", "Pension Income");
    row15.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row16 = new JSONObject();
    row16.put("applicant" + i + "SalaryHead_hl", "Rental Income as per current year");
    row16.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    JSONObject row17 = new JSONObject();
    row17.put("applicant" + i + "SalaryHead_hl", "Agricultural Income as Per Current YR");
    row17.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row18 = new JSONObject();
    row18.put("applicant" + i + "SalaryHead_hl", "Agricultural Income as Per Previous YR");
    row18.put("applicant" + i + "PercentageToBeConsidered_hl", "100");

    JSONObject row19 = new JSONObject();
    row19.put("applicant" + i + "SalaryHead_hl", "Interest & Dividend Income as Per Cur YR");
    row19.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    JSONObject row20 = new JSONObject();
    row20.put("applicant" + i + "SalaryHead_hl", "Interest & Dividend Income as per previous year");
    row20.put("applicant" + i + "PercentageToBeConsidered_hl", "50");

    salarySlipTable.put(0, row1);
    salarySlipTable.put(1, row2);
    salarySlipTable.put(2, row3);
    salarySlipTable.put(3, row4);
    salarySlipTable.put(4, row5);
    salarySlipTable.put(5, row6);
    salarySlipTable.put(6, row7);
    salarySlipTable.put(7, row8);
    salarySlipTable.put(8, row9);
    salarySlipTable.put(9, row10);
    salarySlipTable.put(10, row11);
    salarySlipTable.put(11, row12);
    salarySlipTable.put(12, row13);
    salarySlipTable.put(13, row14);
    salarySlipTable.put(14, row15);
    salarySlipTable.put(15, row16);
    salarySlipTable.put(16, row17);
    salarySlipTable.put(17, row18);
    salarySlipTable.put(18, row19);
    salarySlipTable.put(19, row20);

    return salarySlipTable;
  }
}
