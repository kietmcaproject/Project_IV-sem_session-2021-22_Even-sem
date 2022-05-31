package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.common.engine.impl.util.Financials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InsurancePremiumService {
  private static final Logger logger = LoggerFactory.getLogger(InsurancePremiumService.class);
  private static final DecimalFormat df = new DecimalFormat("0.00");

  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired IbHelper ibHelper;
  @Autowired FinalLoanAmountService finalLoanAmountService;

  public void calculatePremium(Map<String, Object> request) throws JSONException, UnirestException {
    String caseInstanceId = request.get("caseInstanceId").toString();
    String loanAmount =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "loanAmount_hl"));
    logger.info("Loan Amount from final submit:- {}", loanAmount);
    int insurancePremium = (int) (0.036 * Integer.parseInt(loanAmount));
    cmmnRuntimeService.setVariable(caseInstanceId, "insurancePremium_hl", insurancePremium);
    finalLoanAmountService.calcFinalLoanAmount(request);
    String rate =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "updatedROI"));
    String finalLoanAmount =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "finalLoanAmount"));
    String loanTenure =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "loanTenure_hl"));
    logger.info("final ROI Maximum Capping :- {}", rate);
    logger.info("finalLoanAmount :- {}", finalLoanAmount);
    logger.info("loanTenure :- {}", loanTenure);
    double rateD = Double.parseDouble(rate);
    rateD = rateD / 100;
    logger.info("final ROI Maximum Capping D :- {}", rateD);
    double finalLoanAmountD = Double.parseDouble(finalLoanAmount);
    finalLoanAmountD = -(finalLoanAmountD);
    logger.info("finalLoanAmountD :- {}", finalLoanAmountD);
    int loanTenureD = Integer.parseInt(loanTenure);
    logger.info("loanTenureD :- {}", loanTenureD);
    double pmt = Financials.pmt(rateD, loanTenureD, finalLoanAmountD, 0, 1);
    cmmnRuntimeService.setVariable(caseInstanceId, "emi_hl", df.format(pmt));
    logger.info("PMT VALUE :- {}", pmt);

    //    Map<String, Object> payload = payload(caseInstanceId);
    //    logger.info("payload :- {}", payload);
    //    HttpResponse<String> response =
    //        Unirest.post(ibHelper.getUrl("hdfc-insurance/gcpp-calculator"))
    //            .header("Content-Type", "application/json")
    //            .body(new JSONObject(payload))
    //            .asString();
    //    logger.info("Response :- {}", response.getBody());
    //
    //    Map<String, Object> res =
    // CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    //    return new ApiResponse(HttpStatus.OK, "Success", res);
  }

  private Map<String, Object> payload(String caseInstanceId) throws JSONException {
    String dob =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Dob_hl"));
    String loanDisbDate =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "loanDisbDate"));
    String loanAmount =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "loanAmount_hl"));
    String policyTerm =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "policyTerm"));
    String planId =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "planId"));
    String gender =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Gender_hl"));
    String sumAssured =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "sumAssured"));
    Map<String, Object> payload = new HashMap<>();
    payload.put("dob", "06/19/1988");
    payload.put("loanDisbDate", "03/03/2021");
    payload.put("originalLoanAmount", 180000);
    payload.put("policyTerm", 2);
    payload.put("planId", "012");
    payload.put("gender", "Male");
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("sumAssured", 50000);
    jsonArray.put(jsonObject);
    payload.put("productDataList", jsonArray);

    return payload;
  }

  public ApiResponse checkStatus(Map<String, String> request) {

    return new ApiResponse(HttpStatus.OK, "success");
  }
}
