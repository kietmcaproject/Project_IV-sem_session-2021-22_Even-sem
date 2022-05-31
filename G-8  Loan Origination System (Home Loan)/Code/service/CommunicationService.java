package com.kuliza.workbench.service;

import com.kuliza.workbench.model.SMSTemplate;
import com.kuliza.workbench.repository.SMSTemplateRepository;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService {
  @Autowired SMSTemplateRepository smsTemplateRepository;
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired IbHelper ibHelper;

  private static final Logger logger = LoggerFactory.getLogger(CommunicationService.class);

  public DelegatePlanItemInstance smsTemplate(
      DelegatePlanItemInstance planItemInstance, String smsNumber, String MobileNumber)
      throws UnirestException {

    SMSTemplate smsTemplate = smsTemplateRepository.findBySmsIndex(smsNumber);
    String message = smsTemplate.getMessage();

    Map<String, Object> fields = new HashMap<>();

    fields.put("url#dest", MobileNumber);
    fields.put("url#msg", message);

    logger.info("Fields: " + fields);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("prpsms/message-sending"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(fields))
            .asString();

    logger.info("Response: " + response.getBody());

    cmmnRuntimeService.setVariable(
        planItemInstance.getCaseInstanceId(), "smsApiResponse", response.toString());
    return planItemInstance;
  }

  public Map<String, Object> setData(Map<String, Object> request) {
    SMSTemplate smsTemplate = new SMSTemplate();
    smsTemplate.setSmsIndex(request.get("index").toString());
    smsTemplate.setMessage(request.get("message").toString());
    smsTemplate = smsTemplateRepository.save(smsTemplate);
    Map<String, Object> smsTemplates = new HashMap<>();
    smsTemplates.put("data", smsTemplate);
    return smsTemplates;
  }
}
