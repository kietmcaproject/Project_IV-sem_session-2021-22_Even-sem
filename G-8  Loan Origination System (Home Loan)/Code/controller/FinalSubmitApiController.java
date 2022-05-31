package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.FinalSubmitApiService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinalSubmitApiController {

  @Autowired FinalSubmitApiService finalSubmitApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/final-submit")
  public ApiResponse finalSubmitApi(@RequestBody Map<String, Object> request) throws JSONException {

    return finalSubmitApiService.finalSubmit(request);
  }
}
