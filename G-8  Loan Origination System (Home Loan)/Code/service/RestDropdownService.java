package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.MasterHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestDropdownService {
  private static final Logger logger = LoggerFactory.getLogger(RestDropdownService.class);
  @Autowired RuntimeService runtimeService;
  @Autowired MasterHelper masterHelper;
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public String restDropdown(String processInstanceId) throws JSONException {
    Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
    JSONArray data = new JSONArray();
    JSONObject object = new JSONObject(new Gson().toJson(variables));
    logger.info("variables :- {}", object);
    try {
      JSONArray jsonArray = object.getJSONArray("applicants");
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject applicant = (JSONObject) jsonArray.get(i);
        String applicant1 = applicant.getString("applicant_name");
        JSONObject object1 = new JSONObject();
        object1.put("ApplicationName", applicant1);
        data.put(object1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    JSONObject finalResponse = new JSONObject();
    finalResponse.put("data", data);

    return finalResponse.toString();
  }

  public String filterRolesByBranch(String branch) throws JSONException {
    JSONObject hrmisMasterObject = masterHelper.getRequestFromMaster("capri-hrmis");
    logger.info("HRMIS master response :- {}", hrmisMasterObject);

    JSONArray HRMIS = hrmisMasterObject.getJSONArray("data");
    logger.info("HRMIS data :- {}", HRMIS);

    JSONObject vendorMasterObject = masterHelper.getRequestFromMaster("capri-vendor-master");
    logger.info("VENDOR master response :- {}", vendorMasterObject);
    JSONArray VENDOR = vendorMasterObject.getJSONArray("data");
    logger.info("data :- {}", VENDOR);

    JSONArray response = new JSONArray();
    for (int i = 0; i < HRMIS.length(); i++) {
      String role = HRMIS.getJSONObject(i).get("Role").toString();
      String hrmisBranch = HRMIS.getJSONObject(i).get("Branch").toString();
      if (role.equalsIgnoreCase("BTM") && hrmisBranch.equalsIgnoreCase(branch)) {
        JSONObject object = new JSONObject();
        object.put("email", HRMIS.getJSONObject(i).get("Email ID").toString());
        object.put("intExtTechFlag", "I");
        response.put(object);
      }
    }

    for (int i = 0; i < VENDOR.length(); i++) {
      String role = VENDOR.getJSONObject(i).get("Type").toString();
      String vendorBranch = VENDOR.getJSONObject(i).get("Location").toString();
      if (role.equalsIgnoreCase("Technical") && vendorBranch.equalsIgnoreCase(branch)) {
        JSONObject object = new JSONObject();
        object.put("email", VENDOR.getJSONObject(i).get("E-mail ID").toString());
        object.put("intExtTechFlag", "E");
        response.put(object);
      }
    }
    logger.info("---------- complete List of BTM and ETV : {}", response.toString());

    return response.toString();
  }

  public String multiTranchRestDropdown(String caseInstanceId) throws JSONException {
    logger.info("Inside multi tranch api............................");
    //    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    //    JSONObject variablesObject = new JSONObject(new Gson().toJson(variables));
    //    logger.info("variables :- {}", variablesObject);

    String recommendedStageOfConstruction =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "recommendedStageOfConstruction_hl"));

    logger.info(
        "recommendedStageOfConstruction............... :- {}", recommendedStageOfConstruction);

    String endUseOfLoan =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "endUseOfLoanString_hl"));
    logger.info("endUseOfLoanString_hl :- {}", endUseOfLoan);
    JSONObject finalResponse = new JSONObject();

    JSONArray data = new JSONArray();

    List<String> selfConstructionList = new ArrayList<>();
    List<Integer> selfConstructionListPercentage = new ArrayList<>();

    selfConstructionList.add(0, "At Plinth Completion");
    selfConstructionList.add(1, "RCC Complete");
    selfConstructionList.add(2, "Brick Work Complete");
    selfConstructionList.add(3, "Plaster Internal & External");
    selfConstructionList.add(4, "Flooring & Electrical and Plumbing");
    selfConstructionList.add(5, "Finishing Items");

    selfConstructionListPercentage.add(0, 20);
    selfConstructionListPercentage.add(1, 50);
    selfConstructionListPercentage.add(2, 65);
    selfConstructionListPercentage.add(3, 80);
    selfConstructionListPercentage.add(4, 95);
    selfConstructionListPercentage.add(5, 100);

    logger.info("selfConstructionList.... :- {}", selfConstructionList);
    logger.info("selfConstructionListPercentage.... :- {}", selfConstructionListPercentage);

    if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")
        && recommendedStageOfConstruction.equalsIgnoreCase("")) {
      logger.info("Inside 1st if...........................");

      JSONObject selfConstructionObject = new JSONObject();
      selfConstructionObject.put("Options", "Plot purchase & Construction");
      selfConstructionObject.put("Percentage", 60);
      data.put(selfConstructionObject);
      finalResponse.put("data", data);
    }
    if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")
        && !recommendedStageOfConstruction.equalsIgnoreCase("")) {
      logger.info("Inside 2nd if...........................");

      String currentTranchePercent =
          CommonHelperFunctions.getStringValue(
              cmmnRuntimeService.getVariable(caseInstanceId, "currentTranchePercent_hl"));
      int index = selfConstructionListPercentage.indexOf(Integer.parseInt(currentTranchePercent));
      for (int i = index + 1; i < selfConstructionList.size(); i++) {
        JSONObject selfConstructionObject = new JSONObject();
        selfConstructionObject.put("Options", selfConstructionList.get(i));
        selfConstructionObject.put("Percentage", selfConstructionListPercentage.get(i));
        data.put(selfConstructionObject);
      }
      logger.info("Self Construction Array.... :- {}", data);
      finalResponse.put("data", data);
    }

    if (!endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")
        && recommendedStageOfConstruction.equalsIgnoreCase("")) {
      logger.info("Inside multi tranch api nul value............................");

      for (int i = 0; i < selfConstructionList.size(); i++) {
        JSONObject selfConstructionObject = new JSONObject();
        selfConstructionObject.put("Options", selfConstructionList.get(i));
        selfConstructionObject.put("Percentage", selfConstructionListPercentage.get(i));
        data.put(selfConstructionObject);
      }
      logger.info("Self Construction Array.... :- {}", data);
      finalResponse.put("data", data);
    }
    if (!endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")
        && !recommendedStageOfConstruction.equalsIgnoreCase("")) {
      logger.info("Inside multi tranch api not null value............................");

      int index = selfConstructionList.indexOf(recommendedStageOfConstruction);
      for (int i = index + 1; i < selfConstructionList.size(); i++) {

        JSONObject selfConstructionObject = new JSONObject();
        selfConstructionObject.put("Options", selfConstructionList.get(i));
        selfConstructionObject.put("Percentage", selfConstructionListPercentage.get(i));
        data.put(selfConstructionObject);
      }
      logger.info("Self Construction Array.... :- {}", data);
      finalResponse.put("data", data);
    }
    return finalResponse.toString();
  }

  //    if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
  //      JSONArray data = new JSONArray();

  //      List<String> plotPurchaseList = new ArrayList<>();
  //      List<String> plotPurchaseListPercentage = new ArrayList<>();
  //
  //      plotPurchaseList.add(0, "Plaster Internal & External");
  //      plotPurchaseList.add(1, "Flooring & Electrical and Plumbing");
  //      plotPurchaseList.add(2, "Finishing Items");
  //
  //      plotPurchaseListPercentage.add(0, "80");
  //      plotPurchaseListPercentage.add(1, "95");
  //      plotPurchaseListPercentage.add(2, "100");

  //      logger.info("plotPurchaseList.... :- {}", plotPurchaseList);
  //      logger.info("plotPurchaseList Percentage.... :- {}", plotPurchaseListPercentage);

  //      for (int i = 0; i < plotPurchaseList.size(); i++) {
  //        JSONObject plotPurchaseObject = new JSONObject();
  //        String
  //        String plotPurchaseListPercent = plotPurchaseListPercentage.get(i);
  //        plotPurchaseObject.put("Options", plotPurchaseList.get(i));
  //        plotPurchaseObject.put("Percentage", plotPurchaseListPercent);
  //        data.put(plotPurchaseObject);
  //        //        JSONObject object = new JSONObject();
  //        //        String listValues = plotPurchaseList.get(i);
  //        //        object.put("Options", listValues);
  //      }
  //      logger.info("Plot Purchase Array.... :- {}", data);
  //      finalResponse.put("data", data);
  //    }

}
