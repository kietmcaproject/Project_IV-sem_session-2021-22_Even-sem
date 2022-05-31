package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class YesBankService {
  private static final Logger logger = LoggerFactory.getLogger(YesBankService.class);

  @Autowired IbHelper ibHelper;

  public ApiResponse domesticPayments(Map<String, Object> request) throws UnirestException {
    logger.info("Inside domestic payments....................");
    logger.info("Request Body of domestic payments:- {}", request);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("yes-banlk/domestic-payments"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(request))
            .asString();
    logger.info("Response of domestic Payments :- {}", response.getBody());
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    return new ApiResponse(HttpStatus.OK, "Success", res);
  }

  public ApiResponse paymentDetails(Map<String, Object> request) throws UnirestException {
    logger.info("Inside details of payment....................");
    logger.info("Request Body of details of payment:- {}", request);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("yes-banlk/payment-details"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(request))
            .asString();
    logger.info("Response of payment Details :- {}", response.getBody());
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    return new ApiResponse(HttpStatus.OK, "Success", res);
  }

  public ApiResponse fundConfirmation(Map<String, Object> request) throws UnirestException {
    logger.info("Inside fund Confirmation....................");
    logger.info("Request Body of fund Confirmation:- {}", request);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("yes-banlk/fund-confirmation"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(request))
            .asString();
    logger.info("Response of fund Confirmation :- {}", response.getBody());
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    return new ApiResponse(HttpStatus.OK, "Success", res);
  }
}
