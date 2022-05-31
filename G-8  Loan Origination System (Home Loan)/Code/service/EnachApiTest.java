package com.kuliza.workbench.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.flowable.engine.RuntimeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EnachApiTest {
  @Autowired IbHelper ibHelper;
  @Autowired RuntimeService runtimeService;
  @Autowired CapriUtilService capriUtilService;
  @Autowired TemplateEngine templateEngine;
  @Autowired JavaMailSender javaMailSender;
  private static final Logger logger = LoggerFactory.getLogger(ENachService.class);

  @Value("${enach.baseUrl}")
  private String baseUrl;

  public String testEnach(Map<String, Object> request)
      throws UnirestException, JsonProcessingException, JSONException {
    String iseligibleForEnach = "yes";
    if (iseligibleForEnach.equalsIgnoreCase("yes")) {
      logger.info("----- Inside Create Mandate API -----");

      try {
        String payload = getCreateMandatePayload(request);
        logger.info("Create Mandate Request Payload :- {}", payload);
        Map<String, String> headers = getSignatureHeaders();
        HttpResponse<String> response =
            Unirest.post(ibHelper.getSoapUrl("enach/create-mandate"))
                .headers(headers)
                .body(new JSONObject(payload))
                .asString();
        logger.info("Response From Create Mandate API : {}", response.getBody());
        JSONObject responseObject = new JSONObject(response.getBody());

        return parseCreateMandateResponse(responseObject);
      } catch (Exception e) {
      }
    }

    return "";
  }

  public Map<String, String> getSignatureHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put(
        "Authorization",
        "Basic QUk0TEFMVDdSTVEzRFVHRFhSNThLNzg2R1NTOFlWRUg6VFZNTjdEVURBNFpLUUJGVjFRUDNHOUxVRkVURTNNMzQ=");
    return headers;
  }

  public String getCreateMandatePayload(Map<String, Object> request)
      throws JsonProcessingException, JSONException {
    JSONObject payload = new JSONObject();

    payload.put("customer_identifier", request.get("customer_identifier").toString());
    payload.put("auth_mode", request.get("auth_mode").toString());
    payload.put("mandate_type", request.get("mandate_type").toString());
    payload.put("corporate_config_id", request.get("corporate_config_id").toString());
    JSONObject mandate_data = new JSONObject();
    mandate_data.put("maximum_amount", "100");
    mandate_data.put("instrument_type", "debit");
    mandate_data.put("first_collection_date", "2020-01-30");
    mandate_data.put("is_recurring", "true");
    mandate_data.put("frequency", "Monthly");
    mandate_data.put("management_category", "L001");
    mandate_data.put("customer_name", "Gopal Contractor");
    mandate_data.put("customer_account_number", "202011001236657");
    mandate_data.put("destination_bank_id", "FDRL0001542");
    mandate_data.put("destination_bank_name", "Fedral Bank");
    mandate_data.put("customer_account_type", "savings");
    mandate_data.put("customer_ref_number", "");
    mandate_data.put("scheme_ref_number", "");
    payload.put("mandate_data", mandate_data);

    String requestBody = payload.toString();

    return requestBody;
  }

  private String parseCreateMandateResponse(JSONObject response)
      throws JSONException, MessagingException {
    if (response.has("id")) {
      String mandateId = response.optString("id");
      Map<String, Object> dataToSave = new HashMap<>();
      dataToSave.put("enachMandateId_hl", mandateId);
      //      dataToSave.put("isNachEditable", false);
      dataToSave.put("eNachRegistrationStatus_hl", "Enach Registration Initiated");
      sendMandateToUser(mandateId);
    }
    return "";
  }

  private void sendMandateToUser(String mandateId) throws MessagingException {
    logger.info("Sending a communication to user ...");
    String mobileNumber = "9722818617";
    String emailId = "kshitiz.pandey@kuliza.com";
    String url = getEMandateUrl(mandateId, mobileNumber);
    //    String message = "";
    //    message = String.format(message, url);
    //    String[] to = new String[] {emailId};
    //    String[] messageRecievers = new String[] {mobileNumber};

    HashMap<String, Object> mailVar = new HashMap<>();
    mailVar.put("link", url);
    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("enach_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(emailId);
    helper.setSubject("Enach Link");
    helper.setText(body, true);
    javaMailSender.send(mail);
    System.out.println("Mail sent successfully........");
  }

  public String getEMandateUrl(String mandateId, String mobileNumber) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(baseUrl);
    urlBuilder.append(mandateId);
    urlBuilder.append("/");
    String txnId = capriUtilService.getRandomAlphanumericKey(6);
    urlBuilder.append(txnId).append("/");
    urlBuilder.append(mobileNumber);
    urlBuilder.append("?redirect_url=https://los.capriglobal.dev/workbench/enachResponse");
    String url = urlBuilder.toString();
    logger.info("eMandate url :: " + url);
    return url;
  }

  //  public static void main(String[] args) {
  //    String url = "https://mail.google.com/mail/u/0/?tab=wm#inbox";
  //    String msg = "Hello";
  //    msg = String.format(msg, url);
  //    System.out.println(msg);
  //  }
}
