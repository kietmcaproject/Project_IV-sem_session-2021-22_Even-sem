package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.YesBankService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YesBankController {

  @Autowired YesBankService yesBankService;

  @PostMapping("/domestic-payments")
  public ApiResponse payments(@RequestBody Map<String, Object> request)
      throws JSONException, IOException, UnirestException {

    return yesBankService.domesticPayments(request);
  }

  @PostMapping("/payment-details")
  public ApiResponse detailsOfPayment(@RequestBody Map<String, Object> request)
      throws JSONException, IOException, UnirestException {

    return yesBankService.paymentDetails(request);
  }

  @PostMapping("/fund-confirmation")
  public ApiResponse confirmationOfFund(@RequestBody Map<String, Object> request)
      throws JSONException, IOException, UnirestException {

    return yesBankService.fundConfirmation(request);
  }
}
