package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.AssignStatusService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssignStatusController {
  @Autowired AssignStatusService assignStatusService;

  @RequestMapping(method = RequestMethod.POST, value = "/assignStatus")
  public void status(@RequestBody Map<String, Object> request) throws JSONException {

    assignStatusService.statusAssign(request.get("caseInstanceId").toString());
  }
}
