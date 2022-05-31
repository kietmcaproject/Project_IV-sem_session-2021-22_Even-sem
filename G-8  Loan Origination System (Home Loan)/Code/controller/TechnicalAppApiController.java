package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.model.TechAppRequest;
import com.kuliza.workbench.service.TechnicalAppApiService;
import java.util.Map;
import javax.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TechnicalAppApiController {

  @Autowired TechnicalAppApiService technicalAppApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/fetchByActivityCode")
  public ApiResponse techFetchOnAct(@RequestBody Map<String, Object> request) throws JSONException {
    return technicalAppApiService.fetchCases(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/sendDetailsToLos")
  public ApiResponse receiveDetails(@Valid @RequestBody TechAppRequest techAppRequest)
      throws JSONException {
    return technicalAppApiService.receiveDetailsFromTechApp(techAppRequest);
  }
}
