package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.AbstractResponse;
import com.kuliza.workbench.service.StartInternalProcessService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartInternalProcessController {
  @Autowired private StartInternalProcessService startInternalProcessService;

  @RequestMapping(method = RequestMethod.POST, value = "/startInternalProcess")
  public AbstractResponse getResponse(@RequestBody Map<String, String> workflowUserName)
      throws Exception {
    return startInternalProcessService.startProcess(workflowUserName);
  }
}
