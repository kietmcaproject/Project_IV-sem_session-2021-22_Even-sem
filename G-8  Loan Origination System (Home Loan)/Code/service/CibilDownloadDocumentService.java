package com.kuliza.workbench.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.model.CibilDownloadDocument;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CibilDownloadDocumentService {

  public ApiResponse cibilDownload(CibilDownloadDocument cibilDownloadDocument)
      throws UnirestException, JSONException, JsonProcessingException {
    String url =
        "http://65.0.168.18:8080/ib/api/external-integration/soap/soap-demo/downloaddocumentbasichttp/fedfina/W89Mwvj9YCzL5UaYEmwYE_NojOyYK12439hl7bdm00pZEXCINoQigqa6lEbO2JY8/?binding=basicHttp&env=dev&raw=false";
    Map<String, Object> request = new HashMap<>();
    request.put("header", null);
    Map<String, Object> body = new HashMap<>();
    body.put(
        "tem:DownloadDocument.tem:request",
        "\n\t\t<DCRequest xmlns=\"http://transunion.com/dc/extsvc\">\n  <Authentication type=\"Token\">\n    <UserId>NB6877DC01_UAT002</UserId>\n    <Password>CibilUAT@12345678</Password>\n  </Authentication>\n  <DownloadDocument>\n    <ApplicationId>"
            + cibilDownloadDocument.getApplicationId()
            + "</ApplicationId>\n    <DocumentId>"
            + cibilDownloadDocument.getDocumentId()
            + "</DocumentId>\n  </DownloadDocument>\n</DCRequest>        \n");
    request.put("body", body);

    HttpResponse<String> response =
        Unirest.post(url)
            .header("Content-Type", "application/json")
            .body(new JSONObject(request))
            .asString();

    return new ApiResponse(HttpStatus.OK, "SUCCESS", response.getBody());
  }
}
