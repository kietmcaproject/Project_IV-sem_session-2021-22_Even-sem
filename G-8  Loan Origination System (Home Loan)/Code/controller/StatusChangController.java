package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.StatusChangeService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusChangController {
  @Autowired StatusChangeService statusChangeService;

  @RequestMapping(method = RequestMethod.POST, value = "/changeStatus")
  public void restDropdownApi(@RequestBody Map<String, String> currentRoleRequest)
      throws JSONException {

    statusChangeService.changeStatus(currentRoleRequest);
  }
}
