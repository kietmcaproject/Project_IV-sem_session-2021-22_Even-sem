package com.kuliza.workbench.controller;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.repository.CustomerEmailRepository;
import com.kuliza.workbench.service.ENachService;
import com.kuliza.workbench.service.EmpVerificationApiService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import javax.mail.MessagingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CallBackController {
  private static final Logger logger = LoggerFactory.getLogger(CallBackController.class);

  @Autowired CustomerEmailRepository customerEmailRepository;
  @Autowired ENachService eNachService;
  @Autowired EmpVerificationApiService empVerificationApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/rollBackResponse")
  public ApiResponse CallBackApi(@RequestBody Map<String, Object> request) throws JSONException {
    String requestId = String.valueOf(request.get("requestId"));
    String verificationStatus = String.valueOf(request.get("status"));
    boolean isLinkTriggered = !verificationStatus.equals("EXPIRED");
    logger.info("requestId : {}        ,  verificationStatus : {}", requestId, verificationStatus);

    customerEmailRepository.updateCustomerVerified(requestId, verificationStatus);
    customerEmailRepository.updateLinkTriggered(requestId, isLinkTriggered);
    logger.info("Call Back from karza Email : {} ", request);
    return new ApiResponse(200, "ok", "ok");
  }

  @RequestMapping(method = RequestMethod.POST, value = "/form26asResponse")
  public ApiResponse form26asApi(@RequestBody Map<String, Object> request) {
    logger.info("form26As api callback response : {}", request);
    JSONObject callBackResponse = new JSONObject();
    try {
      callBackResponse = new JSONObject(new Gson().toJson(request));
      empVerificationApiService.processForm26AsResponse(callBackResponse);
    } catch (JSONException e) {
      logger.info("Error while processing from26AS callback response");
      e.printStackTrace();
    }

    logger.info("Form26As api callback response : {}", callBackResponse);
    return new ApiResponse(200, "Success", "Ok");
  }

  @RequestMapping(
      method = {RequestMethod.POST, RequestMethod.GET},
      value = "/enachResponse")
  public ApiResponse enachAuthenticationResponse(
      @RequestBody(required = false) Map<String, Object> request,
      @RequestParam(required = false) Map<String, Object> test)
      throws MessagingException, UnirestException, JSONException {
    if (test != null && test.containsKey("status")) {
      logger.info("Enach api Authentication call back first response parameters:- {}", test);
      return new ApiResponse(200, "Success", "Ok");
    } else {
      logger.info(
          "Enach api Authentication call back second response parameters request :- {}", request);
      eNachService.callBackResponse(request); // TODO: callback url not coming
      return new ApiResponse(200, "Success", "Ok");
    }
  }

  @RequestMapping(
      method = {RequestMethod.POST, RequestMethod.GET},
      value = "/mandateResponse")
  public ApiResponse enachMandateResponse(@RequestBody Map<String, Object> request)
      throws MessagingException, JSONException, UnirestException {
    logger.info("Enach api Mandate call back response parameters request1: {}", request);
    eNachService.callBackResponse(request); // TODO: callback url not coming
    return new ApiResponse(200, "Success", "Ok");
  }
}
