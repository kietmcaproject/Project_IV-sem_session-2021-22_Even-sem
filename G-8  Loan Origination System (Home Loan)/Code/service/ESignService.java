package com.kuliza.workbench.service;

import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ESignService {
  @Autowired IbHelper ibHelper;
  @Autowired RuntimeService runtimeService;
  @Autowired CapriUtilService capriUtilService;
  private static final Logger logger = LoggerFactory.getLogger(ENachService.class);

  @Value("${enach.baseUrl}")
  private String baseUrl;

  public DelegateExecution createSignatureRequest(DelegateExecution execution)
      throws JSONException, UnirestException {
    String processInstanceId = execution.getProcessInstanceId();
    logger.info("----- Inside Create Signature API -----");

    Map<String, String> headers = getSignatureHeaders();
    String payload = getCreateSignaturePayload(processInstanceId);

    logger.info("Create Signature Request Payload :- {}", payload);
    try {
      HttpResponse<String> response =
          Unirest.post(ibHelper.getSoapUrl("capri-esign/create-signature-request"))
              .headers(headers)
              .body(new JSONObject(payload))
              .asString();
      logger.info("Response From Create Signature API : {}", response.getBody());
      JSONObject responseObject = new JSONObject(response.getBody());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return execution;
  }

  public Map<String, String> getSignatureHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put(
        "Authorization",
        "Basic QUk0TEFMVDdSTVEzRFVHRFhSNThLNzg2R1NTOFlWRUg6VFZNTjdEVURBNFpLUUJGVjFRUDNHOUxVRkVURTNNMzQ=");
    return headers;
  }

  public String getCreateSignaturePayload(String processInstanceId) {
    Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
    String payload = "";

    return payload;
  }
}
