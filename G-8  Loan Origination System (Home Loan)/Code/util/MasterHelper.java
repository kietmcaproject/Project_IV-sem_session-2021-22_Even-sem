package com.kuliza.workbench.util;

import com.kuliza.lending.authorization.service.GetAdminAccessTokenService;
import com.kuliza.lending.engine_common.configs.MasterConfig;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.util.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterHelper {
  @Autowired MasterConfig masterConfig;

  @Autowired GetAdminAccessTokenService getAdminAccessTokenService;
  private static final Logger logger = LoggerFactory.getLogger(MasterHelper.class);

  public String getUrl(String slug) {
    return masterConfig
        .getProtocol()
        .concat("://")
        .concat(masterConfig.getHost())
        //        .concat(masterConfig.getSubURL())
        .concat("/api/masters/")
        .concat(slug);
  }

  public JSONObject postRequestFromMaster(String slug, String value, String key) {
    String requestPath = getUrl(slug);
    logger.info("Request Path for master " + requestPath);
    Map<String, Object> requestBody = requestBodyFormaster(value, key);
    JSONObject jsonObject = null;
    try {
      Map<String, String> headers = getMasterHeaders();
      HttpResponse<JsonNode> response =
          Unirest.post(requestPath).headers(headers).body(new JSONObject(requestBody)).asJson();
      logger.info("Response body {} ", response.getBody().toString());
      jsonObject = new JSONObject(response.getBody().toString());
    } catch (Exception e) {
      logger.info(" ERROR in hitting postRequestFromMaster ");
      e.printStackTrace();
    }
    return jsonObject;
  }

  public String getTokenForMaster() {
    String token = "";
    try {
      token = getAdminAccessTokenService.getAdminAccessToken();
    } catch (Exception e) {
      logger.info(" ERROR in getting the token from master ");
      e.printStackTrace();
    }
    return token;
  }

  public Map<String, Object> requestBodyFormaster(String value, String key) {
    List<Map<String, Object>> list = new ArrayList<>();
    Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
    requestBody.put("isCaseSensitive", "false");
    requestBody.put("key", key);
    requestBody.put("value", value);
    requestBody.put("comparator", "=");
    requestBody.put("operator", ">");
    list.add(0, requestBody);
    Map<String, Object> finalRequestBody = new LinkedHashMap<String, Object>();
    finalRequestBody.put("keyList", list);
    logger.info("Request body for requestBodyForSsic {} ", finalRequestBody);
    return finalRequestBody;
  }

  public JSONObject getRequestFromMaster(String slug) {
    String requestPath = getUrl(slug);
    logger.info("Request Path for master " + requestPath);
    JSONObject jsonObject = null;
    try {
      Map<String, String> headers = getMasterHeaders();
      HttpResponse<JsonNode> response = Unirest.get(requestPath).headers(headers).asJson();
      logger.info("Response body {} ", response.getBody().toString());
      jsonObject = new JSONObject(response.getBody().toString());
    } catch (Exception e) {
      logger.info("ERROR in hitting getRequestFromMaster");
      e.printStackTrace();
    }
    return jsonObject;
  }

  private Map<String, String> getMasterHeaders() {
    String token = getTokenForMaster();
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + token);
    headers.put("Content-Type", "application/json");
    logger.info(" Master Token {} ", token);
    return headers;
  }
}
