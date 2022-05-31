package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.MasterHelper;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.common.engine.impl.util.Financials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusChangeService {
  private static final Logger logger = LoggerFactory.getLogger(StatusChangeService.class);
  private static final DecimalFormat df = new DecimalFormat("0.00");
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired MasterHelper masterHelper;

  public void changeStatus(Map<String, String> currentRoleRequest) throws JSONException {
    String caseInstanceId = currentRoleRequest.get("caseInstanceId").toString();
    Map<String, Object> allVariables = cmmnRuntimeService.getVariables(caseInstanceId);
    JSONObject object = new JSONObject(new Gson().toJson(allVariables));
    String currentRole = currentRoleRequest.get("currentRole").toString();
    cmmnRuntimeService.setVariable(caseInstanceId, currentRole + "Status", "history");
    logger.info("Current role :- {}", currentRole);
    String currentRoleAssignee = object.getString(currentRole + "Assignee");
    logger.info("Current role Assignee :- {}", currentRoleAssignee);
    JSONObject hrmisMasterObject = masterHelper.getRequestFromMaster("capri-hrmis");
    JSONArray HRMIS = hrmisMasterObject.getJSONArray("data");
    logger.info("HRMIS data :- {}", HRMIS);
    String currentBranch = "";
    for (int i = 0; i < HRMIS.length(); i++) {
      String role = HRMIS.getJSONObject(i).get("Role").toString();
      if (role.equalsIgnoreCase(currentRole)) {
        currentBranch = HRMIS.getJSONObject(i).get("Branch").toString();
        logger.info("Current Branch :- {}", currentBranch);
      }
    }
    HashMap<String, Integer> rol = new HashMap<>();
    List<String> allRoles = new ArrayList<>();
    allRoles.add(0, "BranchManager");
    allRoles.add(1, "ASM");
    allRoles.add(2, "CSM");
    allRoles.add(3, "RSM");
    allRoles.add(4, "ZSM");
    allRoles.add(5, "NationalSalesManager");
    allRoles.add(6, "BranchHead");

    for (int i = 0; i < HRMIS.length(); i++) {
      String branch = HRMIS.getJSONObject(i).get("Branch").toString();
      if (branch.equalsIgnoreCase(currentBranch)) {
        String role = HRMIS.getJSONObject(i).get("Role").toString();

        if (allRoles.contains(role)) {
          if (role.equalsIgnoreCase("BranchManager")) {
            rol.put("BranchManager", 0);
          }
          if (role.equalsIgnoreCase("ASM")) {
            rol.put("ASM", 1);
          }
          if (role.equalsIgnoreCase("CSM")) {
            rol.put("CSM", 2);
          }
          if (role.equalsIgnoreCase("RSM")) {
            rol.put("RSM", 3);
          }
          if (role.equalsIgnoreCase("ZSM")) {
            rol.put("ZSM", 4);
          }
          if (role.equalsIgnoreCase("NationalSalesManager")) {
            rol.put("NationalSalesManager", 5);
          }
          if (role.equalsIgnoreCase("BranchHead")) {
            rol.put("BranchHead", 6);
          }
        }
      }
    }
    logger.info("Roles List related to current Role Branch :- {}", rol);

    //    int index = roles.indexOf(currentRole);
    //    String nextRole = roles.get(index + 1);

    int value = rol.get(currentRole);
    if (value < 6) {
      String nextRole = allRoles.get(value + 1);
      if (nextRole.equalsIgnoreCase("NationalSalesManager")) {
        String roiFinal = object.get("roiFD_hl").toString();
        String updatedROI = object.get("updatedROI").toString();
        double difference =
            Double.parseDouble(
                (df.format(Double.parseDouble(roiFinal) - Double.parseDouble(updatedROI))));
        Double bps = difference * 100;
        cmmnRuntimeService.setVariable(caseInstanceId, "bps", bps);
        if (difference <= 0.50) {
          for (int i = 0; i < HRMIS.length(); i++) {
            String role = HRMIS.getJSONObject(i).get("Role").toString();
            if (role.equalsIgnoreCase(nextRole)) {
              String nextRoleAssignee = HRMIS.getJSONObject(i).get("Email ID").toString();
              logger.info("next role assignee :- {}", nextRoleAssignee);
              cmmnRuntimeService.setVariable(
                  caseInstanceId, nextRole + "Assignee", nextRoleAssignee);
              if (currentRoleRequest.get("status").toString().equalsIgnoreCase("worklist")) {
                cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "worklist");
              } else {
                cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "sanction");
              }
              cmmnRuntimeService.setVariable(caseInstanceId, "lastRoleMaxCap", "yes");
            }
          }
        }

        if (difference > 0.50 && difference <= 1) {
          for (int i = 0; i < HRMIS.length(); i++) {
            String role = HRMIS.getJSONObject(i).get("Role").toString();
            if (role.equalsIgnoreCase(nextRole)) {
              String nextRoleAssignee = HRMIS.getJSONObject(i).get("Email ID").toString();
              logger.info("next role assignee :- {}", nextRoleAssignee);
              cmmnRuntimeService.setVariable(
                  caseInstanceId, nextRole + "Assignee", nextRoleAssignee);
              if (currentRoleRequest.get("status").toString().equalsIgnoreCase("worklist")) {
                cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "worklist");
              } else {
                cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "sanction");
              }
              if (nextRole.equalsIgnoreCase("BranchHead")) {
                cmmnRuntimeService.setVariable(caseInstanceId, "lastRoleMaxCap", "yes");
              }
            }
          }
        }
      } else {
        for (int i = 0; i < HRMIS.length(); i++) {
          String role = HRMIS.getJSONObject(i).get("Role").toString();
          if (role.equalsIgnoreCase(nextRole)) {
            String nextRoleAssignee = HRMIS.getJSONObject(i).get("Email ID").toString();
            logger.info("next role assignee :- {}", nextRoleAssignee);
            cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Assignee", nextRoleAssignee);
            if (currentRoleRequest.get("status").toString().equalsIgnoreCase("worklist")) {
              cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "worklist");
            } else {
              cmmnRuntimeService.setVariable(caseInstanceId, nextRole + "Status", "sanction");
            }
          }
        }
      }
    } else {
      cmmnRuntimeService.setVariable(caseInstanceId, "lastRoleMaxCap", "yes");
    }

    String lastRoleMaxCap =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "lastRoleMaxCap"));
    logger.info("last Role Max Cap :- {}", lastRoleMaxCap);
    if (lastRoleMaxCap.equalsIgnoreCase("yes")) {
      String rate =
          CommonHelperFunctions.getStringValue(
              cmmnRuntimeService.getVariable(caseInstanceId, "finalROIMaximumCapping"));
      String finalLoanAmount =
          CommonHelperFunctions.getStringValue(
              cmmnRuntimeService.getVariable(caseInstanceId, "finalLoanAmount"));
      String loanTenure =
          CommonHelperFunctions.getStringValue(
              cmmnRuntimeService.getVariable(caseInstanceId, "loanTenure_hl"));
      logger.info("final ROI Maximum Capping :- {}", rate);
      logger.info("finalLoanAmount :- {}", finalLoanAmount);
      logger.info("loanTenure :- {}", loanTenure);
      double rateD = Double.parseDouble(rate);
      rateD = rateD / 100;
      logger.info("final ROI Maximum Capping D :- {}", rateD);
      double finalLoanAmountD = Double.parseDouble(finalLoanAmount);
      finalLoanAmountD = -(finalLoanAmountD);
      logger.info("finalLoanAmountD :- {}", finalLoanAmountD);
      int loanTenureD = Integer.parseInt(loanTenure);
      logger.info("loanTenureD :- {}", loanTenureD);
      double pmt = Financials.pmt(rateD, loanTenureD, finalLoanAmountD, 0, 1);
      logger.info("PMT VALUE :- {}", pmt);
      cmmnRuntimeService.setVariable(caseInstanceId, "emi_hl", df.format(pmt));
    }
  }

  public static void main(String[] args) {
    double r = 0.134;
    double pv = -1200000.0;
    int nper = 60;
    //        double pmt = -r * (pv * Math.pow(1 + r, nper)) / ((1 + r) * (Math.pow(1 + r, nper) -
    // 1));

    double pmt = Financials.pmt(r, nper, pv, 0, 1);
    //        double fv=pv * Math.pow((1 + r/12), nper);
    //
    //        double pmt = (fv * r/12) / (Math.pow((1 + r/12), nper) - 1);

    System.out.println(pmt);
  }
  //    List<String> allRoles = new ArrayList<>();
  //    List<String> roles = new ArrayList<>();
  //    String currRole = "BranchManager";
  //    allRoles.add(0, "BranchManager");
  //    allRoles.add(1, "ASM");
  //    allRoles.add(2, "CSM");
  //    allRoles.add(3, "RSM");
  //    allRoles.add(4, "ZSM");
  //    allRoles.add(5, "NSM");
  //
  //    String role = "ASM";
  //
  //    HashMap<String, Integer> rol = new HashMap<>();
  //
  //    if (allRoles.contains(role)) {
  //      if (role.equalsIgnoreCase("BranchManager")) {
  //
  ////        roles.add(0, role);
  //        rol.put("BranchManager", 0);
  //      }
  //      if (role.equalsIgnoreCase("ASM")) {
  ////        roles.add(1, role);
  //        rol.put("ASM", 1);
  //      }
  //      if (role.equalsIgnoreCase("CSM")) {
  ////        roles.add(2, role);
  //        rol.put("CSM", 2);
  //      }
  //      if (role.equalsIgnoreCase("RSM")) {
  ////        roles.add(3, role);
  //        rol.put("RSM", 3);
  //      }
  //      if (role.equalsIgnoreCase("ZSM")) {
  ////        roles.add(4, role);
  //        rol.put("ZSM", 4);
  //      }
  //      if (role.equalsIgnoreCase("NSM")) {
  ////        roles.add(5, role);
  //        rol.put("NSM", 5);
  //      }
  //    }
  //
  //    System.out.println(roles);
  //    System.out.println(rol);
  //
  //
  //
  //  }
}
