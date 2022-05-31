package com.kuliza.workbench.service;

import com.google.gson.Gson;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetRejectionFlagService {

  private static final Logger logger = LoggerFactory.getLogger(R2CScenariosService.class);

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public void setRejectionFlag(Map<String, Object> request) throws JSONException {
    logger.info("--------- setRejectionFlag request : {}", request);

    String caseInstanceId = request.get("caseInstanceId").toString();
    String cmlType = request.get("cmlType").toString();
    JSONArray referToCreditTable =
        new JSONArray(new Gson().toJson(request.get("refertoCredit_hl")));

    logger.info("-------- referToCreditTable : {}", referToCreditTable);
    if (cmlType.equalsIgnoreCase("CML1")) {
      boolean isSuccessful = true;
      for (int i = 0; i < referToCreditTable.length(); i++) {
        String verificationStatus =
            referToCreditTable.getJSONObject(i).get("r2cVerificationStatusCML1_hl").toString();
        if (verificationStatus.equalsIgnoreCase("Verification not successful")) {
          cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML1", "yes");
          isSuccessful = false;
        }
      }
      if (isSuccessful) {
        cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML1", "no");
      }
    }

    if (cmlType.equalsIgnoreCase("CML2")) {
      boolean isSuccessful = true;
      for (int i = 0; i < referToCreditTable.length(); i++) {
        String verificationStatus =
            referToCreditTable.getJSONObject(i).get("r2cVerificationStatusCML2_hl").toString();
        if (verificationStatus.equalsIgnoreCase("Verification not successful")) {
          cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML2", "yes");
          isSuccessful = false;
        }
      }
      if (isSuccessful) {
        cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML2", "no");
      }
    }

    if (cmlType.equalsIgnoreCase("CML3")) {
      boolean isSuccessful = true;
      for (int i = 0; i < referToCreditTable.length(); i++) {
        String verificationStatus =
            referToCreditTable.getJSONObject(i).get("r2cVerificationStatusCML3_hl").toString();
        if (verificationStatus.equalsIgnoreCase("Verification not successful")) {
          cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML3", "yes");
          isSuccessful = false;
        }
      }
      if (isSuccessful) {
        cmmnRuntimeService.setVariable(caseInstanceId, "r2cRejectedCML3", "no");
      }
    }
  }
}
