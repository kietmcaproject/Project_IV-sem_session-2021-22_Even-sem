package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("RaiseQueryService")
public class RaiseQueryService {
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  private static final Logger logger = LoggerFactory.getLogger(RaiseQueryService.class);

  public DelegatePlanItemInstance raiseQuery(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    Map<String, Object> submit = planItemInstance.getVariables();
    logger.info("All variables :- {}", submit);
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    JSONArray jsonArray = new JSONArray();
    try {
      jsonArray =
          CommonHelperFunctions.objectToJSONArray(
              cmmnRuntimeService.getVariable(caseInstanceId, "listOfQueries_hl"));
    } catch (Exception e) {
    }

    String modalQueryRaiseTo =
        CommonHelperFunctions.getStringValue(submit.get("modalQueryRaiseTo_hl"));
    logger.info("modalQueryRaiseTo :- {}", modalQueryRaiseTo);
    String roleName = CommonHelperFunctions.getStringValue(submit.get("roleName_hl"));
    logger.info("roleName :- {}", roleName);

    if (!modalQueryRaiseTo.equalsIgnoreCase("")) {
      cmmnRuntimeService.setVariable(caseInstanceId, "modalQueryRaiseToCopy_hl", modalQueryRaiseTo);
      cmmnRuntimeService.setVariable(caseInstanceId, "roleNameCopy_hl", roleName);
    }
    String modalQueryRaiseToCopy =
        CommonHelperFunctions.getStringValue(submit.get("modalQueryRaiseToCopy_hl"));
    String roleNameCopy = CommonHelperFunctions.getStringValue(submit.get("roleNameCopy_hl"));

    cmmnRuntimeService.setVariable(
        caseInstanceId, roleNameCopy + "Assignee", modalQueryRaiseToCopy);
    cmmnRuntimeService.setVariable(caseInstanceId, roleNameCopy + "Status", "queriesforme");
    JSONObject jsonObject = new JSONObject();
    String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    jsonObject.put("queryTimeStamp_hl", currentDate);
    jsonObject.put(
        "queryRaiseBy_hl",
        CommonHelperFunctions.getStringValue(submit.get("modalQueryRaiseBy_hl")));
    try {
      jsonObject.put("queryRaiseTo_hl", modalQueryRaiseTo);
    } catch (Exception e) {
    }
    jsonObject.put("queryApplicantName_hl", submit.get("modalQueryApplicantName_hl").toString());
    jsonObject.put("queryRegarding_hl", submit.get("modalQueryRegarding_hl").toString());
    jsonObject.put("queryRemarks_hl", submit.get("modalQueryRemarks_hl").toString());
    jsonObject.put("queryRoleName_hl", roleName);

    jsonArray.put(jsonObject);
    if (!submit.get("modalQueryRaiseBy_hl").toString().equals("")) {
      cmmnRuntimeService.setVariable(
          planItemInstance.getCaseInstanceId(), "listOfQueries_hl", jsonArray.toString());
    }
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "modalQueryRaiseBy_hl", "");
    try {
      cmmnRuntimeService.setVariable(
          planItemInstance.getCaseInstanceId(), "modalQueryRaiseTo_hl", "");
    } catch (Exception e) {
    }
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "modalQueryApplicantName_hl", "");
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "modalQueryRegarding_hl", "");
    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "modalQueryRemarks_hl", "");
    cmmnRuntimeService.setVariable(planItemInstance.getCaseInstanceId(), "roleName_hl", "");
    cmmnRuntimeService.setVariable(planItemInstance.getCaseInstanceId(), "queryMode_hl", "on");
    return planItemInstance;
  }
}
