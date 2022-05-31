package com.kuliza.workbench.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.BcmAllocationService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BcmAllocationController {
  @Autowired BcmAllocationService bcmAllocationService;

  @PostMapping("/bcmAllocation")
  public ApiResponse consumerAppStatus(@RequestBody Map<String, String> request)
      throws JSONException, JsonProcessingException {

    return bcmAllocationService.allocateBcm(request);
  }
}
