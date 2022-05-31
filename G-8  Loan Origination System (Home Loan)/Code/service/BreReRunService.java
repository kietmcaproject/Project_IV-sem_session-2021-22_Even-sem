package com.kuliza.workbench.service;

import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("BreReRunService")
public class BreReRunService {
  private static final Logger logger = LoggerFactory.getLogger(BreReRunService.class);

  @Autowired private CmmnRuntimeService cmmnRuntimeService;
  @Autowired private RuntimeService runtimeService;

  public DelegatePlanItemInstance breReRun(
      DelegatePlanItemInstance planItemInstance, String workFlowName) {
    logger.info(
        ".............................................Starting.................................................................");

    ProcessInstance processInstance = null;
    processInstance =
        runtimeService.startProcessInstanceByKey(
            workFlowName,
            planItemInstance.getVariable("application_id").toString(),
            planItemInstance.getVariables());
    logger.info("processInstance..................-: {}", processInstance);
    planItemInstance.setVariables(planItemInstance.getVariables());
    logger.info("planItemInstance..................-: {}", planItemInstance);
    return planItemInstance;
  }

  public DelegateExecution inverseBreReRun(DelegateExecution execution) {
    Map<String, Object> variables = execution.getVariables();
    logger.info("variables..................-: {}", variables);
    execution.removeVariable("processInstanceId");
    cmmnRuntimeService.setVariables(execution.getVariable("caseInstanceId").toString(), variables);
    return execution;
  }
}
