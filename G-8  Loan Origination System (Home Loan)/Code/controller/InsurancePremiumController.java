package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.InsurancePremiumService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InsurancePremiumController {
  @Autowired InsurancePremiumService insurancePremiumService;

  @PostMapping("/calculatePremium")
  public void GcppCalculator(@RequestBody Map<String, Object> request)
      throws JSONException, IOException, UnirestException {

    insurancePremiumService.calculatePremium(request);
  }

  @PostMapping("/premiumStatus")
  public ApiResponse status(@RequestBody Map<String, String> request)
      throws JSONException, IOException, UnirestException {

    return insurancePremiumService.checkStatus(request);
  }
}
