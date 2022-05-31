package com.kuliza.workbench.controller;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.service.CommunicationService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommunicationController {

  @Autowired private CommunicationService communicationService;

  @RequestMapping(method = RequestMethod.POST, value = "/communication")
  public ResponseEntity<Object> communication(@RequestBody Map<String, Object> request) {
    return CommonHelperFunctions.buildResponseEntity(communicationService.setData(request));
  }
}
