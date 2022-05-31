package com.kuliza.workbench.service;

import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PennyDropApiService")
public class PennyDropApiService {

  @Autowired IbHelper ibHelper;

  private static final Logger logger = LoggerFactory.getLogger(PennyDropApiService.class);

  @Autowired NameSimilarityService nameSimilarityService;

  public DelegateExecution callPennyDrop(DelegateExecution execution)
      throws JSONException, UnirestException {

    String bankAccountNumber = String.valueOf(execution.getVariable("bankAccountNumber_hl"));
    String ifscCode = String.valueOf(execution.getVariable("ifscCode_hl"));
    //    bankAccountNumber = "123110023204445";
    //    ifscCode = "UTIB0000889";

    JSONObject response = new JSONObject();
    try {
      JSONObject request = new JSONObject();
      request.put("beneficiary_account_no", bankAccountNumber);
      request.put("beneficiary_ifsc", ifscCode);
      logger.info("----- Penny Drop request : {}", request);
      HttpResponse<String> httpResponse =
          Unirest.post(ibHelper.getUrl("digio-apis/bank-account-verification-penny-drop"))
              .header("Content-Type", "application/json")
              .body(request)
              .asString();
      response = new JSONObject(httpResponse.getBody());
      logger.info("----- Penny Drop response : {}", httpResponse.getBody());
    } catch (JSONException | UnirestException e) {
      logger.info("----- Error in hitting Penny Drop api");
      e.printStackTrace();
    }
    boolean verified = response.getBoolean("verified");
    String beneficiaryNameWithBank = response.get("beneficiary_name_with_bank").toString();
    if (verified) {
      execution.setVariable("beneficiaryNameWithBank_hl", beneficiaryNameWithBank);
      nameSimilarityService.nameMatchApiForAcc(execution, beneficiaryNameWithBank);
    }
    return execution;
  }
}
