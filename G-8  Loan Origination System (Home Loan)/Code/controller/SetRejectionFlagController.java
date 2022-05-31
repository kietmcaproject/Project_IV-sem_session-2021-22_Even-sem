package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.SetRejectionFlagService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetRejectionFlagController {
  @Autowired SetRejectionFlagService setRejectionFlagService;

  @RequestMapping(method = RequestMethod.POST, value = "/setRejectionFlag")
  public void setRejectionFlagApi(@RequestBody Map<String, Object> request) throws JSONException {
    setRejectionFlagService.setRejectionFlag(request);
  }
}
