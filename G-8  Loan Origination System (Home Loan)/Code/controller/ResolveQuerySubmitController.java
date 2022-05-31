package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.ResolveQuerySubmitService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResolveQuerySubmitController {
  @Autowired ResolveQuerySubmitService resolveQuerySubmitService;

  @RequestMapping(method = RequestMethod.POST, value = "/checkResolutionTimestamp")
  public void restDropdownApi(@RequestBody Map<String, String> request) throws JSONException {

    resolveQuerySubmitService.checkResolutionTimestamp(
        request.get("caseInstanceId").toString(), request.get("currentRole").toString());
  }
}
