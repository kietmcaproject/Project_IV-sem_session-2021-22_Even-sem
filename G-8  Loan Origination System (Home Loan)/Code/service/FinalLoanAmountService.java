package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinalLoanAmountService {
  private static final Logger logger = LoggerFactory.getLogger(FinalLoanAmountService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  private static final int THIRTY_LACS = 3000000;
  private static final int SEVENTY_FIVE_LACS = 7500000;

  public ApiResponse calcFinalLoanAmount(Map<String, Object> request) {
    logger.info("inside calcFinalLoanAmount method");
    String caseId = CommonHelperFunctions.getStringValue(request.get("caseInstanceId"));
    double eligibleLoanAmount =
        Double.parseDouble(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseId, "eligibleLoanAmount1_hl")));
    double technicalValuation =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseId, "totalValuation")));
    double insurancePremium =
        Double.parseDouble(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseId, "insurancePremium_hl")));
    double finalLoanAmount = eligibleLoanAmount + insurancePremium;
    double ltvCurr = (eligibleLoanAmount / technicalValuation) * 100;

    logger.info("eligibleLoanAmount : {}", eligibleLoanAmount);
    logger.info("technicalValuation value : {}", technicalValuation);
    logger.info("finalLoanAmount before calc : {}", finalLoanAmount);
    logger.info("ltvCurrent value : {}", ltvCurr);

    double maxLtv;
    if (finalLoanAmount <= THIRTY_LACS) {
      maxLtv = 90;
    } else if (finalLoanAmount <= SEVENTY_FIVE_LACS) {
      maxLtv = 80;
    } else {
      maxLtv = 75;
    }
    logger.info("maxLtv value : {}", maxLtv);

    double ltvDiff = (ltvCurr - maxLtv) / 100;

    if (ltvDiff > 0) {
      double excessAmount = eligibleLoanAmount * ltvDiff;
      finalLoanAmount = (eligibleLoanAmount - excessAmount) + insurancePremium;
    }
    logger.info("difference b/w maxLtv and ltvCurrent value : {}", ltvDiff);
    logger.info("finalLoanAmount after calc : {}", finalLoanAmount);
    cmmnRuntimeService.setVariable(caseId, "finalLoanAmount", finalLoanAmount);

    return new ApiResponse(200, "Final loan amount calculated", "OK");
  }
}
