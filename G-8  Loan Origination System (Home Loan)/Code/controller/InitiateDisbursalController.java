package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.InitiateDisbursalService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InitiateDisbursalController {
  @Autowired InitiateDisbursalService initiateDisbursalService;

  @PostMapping("/initiateDisbursal")
  public ApiResponse disburse(@RequestBody Map<String, Object> request)
      throws JSONException, IOException, UnirestException {

    return initiateDisbursalService.initiate(request);
  }
}
