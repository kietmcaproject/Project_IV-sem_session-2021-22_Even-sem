package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ChangeVariableValueService {
  private static final Logger logger = LoggerFactory.getLogger(ChangeVariableValueService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired RuntimeService runtimeService;

  public ApiResponse changeVariable(Map<String, String> request) {
    String caseInstanceId = request.get("caseInstanceId").toString();
    String processInstanceId = request.get("processInstanceId").toString();
    String varName = request.get("varName").toString();
    String varValue = request.get("varValue").toString();
    String operation = request.get("operation").toString();

    if (!caseInstanceId.equalsIgnoreCase("") && operation.equalsIgnoreCase("fetch-var")) {
      String variableValue = cmmnRuntimeService.getVariable(caseInstanceId, varName).toString();
      return new ApiResponse(HttpStatus.OK, "SUCCESS", variableValue);
    }

    if (!caseInstanceId.equalsIgnoreCase("") && operation.equalsIgnoreCase("edit-var")) {
      cmmnRuntimeService.setVariable(caseInstanceId, varName, varValue);
      return new ApiResponse(HttpStatus.OK, "Variable updated");
    }

    if (!processInstanceId.equalsIgnoreCase("") && operation.equalsIgnoreCase("fetch-var")) {
      String variableValue = runtimeService.getVariable(processInstanceId, varName).toString();
      return new ApiResponse(HttpStatus.OK, "SUCCESS", variableValue);
    }

    if (!processInstanceId.equalsIgnoreCase("") && operation.equalsIgnoreCase("edit-var")) {
      runtimeService.setVariable(processInstanceId, varName, varValue);
      return new ApiResponse(HttpStatus.OK, "Variable updated");
    } else {
      return new ApiResponse(HttpStatus.BAD_REQUEST);
    }
  }
}
