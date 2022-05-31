package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.LoanApplication;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.LoanApplicationRepository;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DsaAppService {
  private static final Logger logger = LoggerFactory.getLogger(DsaAppService.class);

  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Autowired RuntimeService runtimeService;

  @Autowired LoanApplicationRepository loanApplicationRepository;

  public ApiResponse getLeadStatus(Map<String, String> request) {
    String ucic = request.get("ucic");
    logger.info("given customer ucic : {}", ucic);
    String ucicNumber = "";
    PragatiAppDetails pragatiAppDetails = new PragatiAppDetails();
    try {
      pragatiAppDetails = pragatiAppDetailsRepository.findByUcic(ucic);
      ucicNumber = pragatiAppDetails.getUcic();
      logger.info("pragatiAppDetails : {}", pragatiAppDetails);
    } catch (Exception e) {
      logger.info("Error while querying ucic number");
      e.printStackTrace();
    }

    if (!ucicNumber.equals("")) {
      Map<String, Object> response = new HashMap<>();
      LoanApplication loanApplication =
          loanApplicationRepository.findByInquiryNumber(
              String.valueOf(pragatiAppDetails.getApplicationId()));
      String processInstanceId = loanApplication.getProcessInstanceId();
      String dsaAppStatus =
          CommonHelperFunctions.getStringValue(
              runtimeService.getVariable(processInstanceId, "dsaAppStatus"));
      response.put("ucic", ucicNumber);
      response.put("applicationNumber", loanApplication.getLanNo());
      response.put("loanStatus", dsaAppStatus);
      response.put("product", "Home Loan");
      response.put("lan", runtimeService.getVariable(processInstanceId, "loanId_hl"));
      response.put("loanAmount", runtimeService.getVariable(processInstanceId, "loanAmount_hl"));
      logger.info("Response sent to DSA app : {}", response);
      return new ApiResponse(HttpStatus.OK, "Success", response);
    } else {
      return new ApiResponse(HttpStatus.NOT_FOUND, "Customer Number does not exist");
    }
  }
}
