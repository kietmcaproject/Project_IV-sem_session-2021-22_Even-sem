package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.RestDropdownService;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestDropdownController {
  @Autowired RestDropdownService restDropdownService;

  @RequestMapping(method = RequestMethod.POST, value = "/rest")
  public String restDropdownApi(@RequestBody Map<String, Object> request) throws JSONException {

    return restDropdownService.restDropdown(request.get("processInstanceId").toString());
  }

  @RequestMapping(method = RequestMethod.POST, value = "/filterByBranch")
  public String filterRolesByBranchApi(@RequestBody Map<String, Object> request)
      throws JSONException {

    return restDropdownService.filterRolesByBranch(request.get("branch").toString());
  }

  @RequestMapping(method = RequestMethod.POST, value = "/multiTranche")
  public String multiTranchRestDropdownApi(@RequestBody Map<String, Object> request)
      throws JSONException {

    return restDropdownService.multiTranchRestDropdown(request.get("caseInstanceId").toString());
  }
}
