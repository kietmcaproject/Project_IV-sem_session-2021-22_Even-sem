package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.RaiseToService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RaiseToController {
  @Autowired RaiseToService raiseToService;

  @RequestMapping(method = RequestMethod.POST, value = "/raiseTo")
  public String raiseToApi(@RequestBody Map<String, String> request) throws JSONException {
    return raiseToService.raiseTo(request);
  }
}
