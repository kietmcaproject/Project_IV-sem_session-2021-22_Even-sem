package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EStampService {
  @Autowired IbHelper ibHelper;
  @Autowired RuntimeService runtimeService;
  @Autowired CapriUtilService capriUtilService;
  private static final Logger logger = LoggerFactory.getLogger(EStampService.class);

  public DelegateExecution createSignatureRequest(DelegateExecution execution)
      throws JSONException, UnirestException, IOException {
    String processInstanceId = execution.getProcessInstanceId();
    logger.info("----- Inside Create Signature API -----");

    Map<String, String> headers = getSignatureHeaders();
    Map<String, Object> payload = getCreateSignaturePayload(processInstanceId);

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

  public Map<String, Object> getCreateSignaturePayload(String processInstanceId)
      throws IOException, JSONException {
    //    Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
    String payload = "";
    Map<String, Object> requestBody = new HashMap<>();
    List<Object> signersList = new ArrayList<>();
    Map<String, Object> signers = new HashMap<>();
    signers.put("identifier", "8601580222");
    //    signers.put("name", "Anupam K");
    //    signers.put("sign_type", "");
    //    signers.put("reason", "");
    signersList.add(signers);
    requestBody.put("signers", signersList);
    //    requestBody.put("expire_in_days", 10);
    //    requestBody.put("display_on_page", "custom");
    requestBody.put("notify_signers", true);
    requestBody.put("send_sign_link", true);
    requestBody.put("file_name", "test.pdf");
    String encoded =
        capriUtilService.encodeBase64String(
            "/home/kuliza-523/Downloads/statement_anupam_18Jan2022_13_46 (5).pdf");
    requestBody.put("file_data", encoded);
    Map<String, Object> signCoordinates = new HashMap<>();
    String identifier =
        new JSONArray(new Gson().toJson(signersList)).getJSONObject(0).get("identifier").toString();
    Map<String, Object> identifierValue = new HashMap<>();
    List<Object> coordinatesList = new ArrayList<>();
    Map<String, Object> coordinates = new HashMap<>();
    coordinates.put("llx", 54);
    coordinates.put("lly", 431);
    coordinates.put("urx", 194);
    coordinates.put("ury", 471);
    coordinatesList.add(coordinates);
    identifierValue.put("1", coordinatesList);
    signCoordinates.put(identifier, identifierValue);
    requestBody.put("sign_coordinates", signCoordinates);
    Map<String, Object> eStampRequest = new HashMap<>();
    Map<String, Object> tags = new HashMap<>();
    tags.put("MH-100-MH_100", 1);
    eStampRequest.put("tags", tags);
    eStampRequest.put("sign_on_page", "ALL");
    eStampRequest.put("note_content", "");
    eStampRequest.put("note_on_page", "ALL");

    //    requestBody.put("estamp_request", eStampRequest);
    //    logger.info("request body estamp : {}", requestBody);

    return requestBody;
  }

  public void createSignatureRequestTest() throws JSONException, UnirestException, IOException {
    //    String processInstanceId = execution.getProcessInstanceId();
    logger.info("----- Inside Create Signature API -----");

    Map<String, String> headers = getSignatureHeaders();

    //    String payload = getCreateSignaturePayload(processInstanceId);
    Map<String, Object> payload = getCreateSignaturePayload("");
    JSONObject apiRequest = new JSONObject(new Gson().toJson(payload));
    try {
      logger.info("Request of Create Signature API : {}", apiRequest);
      HttpResponse<String> response =
          Unirest.post(ibHelper.getSoapUrl("capri-esign/create-signature-request"))
              .headers(headers)
              .body(apiRequest)
              .asString();
      logger.info("Response From Create Signature API : {}", response.getBody());
      JSONObject responseObject = new JSONObject(response.getBody());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //  public static void main(String[] args) throws IOException {
  //    //    CapriUtilService capriUtilService1 = new CapriUtilService();
  //    //    String encoded =
  //    //        capriUtilService1.encodeBase64String(
  //    //            "/home/kuliza-523/Downloads/statement_anupam_18Jan2022_13_46 (5).pdf");
  //    //    logger.info(encoded);
  //  }
}
