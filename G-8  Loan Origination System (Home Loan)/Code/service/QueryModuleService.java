package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.AbstractResponse;
import com.kuliza.lending.common.pojo.ApiSuccessResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.lending.journey.pojo.WorkflowInitiateRequest;
import com.kuliza.lending.journey.services.UserValidationService;
import com.kuliza.lending.journey.services.WorkflowService;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryModuleService {

  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;

  @Autowired RuntimeService runtimeService;

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  private static final Logger logger = LoggerFactory.getLogger(QueryModuleService.class);

  @Autowired WorkflowService workflowService;
  @Autowired UserValidationService userValidationService;

  public void queryModuleApi(Map<String, Object> request) throws Exception {
    String caseInstanceId = CommonHelperFunctions.getStringValue(request.get("caseInstanceId"));
    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    JSONArray jsonArray = new JSONArray();
    try {
      jsonArray = CommonHelperFunctions.objectToJSONArray(variables.get("listOfQueries_hl"));
      logger.info("list of queries : {}", jsonArray);
    } catch (Exception e) {
      logger.info("error while converting list of query table value to jsonArray format");
      e.printStackTrace();
    }
    String workflowName = request.get("workflowName").toString();
    WorkflowInitiateRequest workflowInitiateRequest = new WorkflowInitiateRequest(workflowName);
    workflowInitiateRequest.setIsNew(true);
    AbstractResponse createUser =
        userValidationService.validateAndRegisterUserWithoutOTP(null, workflowName, "mobile");
    logger.info("createUser : {}", createUser);
    Map<String, Object> responseMap = (Map) ((ApiSuccessResponse) createUser).getData();
    logger.info("responseMap :- {}", responseMap);
    logger.info("responseMap :- {}", responseMap.get("username").toString());
    AbstractResponse createProcess =
        workflowService.startOrResumeProcess(
            responseMap.get("username").toString(), workflowInitiateRequest);
    Map<String, Object> responseMapCreatedApplication =
        (Map) ((ApiSuccessResponse) createProcess).getData();
    logger.info("responseMapCreatedApplication :- {}", responseMapCreatedApplication);
  }

  public DelegateExecution addQuery(DelegateExecution execution) throws JSONException {
    String processInstanceId = execution.getProcessInstanceId();
    String applicationNo =
        CommonHelperFunctions.getStringValue(execution.getVariable("los_id")); // los_id , loanId_hl
    PragatiAppDetails pragatiAppDetails =
        pragatiAppDetailsRepository.findByKulizaAppId(applicationNo);
    String caseId =
        (String)
            runtimeService.getVariable(pragatiAppDetails.getProcessInstanceId(), "caseInstanceId");
    JSONArray queriesTable =
        CommonHelperFunctions.objectToJSONArray(
            cmmnRuntimeService.getVariable(caseId, "listOfQueries_hl"));

    JSONArray newQueriesTable = new JSONArray();
    if (queriesTable.length() != 0) {
      for (int i = 0; i < queriesTable.length(); i++) {
        JSONObject queryRow = queriesTable.getJSONObject(i);
        JSONObject newQueryRow = new JSONObject();
        newQueryRow.put(
            "queryRegarding_hl", cmmnRuntimeService.getVariable(caseId, "queryRegarding_hl"));
        newQueryRow.put(
            "queryApplicantName_hl",
            cmmnRuntimeService.getVariable(caseId, "queryApplicantName_hl"));
        newQueryRow.put(
            "queryRemarks_hl", cmmnRuntimeService.getVariable(caseId, "queryRemarks_hl"));
        newQueryRow.put(
            "queryRaiseBy_hl", cmmnRuntimeService.getVariable(caseId, "queryRaiseBy_hl"));
        newQueryRow.put(
            "queryId_hl", queryRow.get("queryId_hl")); // TODO: ask Kshitiz to create 'queryId_hl'
        newQueryRow.put("newQuery_hl", "added");
        newQueriesTable.put(newQueryRow);
      }
      execution.setVariable("listOfQueries_hl", newQueriesTable.toString());
    }
    return execution;
  }

  public DelegateExecution updateQuery(DelegateExecution execution) throws JSONException {

    String processInstanceId = execution.getProcessInstanceId();
    String applicationNo =
        CommonHelperFunctions.getStringValue(execution.getVariable("applicationNo"));
    PragatiAppDetails pragatiAppDetails =
        pragatiAppDetailsRepository.findByKulizaAppId(applicationNo);
    String caseId =
        (String)
            runtimeService.getVariable(pragatiAppDetails.getProcessInstanceId(), "caseInstanceId");
    String oldCaseId = (String) runtimeService.getVariable(processInstanceId, "caseInstanceId");
    JSONArray queriesTable =
        CommonHelperFunctions.objectToJSONArray(
            cmmnRuntimeService.getVariable(caseId, "listOfQueries_hl"));
    JSONArray oldQueriesTable =
        CommonHelperFunctions.objectToJSONArray(
            cmmnRuntimeService.getVariable(oldCaseId, "listOfQueries_hl"));

    if (oldQueriesTable.length() == queriesTable.length()) {
      execution.setVariable("diffQuerySize", true);
      return execution;
    }
    execution.setVariable("diffQuerySize", false);
    JSONArray newQueriesTable = new JSONArray();
    if (queriesTable.length() != 0) {
      for (int i = 0; i < queriesTable.length(); i++) {
        JSONObject queryRow = queriesTable.getJSONObject(i);
        for (int j = 0; j < oldQueriesTable.length(); j++) {
          JSONObject oldQueryRow = oldQueriesTable.getJSONObject(j);

          if (!(oldQueryRow.get("queryId_hl").equals(queryRow.get("queryId_hl")))
              && oldQueryRow.get("newQuery_hl").equals("added")) {
            JSONObject newQueryRow = new JSONObject();
            newQueryRow.put(
                "queryRegarding_hl", cmmnRuntimeService.getVariable(caseId, "queryRegarding_hl"));
            newQueryRow.put(
                "queryApplicantName_hl",
                cmmnRuntimeService.getVariable(caseId, "queryApplicantName_hl"));
            newQueryRow.put(
                "queryRemarks_hl", cmmnRuntimeService.getVariable(caseId, "queryRemarks_hl"));
            newQueryRow.put(
                "queryRaiseBy_hl", cmmnRuntimeService.getVariable(caseId, "queryRaiseBy_hl"));
            newQueryRow.put(
                "queryId_hl",
                queryRow.get("queryId_hl")); // TODO: ask Kshitiz to create 'queryId_hl'
            newQueryRow.put("newQuery_hl", "added");
            newQueriesTable.put(newQueryRow);
          }
        }
      }
      execution.setVariable("listOfQueries_hl", newQueriesTable.toString());
    }
    return execution;
  }

  //  public static void main(String[] args) throws JSONException {
  //    JSONArray queriesTableC = new JSONArray("[\n"
  //      + "  {\n"
  //      + "    \"queryRaiseTo_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryRaiseBy_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryTimeStamp_hl\": \"25-04-2022\",\n"
  //      + "    \"queryApplicantName_hl\": \"Mayank\",\n"
  //      + "    \"queryRemarks_hl\": \"no available\",\n"
  //      + "    \"queryRegarding_hl\": \"Entries Mis-Match\",\n"
  //      + "    \"queryId_hl\": \"3\"\n"
  //      + "  },\n"
  //      + "  {\n"
  //      + "    \"queryRaiseTo_hl\": \"Pragati\",\n"
  //      + "    \"queryRaiseBy_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryTimeStamp_hl\": \"25-04-2022\",\n"
  //      + "    \"queryApplicantName_hl\": \"Ranjani\",\n"
  //      + "    \"queryRemarks_hl\": \"no available\",\n"
  //      + "    \"queryRegarding_hl\": \"Irrelavant Document\",\n"
  //      + "    \"queryId_hl\": \"2\"\n"
  //      + "  }\n"
  //      + "]");
  //
  //    JSONArray queriesTableP = new JSONArray("[\n"
  //      + "  {\n"
  //      + "    \"queryRaiseTo_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryRaiseBy_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryTimeStamp_hl\": \"25-04-2022\",\n"
  //      + "    \"queryApplicantName_hl\": \"Mayank\",\n"
  //      + "    \"queryRemarks_hl\": \"no available\",\n"
  //      + "    \"queryRegarding_hl\": \"Entries Mis-Match\",\n"
  //      + "    \"queryResolutionRemarks_hl\": \"Rrm\",\n"
  //      + "    \"queryResolutionDoc_hl\": \"docs1\",\n"
  //      + "    \"queryResolutionStatus_hl\": \"reslved\",\n"
  //      + "    \"queryId_hl\": \"1\",\n"
  //      + "    \"canDelete\": \"yes\"\n"
  //      + "  },\n"
  //      + "  {\n"
  //      + "    \"queryRaiseTo_hl\": \"Pragati\",\n"
  //      + "    \"queryRaiseBy_hl\": \"dataentry1@kuliza.com\",\n"
  //      + "    \"queryTimeStamp_hl\": \"25-04-2022\",\n"
  //      + "    \"queryApplicantName_hl\": \"Ranjani\",\n"
  //      + "    \"queryRemarks_hl\": \"no available\",\n"
  //      + "    \"queryRegarding_hl\": \"Irrelavant Document\",\n"
  //      + "    \"queryResolutionRemarks_hl\": \"resolution rm\",\n"
  //      + "    \"queryResolutionDoc_hl\": \"docs2\",\n"
  //      + "    \"queryResolutionStatus_hl\": \"slsl\",\n"
  //      + "    \"queryId_hl\": \"2\",\n"
  //      + "    \"canDelete\": \"yes\"\n"
  //      + "  }\n"
  //      + "]");
  //
  //
  //    for (int i = 0; i < queriesTableC.length(); i++) {
  //      for (int j = i; j < queriesTableP.length(); j++) {
  //        String queryIdC = (String) queriesTableC.getJSONObject(i).get("queryId_hl");
  //        String queryIdP = (String) queriesTableP.getJSONObject(j).get("queryId_hl");
  //        if (queryIdC.equals(queryIdP)) {
  //          JSONObject queryRowP = queriesTableP.getJSONObject(j);
  //          queriesTableC.getJSONObject(i).put("queryResolutionRemarks_hl",
  // queryRowP.get("queryResolutionRemarks_hl"));
  //          queriesTableC.getJSONObject(i).put("queryResolutionDoc_hl",
  // queryRowP.get("queryResolutionDoc_hl"));
  //          queriesTableC.getJSONObject(i).put("queryResolutionStatus_hl",
  // queryRowP.get("queryResolutionStatus_hl"));
  //          queriesTableC.getJSONObject(i).put("canDelete", queryRowP.get("canDelete"));
  //        } else {
  //          queriesTableC.getJSONObject(i).put("queryResolutionRemarks_hl","");
  //          queriesTableC.getJSONObject(i).put("queryResolutionDoc_hl", "");
  //          queriesTableC.getJSONObject(i).put("queryResolutionStatus_hl", "");
  //          queriesTableC.getJSONObject(i).put("canDelete", "");
  //        }
  //      }
  //    }
  //
  //    System.out.println("queriesTable C" + queriesTableC);
  //
  //  }
}
