package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.lending.portal.misc.AllocationServiceTask;
import com.kuliza.workbench.model.BcmAllocationDetails;
import com.kuliza.workbench.repository.BcmAllocationDetailsRepository;
import com.kuliza.workbench.util.MasterHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("AllocationLogicService")
public class AllocationLogicService {
  private static final Logger logger = LoggerFactory.getLogger(AllocationLogicService.class);

  public static final int TWENTY_FIVE_LAKHS = 2500000;

  @Value("${hrmis.master.url}")
  private String hrmisMasterUrl;

  @Value("${vendor.master.url}")
  private String vendorMasterUrl;

  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired RuntimeService runtimeService;
  @Autowired AllocationServiceTask allocationServiceTask;

  @Autowired MasterHelper masterHelper;
  @Autowired BcmAllocationDetailsRepository bcmAllocationDetailsRepository;

  public DelegatePlanItemInstance allocationLogicForBCM(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    Map<String, Object> variables = planItemInstance.getVariables();
    String bcmEmail = "";
    List<String> bcmRoleList = new ArrayList<>();
    String branchName =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(planItemInstance.getCaseInstanceId(), "loanBranch_hl"));

    logger.info("------------ variables : {}", variables);
    JSONArray table;

    try {
      JSONObject object = new JSONObject(new Gson().toJson(variables));
      String tableString = object.get("refertoCredit_hl").toString();
      table = new JSONArray(tableString);
      logger.info("------------ table : {} ", table);
      for (int i = 0; i < table.length(); i++) {
        JSONObject row = table.getJSONObject(i);
        String R2CCode = row.getString("r2cIdentificationCode_hl");
        logger.info("------------ R2CCode : {} ", R2CCode);

        if (R2CCode.equalsIgnoreCase("R2C16")
            || R2CCode.equalsIgnoreCase("R2C17")
            || R2CCode.equalsIgnoreCase("R2C18")
            || R2CCode.equalsIgnoreCase("R2C19")) {
          int userType = Integer.parseInt(row.get("r2cUserType_hl").toString());
          logger.info("------------ userType : {} ", userType);

          String customerScheme =
              CommonHelperFunctions.getStringValue(
                  variables.get("customerSchemeApp" + userType + "_hl"));
          logger.info("------------ customerScheme : {} ", customerScheme);

          if (customerScheme.equalsIgnoreCase("CAT B")
              || customerScheme.equalsIgnoreCase("CAT C")) {
            logger.info(
                "--------------- inside customerScheme if block  --------------------------");
            cmmnRuntimeService.setVariable(
                planItemInstance.getCaseInstanceId(),
                "isR2CValidcoapp" + (userType - 1) + "_hl",
                "yes");

            JSONObject hrmisMasterObject = masterHelper.getRequestFromMaster("capri-hrmis");
            logger.info("HRMIS master response :- {}", hrmisMasterObject);

            JSONArray HRMIS = hrmisMasterObject.getJSONArray("data");
            logger.info("HRMIS data :- {}", HRMIS);
            for (int j = 0; j < HRMIS.length(); j++) {
              String role = HRMIS.getJSONObject(j).get("Role").toString();
              String branch = HRMIS.getJSONObject(j).get("Branch").toString();
              if (role.equalsIgnoreCase("BCM") && branch.equalsIgnoreCase(branchName)) {
                bcmRoleList.add(HRMIS.getJSONObject(j).get("Email ID").toString());
              }
            }
            logger.info(" -------- bcmRoleList : {}", bcmRoleList);
            if (!bcmRoleList.isEmpty()) {
              BcmAllocationDetails branchNameExists = null;
              try {
                branchNameExists = bcmAllocationDetailsRepository.findByBranchName(branchName);
              } catch (Exception e) {
              }
              BcmAllocationDetails bcmAllocationDetails = new BcmAllocationDetails();
              if (branchNameExists == null) {
                bcmAllocationDetails.setBranchName(branchName);
                bcmAllocationDetails.setCount(1);
                bcmAllocationDetailsRepository.save(bcmAllocationDetails);
                String bcmAssignee = bcmRoleList.get(0);
                logger.info("----------- BCM Assignee : {}", bcmAssignee);
                cmmnRuntimeService.setVariable(
                    planItemInstance.getCaseInstanceId(), "BCMAssignee", bcmAssignee);
                cmmnRuntimeService.setVariable(
                    planItemInstance.getCaseInstanceId(), "BCMStatus", "worklist");
              } else {
                int count = branchNameExists.getCount();
                String bcmAssignee = bcmRoleList.get(count);
                logger.info("----------- BCM Assignee : {}", bcmAssignee);
                cmmnRuntimeService.setVariable(
                    planItemInstance.getCaseInstanceId(), "BCMAssignee", bcmAssignee);
                cmmnRuntimeService.setVariable(
                    planItemInstance.getCaseInstanceId(), "BCMStatus", "worklist");
                if (count == bcmRoleList.size() - 1) {
                  bcmAllocationDetailsRepository.updateCount(branchName, 0);
                } else {
                  bcmAllocationDetailsRepository.updateCount(branchName, count + 1);
                }
              }
            }
          } else {
            logger.info(
                "--------------- inside customerScheme else block  --------------------------");
            cmmnRuntimeService.setVariable(
                planItemInstance.getCaseInstanceId(),
                "isR2CValidcoapp" + (userType - 1) + "_hl",
                "no");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return planItemInstance;
  }

  public void allocationLogicForBTMandExternalTechVendor(DelegatePlanItemInstance planItemInstance)
      throws Exception {

    String caseInstanceId = planItemInstance.getCaseInstanceId();
    JSONObject hrmisMasterObject = masterHelper.getRequestFromMaster("capri-hrmis");
    logger.info("HRMIS master response :- {}", hrmisMasterObject);

    JSONArray HRMIS = hrmisMasterObject.getJSONArray("data");
    logger.info("HRMIS data :- {}", HRMIS);

    JSONObject vendorMasterObject = masterHelper.getRequestFromMaster("capri-vendor-master");
    logger.info("VENDOR master response :- {}", vendorMasterObject);
    JSONArray VENDOR = vendorMasterObject.getJSONArray("data");
    logger.info("data :- {}", VENDOR);

    Double aDouble = Double.parseDouble(planItemInstance.getVariable("loanAmount_hl").toString());
    int loanAmount = aDouble.intValue();
    logger.info(" -------------- loanAmount : {}", loanAmount);

    String btmEmail = "";
    String etv1Email = "";
    String etv2Email = "";
    List<String> btmRoleList = new ArrayList<>();
    List<String> extTechVendorRoleList = new ArrayList<>();
    if (loanAmount <= TWENTY_FIVE_LAKHS) {
      for (int i = 0; i < HRMIS.length(); i++) {
        String role = HRMIS.getJSONObject(i).get("Role").toString();
        String branch = HRMIS.getJSONObject(i).get("Branch").toString();
        if (role.equalsIgnoreCase("BTM")
            && branch.equalsIgnoreCase(planItemInstance.getVariable("loanBranch_hl").toString())) {
          btmRoleList.add(HRMIS.getJSONObject(i).get("Email ID").toString());
        }
      }
      logger.info(" -------- btmRoleList : {}", btmRoleList);
      if (!btmRoleList.isEmpty()) {
        String branchName =
            CommonHelperFunctions.getStringValue(planItemInstance.getVariable("branchName_hl"));
        btmEmail =
            allocationServiceTask.allocationLogic(
                planItemInstance.getCaseInstanceId(),
                "BACK OFFICE",
                "",
                "BTM_" + branchName,
                "BTM");
        cmmnRuntimeService.setVariable(caseInstanceId, "quadrant", "first");
        logger.info("----------- btmEmail : {}", btmEmail);
      } else {
        for (int i = 0; i < VENDOR.length(); i++) {
          String role = VENDOR.getJSONObject(i).get("Type").toString();
          String branch = VENDOR.getJSONObject(i).get("Location").toString();
          if (role.equalsIgnoreCase("Technical")
              && branch.equalsIgnoreCase(
                  planItemInstance.getVariable("loanBranch_hl").toString())) {
            extTechVendorRoleList.add(VENDOR.getJSONObject(i).get("E-mail ID").toString());
          }
        }
        logger.info("------------ extTechVendorRoleList : {}", extTechVendorRoleList);
        if (!extTechVendorRoleList.isEmpty()) {
          String branchName =
              CommonHelperFunctions.getStringValue(planItemInstance.getVariable("branchName_hl"));
          etv1Email =
              allocationServiceTask.allocationLogic(
                  planItemInstance.getCaseInstanceId(),
                  "BACK OFFICE",
                  "",
                  "ExternalTechnicalVendor_" + branchName,
                  "ExternalTechnicalVendor");
          cmmnRuntimeService.setVariable(caseInstanceId, "quadrant", "second");
          logger.info("----------- etv1Email : {}", etv1Email);
        }
      }
    } else {
      for (int i = 0; i < HRMIS.length(); i++) {
        String role = HRMIS.getJSONObject(i).get("Role").toString();
        String branch = HRMIS.getJSONObject(i).get("Branch").toString();
        if (role.equalsIgnoreCase("BTM")
            && branch.equalsIgnoreCase(planItemInstance.getVariable("loanBranch_hl").toString())) {
          btmRoleList.add(HRMIS.getJSONObject(i).get("Email ID").toString());
        }
      }
      logger.info("--------- btmRoleList : {}", btmRoleList);
      String branchName =
          CommonHelperFunctions.getStringValue(planItemInstance.getVariable("branchName_hl"));
      if (!btmRoleList.isEmpty()) {
        btmEmail =
            allocationServiceTask.allocationLogic(
                planItemInstance.getCaseInstanceId(),
                "BACK OFFICE",
                "",
                "BTM_" + branchName,
                "BTM");
        etv1Email =
            allocationServiceTask.allocationLogic(
                planItemInstance.getCaseInstanceId(),
                "BACK OFFICE",
                "",
                "ExternalTechnicalVendor_" + branchName,
                "ExternalTechnicalVendor");

        cmmnRuntimeService.setVariable(caseInstanceId, "quadrant", "third");
        logger.info("----------- btmEmail : {}, etv2Email : {}", btmEmail, etv1Email);

      } else {
        etv1Email =
            allocationServiceTask.allocationLogic(
                planItemInstance.getCaseInstanceId(),
                "BACK OFFICE",
                "",
                "ExternalTechnicalVendor_" + branchName,
                "ExternalTechnicalVendor");
        etv2Email =
            allocationServiceTask.allocationLogic(
                planItemInstance.getCaseInstanceId(),
                "BACK OFFICE",
                "",
                "ExternalTechnicalVendor_" + branchName,
                "ExternalTechnicalVendor");
        cmmnRuntimeService.setVariable(caseInstanceId, "quadrant", "fourth");
        logger.info("----------- etv1Email : {}, etv2Email : {} ", etv1Email, etv2Email);
      }
    }
    cmmnRuntimeService.setVariable(caseInstanceId, "btmEmail", btmEmail);
    cmmnRuntimeService.setVariable(caseInstanceId, "etv1Email", etv1Email);
    cmmnRuntimeService.setVariable(caseInstanceId, "etv2Email", etv2Email);
  }

  public void masterTest() {
    JSONObject vendorMasterObject = masterHelper.getRequestFromMaster("capri-vendor-master");
    JSONObject hrmisMasterObject = masterHelper.getRequestFromMaster("capri-hrmis");

    logger.info("------- vendorMaster : {}", vendorMasterObject);
    logger.info("------- hrmis : {}", hrmisMasterObject);
  }

  public static void main(String[] args) throws InterruptedException {
    int index = 0;
    List<String> bcmRoleList = new ArrayList<>();
    bcmRoleList.add("One");
    bcmRoleList.add("Two");
    bcmRoleList.add("OnThreee");
    bcmRoleList.add("Four");
    bcmRoleList.add("five");
    bcmRoleList.add("six");
    if (index >= bcmRoleList.size()) {
      index = 0;
    }
    //      String randomAssignee = bcmRoleList.get(new Random().nextInt(bcmRoleList.size()));
    String randomAssignee = bcmRoleList.get(index++);
    System.out.println(randomAssignee);
    bcmRoleList.remove(randomAssignee);
  }
  //
  //        HttpResponse<String> response =
  //            Unirest.get("https://los.capriglobal.dev/masters/product/api/masters/capri-hrmis")
  //                .header(
  //                    "authorization",
  //                    "Bearer
  //
  // eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3QmJpejR4a3VlM2ZpZlllMnhtMmEtVWo5RXYxejNGbFQzcjNVUUQ5QjBNIn0.eyJleHAiOjE2NDQ5NDk1MTQsImlhdCI6MTY0NDk0NzcxNCwiYXV0aF90aW1lIjoxNjQ0OTQ3NzE0LCJqdGkiOiI0YjllMjU4ZS00YTdjLTQxNzktOTYyMy03NzcxOTBiNWQwMGIiLCJpc3MiOiJodHRwczovL2xvcy5jYXByaWdsb2JhbC5kZXYvYXV0aC9yZWFsbXMva3VsaXphX3JlYWxtIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6Ijc1OGI2MDk5LWMxYjUtNGE0YS1iMzQ4LTkyNWUyMmQ1NzI1YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImxlbmRpbmciLCJub25jZSI6ImZxSGYzTFJHOEQ0R3pMeF9BdmhwVUtvVUg4dFJtVFdZLWpZYkhmUF9Hb2siLCJzZXNzaW9uX3N0YXRlIjoiODg5NmU2NTAtNDRkNC00MjM2LWI0NmItMWJhNmI5ZmY5YzYzIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtYXN0ZXJzX2FkbWluIiwiYWNjZXNzLWlkbSIsIm9mZmxpbmVfYWNjZXNzIiwiYWNjZXNzLXRhc2siLCJhY2Nlc3MtbW9kZWxlciIsInVtYV9hdXRob3JpemF0aW9uIiwiYWNjZXNzLWFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJsZW5kaW5nIjp7InJvbGVzIjpbIm1hc3RlcnNfYWRtaW4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGFkZHJlc3MgZW1haWwgcGhvbmUgbWljcm9wcm9maWxlLWp3dCBvZmZsaW5lX2FjY2VzcyBwcm9maWxlIiwidXBuIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoic3R1ZGlvIGFkbWluIiwiZ3JvdXBzIjpbIm1hc3RlcnNfYWRtaW4iLCJhY2Nlc3MtaWRtIiwib2ZmbGluZV9hY2Nlc3MiLCJhY2Nlc3MtdGFzayIsImFjY2Vzcy1tb2RlbGVyIiwidW1hX2F1dGhvcml6YXRpb24iLCJhY2Nlc3MtYWRtaW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJnaXZlbl9uYW1lIjoic3R1ZGlvIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20ifQ.KzFqll_fz5DQmgWKjIg374FFMjCKYPdSj5bk1Ii2HwsKBEHyIxIPKpTXlpzrubyYKeNjEp2j7QvdHH8Ud4a0nbDZQBbX_xU6yJogqg4LRIROhiYhthzviJGm1lDmuT5E2aeyfaowNIP-Nv5M4AppfLWB4VgKs0RWgtbcBxCsTMD2lc4G_-ILYJr_oUOrg0ByN3GB0WflAdgAb8BqUyWJXh6oDcGaHkVkoQl04JlLPgy_5LQKV1kxjJrsKsAwf552uONbXUf-uKfpscwqCpHpx7EjkYYUcIyurWrOYVnkVy-0kvY0d7s7AtjrdX_X7u-0WM3s0JYLKER8_WXUWqwdUw")
  //                .asString();
  //
  //    HttpResponse<String> response =
  //        Unirest.get(
  //
  // "https://los.capriglobal.dev/masters/product/api/masters/data/definition/capri-account-type-map")
  //            .header(
  //                "authorization",
  //                "Bearer
  // eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3QmJpejR4a3VlM2ZpZlllMnhtMmEtVWo5RXYxejNGbFQzcjNVUUQ5QjBNIn0.eyJleHAiOjE2NDQ5NDk1MTQsImlhdCI6MTY0NDk0NzcxNCwiYXV0aF90aW1lIjoxNjQ0OTQ3NzE0LCJqdGkiOiI0YjllMjU4ZS00YTdjLTQxNzktOTYyMy03NzcxOTBiNWQwMGIiLCJpc3MiOiJodHRwczovL2xvcy5jYXByaWdsb2JhbC5kZXYvYXV0aC9yZWFsbXMva3VsaXphX3JlYWxtIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6Ijc1OGI2MDk5LWMxYjUtNGE0YS1iMzQ4LTkyNWUyMmQ1NzI1YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImxlbmRpbmciLCJub25jZSI6ImZxSGYzTFJHOEQ0R3pMeF9BdmhwVUtvVUg4dFJtVFdZLWpZYkhmUF9Hb2siLCJzZXNzaW9uX3N0YXRlIjoiODg5NmU2NTAtNDRkNC00MjM2LWI0NmItMWJhNmI5ZmY5YzYzIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtYXN0ZXJzX2FkbWluIiwiYWNjZXNzLWlkbSIsIm9mZmxpbmVfYWNjZXNzIiwiYWNjZXNzLXRhc2siLCJhY2Nlc3MtbW9kZWxlciIsInVtYV9hdXRob3JpemF0aW9uIiwiYWNjZXNzLWFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJsZW5kaW5nIjp7InJvbGVzIjpbIm1hc3RlcnNfYWRtaW4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGFkZHJlc3MgZW1haWwgcGhvbmUgbWljcm9wcm9maWxlLWp3dCBvZmZsaW5lX2FjY2VzcyBwcm9maWxlIiwidXBuIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoic3R1ZGlvIGFkbWluIiwiZ3JvdXBzIjpbIm1hc3RlcnNfYWRtaW4iLCJhY2Nlc3MtaWRtIiwib2ZmbGluZV9hY2Nlc3MiLCJhY2Nlc3MtdGFzayIsImFjY2Vzcy1tb2RlbGVyIiwidW1hX2F1dGhvcml6YXRpb24iLCJhY2Nlc3MtYWRtaW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJnaXZlbl9uYW1lIjoic3R1ZGlvIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20ifQ.KzFqll_fz5DQmgWKjIg374FFMjCKYPdSj5bk1Ii2HwsKBEHyIxIPKpTXlpzrubyYKeNjEp2j7QvdHH8Ud4a0nbDZQBbX_xU6yJogqg4LRIROhiYhthzviJGm1lDmuT5E2aeyfaowNIP-Nv5M4AppfLWB4VgKs0RWgtbcBxCsTMD2lc4G_-ILYJr_oUOrg0ByN3GB0WflAdgAb8BqUyWJXh6oDcGaHkVkoQl04JlLPgy_5LQKV1kxjJrsKsAwf552uONbXUf-uKfpscwqCpHpx7EjkYYUcIyurWrOYVnkVy-0kvY0d7s7AtjrdX_X7u-0WM3s0JYLKER8_WXUWqwdUw")
  //            .asString();
  //    logger.info("hrmis master response :- {}", response.getBody());
  //
  //    JSONObject data = new JSONObject(response.getBody());
  //    logger.info("data :- {}", data);
  //
  //    JSONArray dataStorageList = data.getJSONArray("dataStorageList");
  //    logger.info("dataStorageList :- {}", dataStorageList);
  //
  //    JSONArray HRMIS = data.getJSONArray("data");
  //
  //    logger.info("HRMIS :- {}", HRMIS);
  //
  //
  //
  //    double p = 13375748;
  //    double r = 6.7;
  //    double n = 60.0;
  //    double rate = r/1200;
  //
  //    double proposedEMI = (p * rate * Math.pow(1 + rate, n)) / ( Math.pow(1 + rate, n) - 1);
  //    logger.info("proposedEmi : {} ", Math.round(proposedEMI));
  //
  //  }
}
