package com.kuliza.workbench.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.CibilDownloadDocument;
import com.kuliza.workbench.service.CibilDownloadDocumentService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CibilDownloadDocumentController {
  @Autowired CibilDownloadDocumentService cibilDownloadDocumentService;

  @RequestMapping(method = RequestMethod.POST, value = "/downloadCibilDocument")
  public ResponseEntity<Object> getResponseFromIb(
      @RequestBody CibilDownloadDocument cibilDownloadDocument)
      throws UnirestException, JSONException, JsonProcessingException {

    return CommonHelperFunctions.buildResponseEntity(
        cibilDownloadDocumentService.cibilDownload(cibilDownloadDocument));
  }
}
