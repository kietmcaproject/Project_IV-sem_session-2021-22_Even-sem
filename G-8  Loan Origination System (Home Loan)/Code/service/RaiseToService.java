package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RaiseToService {
  private static final Logger logger = LoggerFactory.getLogger(RaiseToService.class);

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public String raiseTo(Map<String, String> request) throws JSONException {
    logger.info("Raise To service task starts..... :- {}", request);
    String caseInstanceId = CommonHelperFunctions.getStringValue(request.get("caseInstanceId"));
    String currentRole = CommonHelperFunctions.getStringValue(request.get("currentRole"));
    logger.info("Current Role..... :- {}", currentRole);
    currentRole = currentRole + "Assignee";
    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    List<String> assigneeList = new ArrayList<>();
    assigneeList.add("DataEntryAssignee");
    assigneeList.add("CML1Assignee");
    assigneeList.add("CML2Assignee");
    assigneeList.add("CML3Assignee");
    assigneeList.add("BCMAssignee");
    assigneeList.add("FCUSamplerAssignee");
    assigneeList.add("NCMAssignee");
    assigneeList.add("BTMAssignee");
    assigneeList.add("ATMAssignee");
    assigneeList.add("RTMAssignee");
    assigneeList.add("InternalLegalManagerAssignee");
    assigneeList.add("BranchManagerAssignee");
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < assigneeList.size(); i++) {
      String assigneeFromList = assigneeList.get(i);
      if (variables.containsKey(assigneeFromList)
          && !(currentRole.equalsIgnoreCase(assigneeFromList))) {
        JSONObject jsonObject = new JSONObject();
        String assignee = CommonHelperFunctions.getStringValue(variables.get(assigneeFromList));
        jsonObject.put("role", assigneeFromList.substring(0, assigneeFromList.length() - 8));
        jsonObject.put("user", assignee);
        jsonArray.put(jsonObject);
      }
    }
    JSONObject pragati = new JSONObject();
    pragati.put("role", "RM");
    pragati.put("user", "Pragati");
    jsonArray.put(pragati);
    return jsonArray.toString();
  }
}
