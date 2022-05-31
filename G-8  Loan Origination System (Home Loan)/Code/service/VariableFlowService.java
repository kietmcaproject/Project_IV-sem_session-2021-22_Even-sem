package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("VariableFlowService")
public class VariableFlowService {

  private static final Logger logger = LoggerFactory.getLogger(VariableFlowService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public DelegateExecution journeyToPortalPostSanction(DelegateExecution execution) {
    Map<String, Object> variables = execution.getVariables();
    logger.info("variable flow service variables : {}", variables);
    String caseInstanceId =
        CommonHelperFunctions.getStringValue(execution.getVariable("caseInstanceId"));
    logger.info("caseInstanceId : {}", caseInstanceId);
    Map<String, Object> variablesToSave = new HashMap<>();
    variablesToSave.put("sellerName_hl", variables.get("sellerName_hl").toString());
    variablesToSave.put("bankName_hl", variables.get("bankName_hl").toString());
    variablesToSave.put("bankAccountNumber_hl", variables.get("bankAccountNumber_hl").toString());
    variablesToSave.put("ifscCode_hl", variables.get("ifscCode_hl").toString());
    variablesToSave.put(
        "beneficiaryNameWithBank_hl", variables.get("beneficiaryNameWithBank_hl").toString());
    variablesToSave.put("nameMatchPercentage", variables.get("nameMatchPercentage").toString());
    variablesToSave.put(
        "isNameMatchAboveThreshold", variables.get("isNameMatchAboveThreshold").toString());
    variablesToSave.put("nameMatchThreshold", variables.get("nameMatchThreshold").toString());
    variablesToSave.put(
        "triggerCountBankDetails", variables.get("triggerCountBankDetails").toString());
    variablesToSave.put("iseligibleForEnach", variables.get("iseligibleForEnach").toString());
    variablesToSave.put("CML1Status", variables.get("CML1Status").toString());

    logger.info("variables to save : {}", variablesToSave);
    cmmnRuntimeService.setVariables(caseInstanceId, variablesToSave);
    return execution;
  }

  public DelegateExecution journeyToPortalBankDetails(DelegateExecution execution) {
    logger.info(".........Inside journeyToPortalBankDetails");
    Map<String, Object> variables = execution.getVariables();
    logger.info("variables of bank details : {}", variables);
    String oldCaseInstanceId =
        CommonHelperFunctions.getStringValue(execution.getVariable("oldCaseInstanceId"));
    logger.info("caseInstanceId : {}", oldCaseInstanceId);
    Map<String, Object> variablesToSave = new HashMap<>();
    variablesToSave.put("sellerName_hl", variables.get("sellerName_hl").toString());
    variablesToSave.put("bankName_hl", variables.get("bankName_hl").toString());
    variablesToSave.put("bankAccountNumber_hl", variables.get("bankAccountNumber_hl").toString());
    variablesToSave.put("ifscCode_hl", variables.get("ifscCode_hl").toString());
    variablesToSave.put("iseligibleForEnach", variables.get("iseligibleForEnach").toString());
    variablesToSave.put("triggerPostSanction", variables.get("triggerPostSanction").toString());

    logger.info("variables to save : {}", variablesToSave);
    cmmnRuntimeService.setVariables(oldCaseInstanceId, variablesToSave);
    return execution;
  }

  public DelegateExecution journeyToPortalFcu(DelegateExecution execution) {
    Map<String, Object> variables = execution.getVariables();
    logger.info("variable flow service variables : {}", variables);
    String oldCaseInstanceId =
        CommonHelperFunctions.getStringValue(execution.getVariable("oldCaseInstanceId"));
    logger.info("caseInstanceId : {}", oldCaseInstanceId);
    Map<String, Object> variablesToSave = new HashMap<>();
    variablesToSave.put("FCUreport", variables.get("FCUreport"));
    variablesToSave.put("finalStatusFcuVendor", variables.get("finalStatusFcuVendor").toString());
    variablesToSave.put("FCUVendorRemarks", variables.get("FCUVendorRemarks").toString());
    logger.info("variables to save : {}", variablesToSave);
    cmmnRuntimeService.setVariables(oldCaseInstanceId, variablesToSave);

    return execution;
  }

  public DelegateExecution journeyToPortalLegal(DelegateExecution execution) {
    Map<String, Object> variables = execution.getVariables();
    logger.info("variable flow service variables : {}", variables);
    String oldCaseInstanceId =
        CommonHelperFunctions.getStringValue(execution.getVariable("oldCaseInstanceId"));
    logger.info("caseInstanceId : {}", oldCaseInstanceId);
    Map<String, Object> variablesToSave = new HashMap<>();
    variablesToSave.put("legalOpinionReport", variables.get("legalOpinionReport"));
    variablesToSave.put("searchReport", variables.get("searchReport"));
    variablesToSave.put(
        "externalLawyerDecision", variables.get("externalLawyerDecision").toString());
    variablesToSave.put("externalLawyerRemarks", variables.get("externalLawyerRemarks").toString());
    variablesToSave.put("legalQueryType", variables.get("legalQueryType").toString());
    variablesToSave.put("nameOfTheDocument", variables.get("nameofthedocument").toString());
    variablesToSave.put(
        "queryReasonExternalLawyer", variables.get("queryReasonExternalLawyer").toString());

    logger.info("variables to save : {}", variablesToSave);
    cmmnRuntimeService.setVariables(oldCaseInstanceId, variablesToSave);

    return execution;
  }
}
