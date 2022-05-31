package com.kuliza.workbench.controller;

import static com.kuliza.workbench.util.WorkbenchConstants.WORKBENCH_STARTER_API_PREFIX;
import static com.kuliza.workbench.util.WorkbenchConstants.WORKBENCH_STARTER_PROP_ENDPOINT;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.WorkbenchRestController;
import com.kuliza.workbench.pojo.GettingStartedPojo;
import com.kuliza.workbench.service.GettingStartedService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@WorkbenchRestController
@RequestMapping(WORKBENCH_STARTER_API_PREFIX)
public class GettingStartedController {

  @Autowired private GettingStartedService starterService;

  @RequestMapping(method = RequestMethod.GET, value = WORKBENCH_STARTER_PROP_ENDPOINT)
  public ResponseEntity<Object> getGlobalProperties(
      @RequestParam(required = false) String propName) {
    return CommonHelperFunctions.buildResponseEntity(starterService.getGlobalProp(propName));
  }

  @RequestMapping(method = RequestMethod.POST, value = WORKBENCH_STARTER_PROP_ENDPOINT)
  public ResponseEntity<Object> createOrUpdateGlobalPrroperties(
      @RequestBody @Valid GettingStartedPojo property) {
    return CommonHelperFunctions.buildResponseEntity(
        starterService.createOrUpdateGlobalProp(property));
  }
}
