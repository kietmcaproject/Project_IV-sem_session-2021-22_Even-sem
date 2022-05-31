package com.kuliza.workbench.service;

import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NameSimilarityService {

  private static final Logger logger = LoggerFactory.getLogger(NameSimilarityService.class);
  @Autowired IbHelper ibHelper;

  public DelegateExecution nameMatchApiForAcc(
      DelegateExecution execution, String beneficiaryNameWithBank)
      throws JSONException, UnirestException {

    String name1 = String.valueOf(execution.getVariable("sellerName_hl"));

    JSONObject response = new JSONObject();
    try {
      JSONObject request = new JSONObject();
      request.put("name1", name1);
      request.put("name2", beneficiaryNameWithBank);
      request.put("type", "individual");
      request.put("preset", "s");
      request.put("allowPartialMatch", true);
      logger.info("nameMatch api request : {} ", request);
      HttpResponse<String> httpResponse =
          Unirest.post(ibHelper.getUrl("karza-apis/name-similarity"))
              .header("Content-Type", "application/json")
              .body(request)
              .asString();
      response = new JSONObject(httpResponse.getBody());
      logger.info("nameMatch api response : {}", response);
    } catch (UnirestException e) {
      logger.info("Error while hitting nameMatchApi");
      e.printStackTrace();
    }
    double nameMatchScore = Double.parseDouble(response.get("score").toString());
    execution.setVariable("nameMatchScore_hl", nameMatchScore);
    double nameMatchPercentage = nameMatchScore * 100;
    BigDecimal bdPercent = new BigDecimal(nameMatchPercentage).setScale(2, RoundingMode.HALF_UP);
    nameMatchPercentage = bdPercent.doubleValue();
    logger.info("name Match Percentage : {}", nameMatchPercentage);
    execution.setVariable("nameMatchPercentage", nameMatchPercentage);
    BigDecimal bd = new BigDecimal(nameMatchScore).setScale(2, RoundingMode.HALF_UP);
    nameMatchScore = bd.doubleValue();

    double nameMatchThreshold =
        Double.parseDouble(execution.getVariable("nameMatchThreshold").toString());
    logger.info("nameMatchScore : {}", nameMatchScore);
    logger.info("nameMatchThreshold : {}", nameMatchThreshold);

    if (nameMatchScore >= nameMatchThreshold) {
      execution.setVariable("isNameMatchAboveThreshold", "yes");
    } else {
      execution.setVariable("isNameMatchAboveThreshold", "no");
    }

    return execution;
  }

  public double nameMatchApi(String name1, String name2) throws JSONException, UnirestException {

    JSONObject response = new JSONObject();
    try {
      JSONObject request = new JSONObject();
      request.put("name1", name1);
      request.put("name2", name2);
      request.put("type", "individual");
      request.put("preset", "s");
      request.put("allowPartialMatch", true);
      logger.info("nameMatch api request : {} ", request);
      HttpResponse<String> httpResponse =
          Unirest.post(ibHelper.getUrl("karza-apis/name-similarity"))
              .header("Content-Type", "application/json")
              .body(request)
              .asString();
      response = new JSONObject(httpResponse.getBody());
      logger.info("nameMatch api response : {}", response);
    } catch (UnirestException e) {
      logger.info("Error while hitting nameMatchApi");
      e.printStackTrace();
    }
    double nameMatchScore = Double.parseDouble(response.get("score").toString());
    BigDecimal bdPercent = new BigDecimal(nameMatchScore).setScale(2, RoundingMode.HALF_UP);
    nameMatchScore = bdPercent.doubleValue();
    logger.info("name match score : {}", nameMatchScore);
    return nameMatchScore;
  }
}
