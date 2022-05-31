package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConsumerAppApiService {
  private static final Logger logger = LoggerFactory.getLogger(ConsumerAppApiService.class);

  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;
  @Autowired RuntimeService runtimeService;

  public ApiResponse getConsumerAppStatus(Map<String, String> request)
      throws JSONException, JSONException {
    String appStatus = "";
    String ucicNoExist = "";
    JSONObject details = new JSONObject();
    int applicantCount = -1;
    String ucic = request.get("ucic").toString();
    try {
      PragatiAppDetails ucicExists = pragatiAppDetailsRepository.findByUcic(ucic);
      ucicNoExist = ucicExists.getUcic();
    } catch (Exception e) {
    }
    if (ucicNoExist.equalsIgnoreCase("")) {
      return new ApiResponse(HttpStatus.NOT_FOUND, "Customer Number doest not exist");
    } else {
      PragatiAppDetails pragatiAppDetails = pragatiAppDetailsRepository.findByUcic(ucic);
      logger.info("pragatiAppDetails :-{}", pragatiAppDetails.getProcessInstanceId());
      String processInstanceId = pragatiAppDetails.getProcessInstanceId();
      Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
      JSONObject object = new JSONObject(new Gson().toJson(variables));
      logger.info("variables :-{}", object);

      try {
        applicantCount = object.getInt("applicantCount_hl");
      } catch (Exception e) {
      }

      if (applicantCount == -1) {
        return new ApiResponse(HttpStatus.NOT_FOUND, "Inquiry Number doest not exist");
      }
      try {
        appStatus = object.getString("consumerAppStatus_hl");
        logger.info("appStatus.......:- {}", appStatus);
      } catch (Exception e) {
        e.printStackTrace();
      }

      JSONArray ucicArray = new JSONArray();
      JSONObject ucicObject = new JSONObject();
      String ucicId = "";

      for (int i = 0; i < applicantCount; i++) {
        try {
          ucicId = object.getString("applicant" + (i + 1) + "UcicId_hl");
        } catch (Exception e) {
        }

        ucicObject.put("applicant" + (i + 1), ucicId);
      }
      ucicArray.put(ucicObject);

      JSONArray coApplicantName = new JSONArray();
      JSONObject coApplicantNameObject = new JSONObject();
      for (int i = 1; i < applicantCount; i++) {
        coApplicantNameObject.put(
            "CoApplicant" + i, object.getString("applicant" + (i + 1) + "Name_hl"));
      }
      coApplicantName.put(coApplicantNameObject);

      if (appStatus.equalsIgnoreCase("")) {
        return new ApiResponse(HttpStatus.NOT_FOUND, "Consumer App Status Not Found");
      }

      if (appStatus.equalsIgnoreCase("Under Progress")) {
        details.put("TypeOfLoan", object.getString("endUseOfLoanString_hl"));
        details.put("LANId", object.getString("loanId_hl"));
        details.put("CustomerId", ucic);
        details.put("Status", object.getString("consumerAppStatus_hl"));
        details.put("ApplicantName", object.getString("applicant1Name_hl"));
        details.put("CoApplicantName", coApplicantName);
        logger.info("UnderProgressDetails.......:- {}", details);
      }

      if (appStatus.equalsIgnoreCase("Rejected")) {
        details.put("TypeOfLoan", object.getString("endUseOfLoanString_hl"));
        details.put("LANId", object.getString("loanId_hl"));
        details.put("CustomerId", ucic);
        details.put("Status", object.getString("consumerAppStatus_hl"));
        try {
          String cml1Remarks = object.getString("cml1Remarks_hl");
          if (cml1Remarks.isEmpty() || cml1Remarks.equals("")) {
            details.put("RejectedRemarks", "NA");
          } else {
            details.put("RejectedRemarks", cml1Remarks);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        details.put("ApplicantName", object.getString("applicant1Name_hl"));
        details.put("CoApplicantName", coApplicantName);
        logger.info("RejectedDetails.......:- {}", details);
      }
    }
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(details.toString());
    return new ApiResponse(HttpStatus.OK, "SUCCESS", res);
  }
}
