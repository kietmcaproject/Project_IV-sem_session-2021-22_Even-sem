package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.ArrayList;
import java.util.List;
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

@Service("BranchManagerTableService")
public class BranchManagerTableService {
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  private static final Logger logger = LoggerFactory.getLogger(ENachService.class);

  public DelegatePlanItemInstance documentsTable(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    Map<String, Object> variables = planItemInstance.getVariables();
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    String propertyEligiblePmayClss =
        CommonHelperFunctions.getStringValue(variables.get("propertyEligiblePMAYCLSS_hl"));
    String endUseOfLoan = CommonHelperFunctions.getStringValue(variables.get("endUseOfLoan_hl"));
    postSanctionDocumentsTable(variables, caseInstanceId, propertyEligiblePmayClss, endUseOfLoan);
    sanctionConditionDocumentsTable(
        variables, caseInstanceId, propertyEligiblePmayClss, endUseOfLoan);

    //    String bmSubmitDocs =
    // CommonHelperFunctions.getStringValue(variables.get("bmSubmitDocs_hl"));
    //    if (bmSubmitDocs.equalsIgnoreCase("yes")) {
    //      finalSignedDocumentsTable(variables, caseInstanceId);
    //    } else {
    //      physicallySignedDocumentsTable(
    //          variables, caseInstanceId, propertyEligiblePmayClss, endUseOfLoan);

    return planItemInstance;
  }

  private void sanctionConditionDocumentsTable(
      Map<String, Object> variables,
      String caseInstanceId,
      String propertyEligiblePmayClss,
      String endUseOfLoan)
      throws JSONException {
    JSONArray sanctionConditionDocuments = new JSONArray();
    List<String> documents = new ArrayList<>();
    documents.add("Loan Application Form");
    documents.add("Sanction Letter including schedule of charges, MITC");
    documents.add("Vernacular Sanction Letter");
    documents.add("Loan Agreement");
    documents.add("Disbursement Request Form");
    documents.add("Facility Agreement");
    documents.add("End Use Letter");
    documents.add("Insurance Form");
    documents.add("NACH form");
    documents.add("Demand Promissory Note");
    documents.add("Letter of continuity");
    documents.add("Technical Undertaking");
    documents.add("Legal Undertaking");
    documents.add("Vernacular Declaration");
    if (propertyEligiblePmayClss.equalsIgnoreCase(
        "Yes")) { // TODO: Anupam has to provide variable name
      documents.add("PMAY Undertaking");
    }
    if (endUseOfLoan.equalsIgnoreCase("self-construction")
        || endUseOfLoan.equalsIgnoreCase(
            "Plot Purchase + Construction")) { // TODO: Anupam has to provide variable name

      for (int i = 0; i < documents.size(); i++) {
        JSONObject tableData = new JSONObject();
        tableData.put("documentNameSanctionConditions_hl", documents.get(i));
        sanctionConditionDocuments.put(tableData);
      }
      cmmnRuntimeService.setVariable(
          caseInstanceId, "sanctionConditionsDocumentsTable_hl", sanctionConditionDocuments);
    }
  }

  private JSONArray postSanctionDocumentsTable(
      Map<String, Object> variables,
      String caseInstanceId,
      String propertyEligiblePmayClss,
      String endUseOfLoan)
      throws JSONException {
    JSONArray postSanctionDocuments = new JSONArray();
    List<String> documents = new ArrayList<>();
    documents.add("Loan Application Form");
    documents.add("Sanction Letter including schedule of charges, MITC");
    documents.add("Vernacular Sanction Letter");
    documents.add("Loan Agreement");
    documents.add("Disbursement Request Form");
    documents.add("Facility Agreement");
    documents.add("End Use Letter");
    documents.add("Insurance Form");
    documents.add("NACH form");
    documents.add("Demand Promissory Note");
    documents.add("Letter of continuity");
    documents.add("Technical Undertaking");
    documents.add("Legal Undertaking");
    documents.add("Vernacular Declaration");
    if (propertyEligiblePmayClss.equalsIgnoreCase(
        "Yes")) { // TODO: Anupam has to provide variable name
      documents.add("PMAY Undertaking");
    }
    if (endUseOfLoan.equalsIgnoreCase("self-construction")
        || endUseOfLoan.equalsIgnoreCase(
            "Plot Purchase + Construction")) { // TODO: Anupam has to provide variable name
      documents.add("Affidavit cum undertaking");

      cmmnRuntimeService.setVariable(
          caseInstanceId, "sanctionConditionsDocumentsTable_hl", postSanctionDocuments);
    }

    for (int i = 0; i < documents.size(); i++) {
      JSONObject tableData = new JSONObject();
      tableData.put("documentNamePostSanction_hl", documents.get(i));
      tableData.put("documentUploadPostSanction_hl", "");
      postSanctionDocuments.put(tableData);
    }
    cmmnRuntimeService.setVariable(
        caseInstanceId, "postSanctionDocumentsTable_hl", postSanctionDocuments);

    return postSanctionDocuments;
  }
}

//  private JSONArray physicallySignedDocumentsTable(
//      Map<String, Object> variables,
//      String caseInstanceId,
//      String propertyEligiblePmayClss,
//      String endUseOfLoan)
//      throws JSONException {
//    JSONArray physicallySignedDocuments = new JSONArray();
//    List<String> documents = new ArrayList<>();
//    documents.add("NACH form");
//    documents.add("Demand Promissory Note");
//    documents.add("Letter of continuity");
//    documents.add("Technical Undertaking");
//    documents.add("Legal Undertaking");
//    if (propertyEligiblePmayClss.equalsIgnoreCase(
//        "Yes")) { // TODO: Anupam has to provide variable name
//      documents.add("PMAY Undertaking");
//    }
//    if (endUseOfLoan.equalsIgnoreCase("self-construction")
//        || endUseOfLoan.equalsIgnoreCase(
//            "Plot Purchase + Construction")) { // TODO: Anupam has to provide variable name
//      documents.add("Affidavit cum undertaking");
//    }
//    documents.add("PDC cheques");
//
//    for (int i = 0; i < documents.size(); i++) {
//      JSONObject tableData = new JSONObject();
//      tableData.put("documentNamePhysicallySigned_hl", documents.get(i));
//      physicallySignedDocuments.put(tableData);
//    }
//    cmmnRuntimeService.setVariable(
//        caseInstanceId, "physicallySignedDocumentsTable_hl", physicallySignedDocuments);
//    return physicallySignedDocuments;
//  }

//  public void finalSignedDocumentsTable(Map<String, Object> variables, String caseInstanceId)
//      throws JSONException {
//    JSONArray finalSignedDocuments = new JSONArray();
//    List<String> documents = new ArrayList<>();
//    JSONArray physicallySignedDocumentsArray =
//        (JSONArray) variables.get("physicallySignedDocumentsTable_hl");
//    JSONArray postSanctionDocumentsArray =
//        (JSONArray) variables.get("postSanctionDocumentsTable_hl");
//    for (int i = 0; i < physicallySignedDocumentsArray.length(); i++) {
//      JSONObject physicallySignedDocumentsObject =
//          (JSONObject) physicallySignedDocumentsArray.get(i);
//      String documentName =
//          CommonHelperFunctions.getStringValue(
//              physicallySignedDocumentsObject.get("documentNamePhysicallySigned_hl"));
//      documents.add(documentName);
//    }
//    for (int i = 0; i < postSanctionDocumentsArray.length(); i++) {
//      JSONObject postSanctionDocumentsObject = (JSONObject) postSanctionDocumentsArray.get(i);
//      String documentName =
//          CommonHelperFunctions.getStringValue(
//              postSanctionDocumentsObject.get("documentNamePhysicallySigned_hl"));
//      if (!documents.contains(documentName)) {
//        documents.add(documentName);
//      }
//    }
//
//    for (int i = 0; i < documents.size(); i++) {
//      JSONObject tableData = new JSONObject();
//      tableData.put("documentNameFinal_hl", documents.get(i));
//      tableData.put("View/Download", "");
//      for (int j = 0; j < physicallySignedDocumentsArray.length(); j++) {
//        JSONObject physicallySignedDocumentsObject =
//            (JSONObject) physicallySignedDocumentsArray.get(i);
//        String documentName =
//            CommonHelperFunctions.getStringValue(
//                physicallySignedDocumentsObject.get("documentNamePhysicallySigned_hl"));
//        if (documents.get(i).equalsIgnoreCase(documentName)) {
//          tableData.put(
//              "docReceivedBmFinal_hl",
//              CommonHelperFunctions.getStringValue(
//                  physicallySignedDocumentsObject.get("docReceivedBmFinal_hl")));
//        }
//      }
//      finalSignedDocuments.put(tableData);
//    }
//    cmmnRuntimeService.setVariable(caseInstanceId, "finalSignedTable_hl", finalSignedDocuments);
//  }
