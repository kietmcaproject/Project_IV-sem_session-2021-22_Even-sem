package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.AbstractResponse;
import com.kuliza.lending.common.pojo.ApiResponseFactory;
import com.kuliza.lending.common.pojo.ApiSuccessResponse;
import com.kuliza.lending.journey.pojo.WorkflowInitiateRequest;
import com.kuliza.lending.journey.services.UserValidationService;
import com.kuliza.lending.journey.services.WorkflowService;
import com.kuliza.workbench.model.Fields;
import com.kuliza.workbench.model.LoanApplication;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.LoanApplicationRepository;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import com.kuliza.workbench.util.WorkbenchConstants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("LoanApplicationService")
public class LoanApplicationService {
  @Autowired LoanApplicationRepository loanApplicationRepository;
  @Autowired WorkflowService workflowService;
  @Autowired UserValidationService userValidationService;
  @Autowired RuntimeService runtimeService;

  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;
  private static final Logger logger = LoggerFactory.getLogger(LoanApplicationService.class);

  public LoanApplication createNewApplication(Fields fields) throws Exception {
    logger.info("fields:- {}", new Gson().toJson(fields));
    logger.info("InquiryNumber:- {}", fields.getInquiryNumber());
    LoanApplication loanApplication = new LoanApplication();
    WorkflowInitiateRequest workflowInitiateRequest = new WorkflowInitiateRequest();
    try {
      LoanApplication inquiryNumberExists =
          loanApplicationRepository.findByInquiryNumber(fields.getInquiryNumber());
      logger.info("inquiryNumberExists :- {}", inquiryNumberExists.getInquiryNumber());
      logger.info("inquiryNumberExists id :- {}", inquiryNumberExists.getId());
      if (inquiryNumberExists != null) {
        workflowInitiateRequest.setIsNew(false);
        loanApplication.setInquiryNumber(inquiryNumberExists.getInquiryNumber());
        loanApplication.setLanNo(inquiryNumberExists.getLanNo());
        loanApplication.setStatus("Inquiry Number Already Exists");
        loanApplication.setId(inquiryNumberExists.getId());
        loanApplication.setMobileNumber(inquiryNumberExists.getMobileNumber());
        return loanApplication;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    workflowInitiateRequest.setIsNew(true);
    AbstractResponse createUser =
        userValidationService.validateAndRegisterUserWithoutOTP(
            null, fields.getInquiryNumber(), "mobile");
    Map<String, Object> responseMap = (Map) ((ApiSuccessResponse) createUser).getData();
    workflowInitiateRequest.setApplicationNumber(Long.valueOf(fields.getInquiryNumber()));
    workflowInitiateRequest.setWorkflowName("capriJourney1Los");
    Map<String, Object> maps = new HashMap<>();
    maps.put("MobileNumber", fields.getMobileNumber());
    maps.put("inquiryNumber", fields.getInquiryNumber());
    workflowInitiateRequest.setProcessVariables(maps);
    loanApplication.setMobileNumber(fields.getMobileNumber());
    loanApplication.setStatus(WorkbenchConstants.APPLICATION_NEW_STATUS);
    loanApplication.setInquiryNumber(fields.getInquiryNumber());
    Map<String, Object> variables = fields.getVariables();
    String branch = variables.get("branch_id").toString();
    fields.setLanNo(generateLanNo(branch));
    fields.setStatus(loanApplication.getStatus());
    AbstractResponse createProcess =
        workflowService.startOrResumeProcess(
            responseMap.get("username").toString(), workflowInitiateRequest);
    Map<String, Object> responseMapCreatedApplication =
        (Map) ((ApiSuccessResponse) createProcess).getData();
    loanApplication.setLanNo(generateLanNo(branch));
    loanApplication.setProcessInstanceId(
        responseMapCreatedApplication.get("processInstanceId").toString());
    loanApplicationRepository.save(loanApplication);

    int application_id = Integer.parseInt(fields.getInquiryNumber());
    PragatiAppDetails pragatiAppDetails = new PragatiAppDetails();
    pragatiAppDetails.setApplicationId(application_id);
    pragatiAppDetails.setKulizaAppId(loanApplication.getLanNo());

    //    String los_id = loanApplication.getLanNo();
    //    pragatiAppDetails.setProcessInstanceId(los_id);
    pragatiAppDetails.setProcessInstanceId(
        responseMapCreatedApplication.get("processInstanceId").toString());
    //    pragatiAppDetails.setFinalSubmitRequest(object.toString());

    pragatiAppDetailsRepository.save(pragatiAppDetails);

    return loanApplication;
  }

  private String generateLanNo(String branch) {
    String loanType = "HL";
    String application = "AP";
    String lanNo;
    String kulizaId = "KZ";
    PragatiAppDetails pragatiAppDetails = new PragatiAppDetails();
    LoanApplication previousLanNumber = loanApplicationRepository.findTop1ByOrderByCreatedDesc();
    if (previousLanNumber == null) {
      lanNo = application + loanType + branch + kulizaId + "000000001";
      return lanNo;
    } else {
      lanNo = previousLanNumber.getLanNo();
      long lastNineDigits = Long.parseLong(lanNo.substring(lanNo.length() - 9)) + 1;
      String nextLanNo =
          application + loanType + branch + kulizaId + String.format("%09d", lastNineDigits);
      return nextLanNo;
    }
  }

  public AbstractResponse getApplicationDetails(String inquiryNumber) {
    logger.info("fields:- {}", new Gson().toJson(inquiryNumber));
    HashMap<String, Object> variables = new HashMap<>();
    variables.putAll(runtimeService.getVariables(inquiryNumber));
    List<String> removeVariable =
        Arrays.asList(
            "applicationId",
            "assignee",
            "workflowName",
            "rootProcessInstanceId",
            "applicantMobileNumber",
            "initiator",
            "applicationNumber",
            "journeyType",
            "processInstanceId");
    removeVariable.forEach(
        test -> {
          variables.remove(test);
        });
    return ApiResponseFactory.constructApiSuccessResponse(HttpStatus.OK, variables);
  }
}
