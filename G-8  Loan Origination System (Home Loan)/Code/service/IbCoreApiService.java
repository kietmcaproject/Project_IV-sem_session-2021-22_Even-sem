package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.repository.IbCustomApiFieldsRepository;
import com.kuliza.workbench.util.WorkbenchConstants;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IbCoreApiService {
  private static final Logger logger = LoggerFactory.getLogger(IbCoreApiService.class);

  @Autowired private IbCustomApiFieldsRepository ibCustomApiFieldsRepository;

  public ResponseEntity<Object> customApi(
      String slugName, String apiName, Map<String, Object> request)
      throws UnirestException, JSONException {
    JSONObject object = new JSONObject(new Gson().toJson(request));
    String url = WorkbenchConstants.IB_HOST + slugName + "/" + WorkbenchConstants.IB_HOST_END;
    logger.info(slugName);
    logger.info(WorkbenchConstants.IB_HOST);
    logger.info(WorkbenchConstants.IB_HOST_END);
    logger.info(url);
    logger.info(String.valueOf(object));

    HttpResponse<String> httpResponse =
        Unirest.post(url).header("Content-Type", "application/json").body(object).asString();
    Map<String, Object> response =
        CommonHelperFunctions.getHashMapFromJsonString(httpResponse.getBody());
    logger.info("response : {}", response);
    return new ResponseEntity<Object>(response, HttpStatus.OK);
  }
}
