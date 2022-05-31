package com.kuliza.workbench.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.CreateMandate;
import com.kuliza.workbench.model.MandateData;
import com.kuliza.workbench.repository.CreateMandateRepository;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.kuliza.workbench.util.MasterHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("ENachService")
public class ENachService {
  @Autowired IbHelper ibHelper;
  @Autowired RuntimeService runtimeService;
  @Autowired CapriUtilService capriUtilService;
  @Autowired TemplateEngine templateEngine;
  @Autowired JavaMailSender javaMailSender;
  @Autowired CreateMandateRepository createMandateRepository;
  @Autowired MasterHelper masterHelper;
  private static final Logger logger = LoggerFactory.getLogger(ENachService.class);

  @Value("${enach.baseUrl}")
  private String baseUrl;

  public DelegateExecution createMandate(DelegateExecution execution)
      throws UnirestException, JsonProcessingException, JSONException {
    String processInstanceId = execution.getProcessInstanceId();
    String iseligibleForEnach =
        CommonHelperFunctions.getStringValue(execution.getVariable("iseligibleForEnach"));
    if (iseligibleForEnach.equalsIgnoreCase("yes")) {
      logger.info("----- Inside Create Mandate API -----");
      JSONArray enachDetails = new JSONArray();
      JSONObject enachDetailObject = new JSONObject();
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = new Date();
      enachDetailObject.put("eNachTriggeredTime_hl", formatter.format(date));
      enachDetailObject.put("key", enachDetails.length());

      try {
        CreateMandate createMandate = new CreateMandate();
        String payload = getCreateMandatePayload(processInstanceId, createMandate);
        Gson gson = new Gson();
        JSONObject object = new JSONObject(payload);
        object.getJSONObject("mandate_data").remove("id");
        Map<String, Object> variables =
            new Gson()
                .fromJson(
                    String.valueOf(object), new TypeToken<HashMap<String, Object>>() {}.getType());

        List<String> removeVariable =
            Arrays.asList(
                "processInstanceId",
                "reTriggerCount",
                "enachStatus",
                "mandateId",
                "id",
                "notifyCustomer",
                "isRecurring");
        removeVariable.forEach(
            test -> {
              variables.remove(test);
            });
        logger.info("Create Mandate Request Payload :- {}", variables);
        Map<String, String> headers = headers();
        logger.info("headers :- {}", headers);
        logger.info("variables :- {}", new JSONObject(variables));

        HttpResponse<String> response =
            Unirest.post(ibHelper.getSoapUrl("enach/create-mandate"))
                .headers(headers)
                .body(new JSONObject(variables))
                .asString();
        logger.info("Response From Create Mandate API : {}", response.getBody());
        JSONObject responseObject = new JSONObject(response.getBody());

        return parseCreateMandateResponse(
            processInstanceId,
            responseObject,
            enachDetailObject,
            enachDetails,
            execution,
            createMandate);
      } catch (Exception e) {
        e.printStackTrace();
        enachDetailObject.put("eNachErrorMessage_hl", "Internal Server Error");
        enachDetails.put(enachDetailObject);
        runtimeService.setVariable(processInstanceId, "eNachDetails_hl", enachDetails.toString());
        return execution;
      }
    } else {
      runtimeService.setVariable(
          processInstanceId, "eNachRegistrationStatus_hl", "Customer’s bank is not live on eNach");
      Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
      String losId = CommonHelperFunctions.getStringValue(processVariables.get("los_id"));
      String title = "Customer bank is not live on Enach";
      String description =
          "Applicants Bank is not live on enach. So mandate form cannot be created";
      notificationToRm(losId, title, description);
      return execution;
    }
  }

  private Map<String, String> headers() {
    Map<String, String> header = new HashMap<>();
    header.put("Content-Type", "application/json");
    header.put(
        "Authorization",
        "Basic QUk0TEFMVDdSTVEzRFVHRFhSNThLNzg2R1NTOFlWRUg6VFZNTjdEVURBNFpLUUJGVjFRUDNHOUxV RkVURTNNMzQ=");
    return header;
  }

  public String getCreateMandatePayload(String processInstanceId, CreateMandate createMandate)
      throws JsonProcessingException {
    Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
    createMandate.setCustomer_identifier(
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1MobileNumber_hl")));
    createMandate.setAuth_mode("api");
    createMandate.setMandate_type("create");
    createMandate.setCorporate_config_id("TSE21091420421244225Q7ZYVS2WVYRA");
    createMandate.setNotifyCustomer(false);
    createMandate.setMandate_data(getMandateData(processVariables));
    createMandate.setProcessInstanceId(processInstanceId);
    createMandate.setReTriggerCount(0);
    createMandateRepository.save(createMandate);

    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(createMandate);
    runtimeService.setVariable(processInstanceId, "eNachCreatedMandateRequest_hl", payload);
    return payload;
  }

  public MandateData getMandateData(Map<String, Object> processVariables) {
    String emiStartDueDay =
        CommonHelperFunctions.getStringValue(processVariables.get("emiStartDueDay_hl"));
    MandateData mandateData = new MandateData();
    //    mandateData.setMaximum_amount(
    //        CommonHelperFunctions.getStringValue(processVariables.get("finalEmi_hl")));
    mandateData.setMaximum_amount("100");
    mandateData.setInstrument_type("debit");
    mandateData.setFirst_collection_date("2021-05-30");
    mandateData.setIs_recurring(true);
    mandateData.setFrequency("Monthly");
    mandateData.setManagement_category("L001");
    mandateData.setCustomer_name(
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1Name_hl")));
    mandateData.setCustomer_account_number(
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1AccountNumber_hl")));
    mandateData.setDestination_bank_id(
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1Ifsc_hl")));
    mandateData.setDestination_bank_name(
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1BankName_hl")));
    mandateData.setCustomer_account_type("savings");
    return mandateData;
  }

  private DelegateExecution parseCreateMandateResponse(
      String processInstanceId,
      JSONObject response,
      JSONObject enachDetailObject,
      JSONArray enachDetails,
      DelegateExecution execution,
      CreateMandate createMandate)
      throws JSONException, MessagingException, UnirestException {
    runtimeService.setVariable(
        processInstanceId, "eNachCreateMandateResponse_hl", response.toString());
    if (response.has("id")) {
      String mandateId = response.optString("id");
      createMandateRepository.updateMandateId(processInstanceId, mandateId);
      //      enachDetailObject.put("eNachRegistrationStatus_hl", "Enach Registration Initiated");
      //      enachDetailObject.put("eNachMandateId_hl", mandateId);
      //      enachDetails.put(enachDetailObject);

      Map<String, Object> dataToSave = new HashMap<>();
      dataToSave.put("enachMandateId_hl", mandateId);
      //      dataToSave.put("isNachEditable", false);
      dataToSave.put("eNachRegistrationStatus_hl", "Enach Registration Initiated");
      dataToSave.put("eNachDetails_hl", enachDetails.toString());
      runtimeService.setVariables(processInstanceId, dataToSave);
      sendMandateToUser(mandateId, processInstanceId);
      //      createMandateRepository.save(createMandate);
      return execution;
    } else {
      String errorMessage = CommonHelperFunctions.getStringValue(response.opt("message"));
      enachDetailObject.put("eNachErrorMessage_hl", errorMessage);
      enachDetails.put(enachDetailObject);
      runtimeService.setVariable(processInstanceId, "eNachDetails_hl", enachDetails.toString());
      return execution;
    }
  }

  private void sendMandateToUser(String mandateId, String processInstanceId)
      throws MessagingException, UnirestException {
    logger.info("Sending a communication to user ...");
    Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
    String losId = CommonHelperFunctions.getStringValue(processVariables.get("los_id"));
    String title = "Mandate sent to user";
    String mobileNumber =
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1MobileNumber_hl"));
    String emailId =
        CommonHelperFunctions.getStringValue(processVariables.get("applicant1Email_hl"));
    String description = "Mandate has been sent to user for accepting";
    String url = getEMandateUrl(mandateId, mobileNumber);
    notificationToRm(losId, title, description);
    runtimeService.setVariable(processInstanceId, "eNachMandateUrl_hl", url);

    String message =
        "Dear Customer with reference to Home Loan Application No. "
            + losId
            + "with CGHFL please click on following link for ENach Registration. "
            + url;
    Map<String, Object> payloadForSms = new HashMap<>();
    payloadForSms.put("url#dest", mobileNumber);
    payloadForSms.put("url#msg", message);
    logger.info("payloadForSms: " + payloadForSms);
    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("prpsms/message-sending"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(payloadForSms))
            .asString();
    logger.info("Response: " + response.getBody());

    //    String message = "";
    //    message = String.format(message, url);
    //    String[] to = new String[] {emailId};
    //    String[] messageRecievers = new String[] {mobileNumber};

    //    HashMap<String, Object> mailVar = new HashMap<>();
    //    mailVar.put("enachLink", url);
    //    Context context = new Context();
    //    context.setVariables(mailVar);
    //    MimeMessage mail = javaMailSender.createMimeMessage();
    //    String body = templateEngine.process("enach_email_template", context);
    //    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    //
    //    helper.setTo(emailId);
    //    helper.setSubject("Enach Link");
    //    helper.setText(body, true);
    //    javaMailSender.send(mail);
    //    System.out.println("Mail sent successfully........");
  }

  public void notificationToRm(String losId, String title, String description)
      throws UnirestException {
    Map<String, Object> notificationRequest = new HashMap<>();
    notificationRequest.put("title", title);
    notificationRequest.put("description", description);
    notificationRequest.put("lan_id", losId);
    notificationRequest.put("webpage_link", "");
    notificationRequest.put("raised_by", "");
    logger.info("notificationRequest:- {}", notificationRequest);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("notification-to-rm/notification-api"))
            .header("Content-Type", "application/json")
            .body(new JSONObject(notificationRequest))
            .asString();
    logger.info("Response of notification:- {}", response.getBody());
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

  public void callBackResponse(Map<String, Object> request)
      throws MessagingException, UnirestException, JSONException {
    JSONObject object = new JSONObject(new Gson().toJson(request));

    JSONObject payload = (JSONObject) object.get("payload");
    String currentStatus =
        CommonHelperFunctions.getStringValue(
            payload.getJSONObject("api_mandate").get("current_status"));
    String mandateId =
        CommonHelperFunctions.getStringValue(payload.getJSONObject("api_mandate").get("id"));
    CreateMandate createMandate = createMandateRepository.findByMandateId(mandateId);
    logger.info("createMandate :- {}", createMandate.getProcessInstanceId());
    String processInstanceId = createMandate.getProcessInstanceId();
    int reTriggerCount = createMandate.getReTriggerCount();
    if (reTriggerCount < 2) {
      if (currentStatus.equalsIgnoreCase("success")) {
        runtimeService.setVariable(
            processInstanceId, "eNachRegistrationStatus_hl", "Enach Authentication Successful");
      } else if (currentStatus.equalsIgnoreCase("register_success")) {
        runtimeService.setVariable(
            processInstanceId, "eNachRegistrationStatus_hl", "Enach Mandate Successful");
        runtimeService.setVariable(processInstanceId, "isPhysicalNachRequired", "no");
      } else {
        runtimeService.setVariable(
            processInstanceId, "eNachRegistrationStatus_hl", "Enach Registration Fail");
        JSONObject masterObject = masterHelper.getRequestFromMaster("capri-enach-banks");
        JSONArray masterData = masterObject.getJSONArray("data");
        String applicantBankName =
            CommonHelperFunctions.getStringValue(
                runtimeService.getVariable(processInstanceId, "bankName_hl"));
        for (int i = 0; i < masterData.length(); i++) {
          JSONObject data = masterData.getJSONObject(i);
          if (applicantBankName.equalsIgnoreCase(data.get("BANK NAME").toString())) {
            String mobileNumber =
                CommonHelperFunctions.getStringValue(
                    runtimeService.getVariable(processInstanceId, "applicant1MobileNumber_hl"));
            String emailId =
                CommonHelperFunctions.getStringValue(
                    runtimeService.getVariable(processInstanceId, "applicant1Email_hl"));
            String url = getEMandateUrl(mandateId, mobileNumber);
            logger.info("url..........:-{}", url);
            runtimeService.setVariable(processInstanceId, "eNachMandateUrl_hl", url);
            sendMandateToUser(mandateId, processInstanceId);
            reTriggerCount++;
            createMandateRepository.updateReTriggerCount(processInstanceId, reTriggerCount);
            break;
          }
          if (i == masterData.length() - 1) {
            String losId =
                CommonHelperFunctions.getStringValue(
                    runtimeService.getVariable(processInstanceId, "los_id"));
            String title = "Applicants Bank";
            String description = "Applicants Bank is not listed in list of mandatory banks";
            notificationToRm(losId, title, description);
            runtimeService.setVariable(processInstanceId, "isPhysicalNachRequired", "yes");
          }
        }
      }
    } else {
      runtimeService.setVariable(processInstanceId, "isPhysicalNachRequired", "yes");
    }
  }

  //  public void mandateCallBackResponse(Map<String, Object> request) throws JSONException {
  //    JSONObject payload = (JSONObject) request.get("payload");
  //    String currentStatus =
  // CommonHelperFunctions.getStringValue(payload.getJSONObject("api_mandate").get("current_status"));
  //    String mandateId =
  // CommonHelperFunctions.getStringValue(payload.getJSONObject("api_mandate").get("id"));
  //    if (currentStatus.equalsIgnoreCase("register_success")) {
  //      runtimeService.setVariable("eNachRegistrationStatus_hl", "Enach Mandate Successful");
  //    }

}
