package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.ChangeVariableValueService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeVariableValueController {
  @Autowired ChangeVariableValueService changeVariableValueService;

  @RequestMapping(method = RequestMethod.POST, value = "/changeValue")
  public ApiResponse changeValue(@RequestBody Map<String, String> request) throws JSONException {

    return changeVariableValueService.changeVariable(request);
  }
}
