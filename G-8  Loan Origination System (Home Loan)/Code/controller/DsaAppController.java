package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.DsaAppService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DsaAppController {
  private static final Logger logger = LoggerFactory.getLogger(DsaAppController.class);

  @Autowired DsaAppService dsaAppService;

  @PostMapping(value = "/lead-status")
  public ApiResponse leadStatusApi(@RequestBody Map<String, String> request) {
    if (!request.containsKey("ucic")) {
      return new ApiResponse(HttpStatus.BAD_REQUEST, "Customer UCIC is mandatory");
    }
    return dsaAppService.getLeadStatus(request);
  }
}
