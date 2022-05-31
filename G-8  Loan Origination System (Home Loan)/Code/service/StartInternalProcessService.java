package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.AbstractResponse;
import com.kuliza.lending.common.pojo.ApiSuccessResponse;
import com.kuliza.lending.journey.pojo.WorkflowInitiateRequest;
import com.kuliza.lending.journey.services.UserValidationService;
import com.kuliza.lending.journey.services.WorkflowService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartInternalProcessService {
  private static final Logger logger = LoggerFactory.getLogger(StartInternalProcessService.class);
  @Autowired WorkflowService workflowService;
  @Autowired UserValidationService userValidationService;

  public AbstractResponse startProcess(Map<String, String> workflowUserName) throws Exception {
    String workflowName = workflowUserName.get("workflowName");
    String losId = workflowUserName.get("lasid");
    Map<String, Object> processVariables = new HashMap<>();
    processVariables.put("lasid", losId);
    WorkflowInitiateRequest workflowInitiateRequest = new WorkflowInitiateRequest();
    workflowInitiateRequest.setIsNew(true);
    workflowInitiateRequest.setWorkflowName(workflowName);
    workflowInitiateRequest.setProcessVariables(processVariables);
    AbstractResponse createUser =
        userValidationService.validateAndRegisterUserWithoutOTP(null, workflowName, "mobile");
    logger.info("createUser :- {}", createUser.toString());
    Map<String, Object> responseMap = (Map) ((ApiSuccessResponse) createUser).getData();
    logger.info("responseMap :- {}", responseMap);
    logger.info("responseMap :- {}", responseMap.get("username").toString());
    AbstractResponse createProcess =
        workflowService.startOrResumeProcess(
            responseMap.get("username").toString(), workflowInitiateRequest);
    Map<String, Object> responseMapCreatedApplication =
        (Map) ((ApiSuccessResponse) createProcess).getData();
    logger.info("responseMapCreatedApplication :- {}", responseMapCreatedApplication);

    return createProcess;
  }
}
