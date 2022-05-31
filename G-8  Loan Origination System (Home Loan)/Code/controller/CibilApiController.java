package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.model.FinalCibilRequest;
import com.kuliza.workbench.service.CibilApiService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CibilApiController {
  private static final Logger logger = LoggerFactory.getLogger(CibilApiController.class);

  @Autowired private CibilApiService cibilApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/cibil")
  public ApiResponse getResponseFromIb(@RequestBody FinalCibilRequest cibilRequest)
      throws UnirestException, JSONException, IOException, ParseException, TransformerException {
    return cibilApiService.cibilApi(cibilRequest);
  }
}
