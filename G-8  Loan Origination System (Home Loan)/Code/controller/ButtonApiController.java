package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.ButtonApiService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ButtonApiController {
  @Autowired ButtonApiService buttonApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/enableRecalculateBtn")
  public void recalculateButtonApi(@RequestBody Map<String, Object> request) {
    buttonApiService.enableRecalculate(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/changeRoleStatusBtn")
  public String roleStatusChangeButtonApi(@RequestBody Map<String, Object> request) {
    return buttonApiService.changeRoleStatus(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/directlyChangeRoleStatusBtn")
  public ApiResponse directlyChangeRoleStatusApi(@RequestBody Map<String, Object> request) {
    return buttonApiService.directlyChangeRoleStatus(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/finalApproval")
  public ApiResponse finalApprovalApi(@RequestBody Map<String, Object> request) {
    return buttonApiService.finalApproval(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/finalRejection")
  public ApiResponse finalRejectionApi(@RequestBody Map<String, Object> request) {
    return buttonApiService.finalRejection(request);
  }
}
