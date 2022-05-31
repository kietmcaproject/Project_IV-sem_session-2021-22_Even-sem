package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.*;
import com.kuliza.workbench.repository.*;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmpVerificationApiService {

  @Autowired EPFOApiDetailsRepository epfoApiDetailsRepository;

  @Autowired EmailAuthApiDetailsRepository emailAuthApiDetailsRepository;

  @Autowired KScanApiDetailsRepository kScanApiDetailsRepository;

  @Autowired Form26asApiDetailsRepository form26asApiDetailsRepository;

  @Autowired EmpVerifDetailsRepository empVerifDetailsRepository;

  @Autowired CustomerEmailRepository customerEmailRepository;

  @Autowired IbHelper ibHelper;

  @Autowired CapriUtilService capriUtilService;
  private static final Logger logger = LoggerFactory.getLogger(EmpVerificationApiService.class);

  @Value("${link.to.open.process}")
  private String linkToOpenProcess;

  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;

  @Autowired JavaMailSender javaMailSender;

  @Autowired TemplateEngine templateEngine;

  @Autowired RuntimeService runtimeService;

  public ApiResponse receiveEmpDetailsFromPragatiApp(Map<String, Object> request)
      throws JSONException, MessagingException {
    String applicationId = CommonHelperFunctions.getStringValue(request.get("application_id"));
    if (applicationId.equals("")) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Application id is null");
      response.put("error", "Y");
      return new ApiResponse(HttpStatus.BAD_REQUEST, "Bad request", response);
    }

    JSONObject object = new JSONObject(new Gson().toJson(request));

    //    PragatiAppDetails pragatiAppDetails =
    //        pragatiAppDetailsRepository.findByApplicationId(Integer.parseInt(applicationId));
    //    String processInstanceId = pragatiAppDetails.getProcessInstanceId();
    //    String supervisorMobileNo = object.getString("supervisor_mobile");
    //    runtimeService.setVariable(processInstanceId, "applicant1PhoneNumber_hl",
    // supervisorMobileNo);

    String is26asAvailable = String.valueOf(object.get("26as_available"));

    if (is26asAvailable.equalsIgnoreCase("Yes")) {
      try {
        String pan = String.valueOf(object.get("applicant_pan"));
        String officialEmailId = String.valueOf(object.get("official_email_id"));
        Map<String, Object> form26asPayload =
            createForm26AsCheckPayload(applicationId, pan, officialEmailId);
        form26asCheck(form26asPayload); // call form26As api
      } catch (UnirestException e) {
        logger.info("Error hitting form26asCheck");
        e.printStackTrace();
      }

      Form26asApiDetails form26asApiDetails =
          form26asApiDetailsRepository.findByApplicationId(Integer.parseInt(applicationId));
      String requestId = form26asApiDetails.getRequestId();
    }

    JSONObject kscanResponse = new JSONObject();
    JSONObject epfoResponse = new JSONObject();
    if (is26asAvailable.equalsIgnoreCase("No")) {
      // KScan - employerSearch api
      String employerName = String.valueOf(object.get("employer_name"));
      try {
        String kid = employerSearchApi(employerName, Integer.parseInt(applicationId));
        if (kid != null) {
          // K Scan -> epf profile api
          Map<String, Object> kscanRequest = new HashMap<>();
          Map<String, Object> requestBody = new HashMap<>();
          requestBody.put("id", kid);
          kscanRequest.put("applicationId", applicationId);
          kscanRequest.put("request", requestBody);

          ResponseEntity<Object> response = kscanEpfProfile(kscanRequest);
          kscanResponse = new JSONObject(response.getBody().toString());
        }
      } catch (UnirestException e) {
        e.printStackTrace();
      }
      JSONArray kscanResult = kscanResponse.getJSONArray("result");
      String ownershipType = String.valueOf(kscanResult.getJSONObject(0).get("ownershipType"));
      if (!ownershipType.equals("null")
          && (ownershipType.equalsIgnoreCase("Central Public Sector Undertaking")
              || ownershipType.equalsIgnoreCase("Government"))) {
        // TODO: ‘Employment FI is waived’ message displayed on the interface
      }

      String applicantName = String.valueOf(object.get("applicant_name"));

      try {
        Map<String, Object> epfoRequest = new HashMap<>();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("employerName", employerName);
        requestBody.put("employeeName", applicantName);
        requestBody.put("uans", "");
        requestBody.put("entityId", "");
        requestBody.put("mobile", "");
        requestBody.put("emailId", "");
        requestBody.put("pdf", true);
        epfoRequest.put("applicationId", applicationId);
        epfoRequest.put("request", requestBody);

        ResponseEntity<Object> response = epfoCheck(epfoRequest);
        epfoResponse = new JSONObject(response.getBody().toString());

      } catch (UnirestException e) {
        e.printStackTrace();
      }

      int epfoStatusCode = Integer.parseInt(epfoResponse.get("status-code").toString());

      if (epfoStatusCode == 101) {
        boolean isNameExact = false;
        boolean isNameUnique = false;
        JSONObject nameLookup = epfoResponse.getJSONObject("result").getJSONObject("nameLookup");
        isNameExact = Boolean.parseBoolean(nameLookup.get("isNameExact").toString());
        isNameUnique = Boolean.parseBoolean(nameLookup.get("isNameUnique").toString());
        JSONArray epfHistory = nameLookup.getJSONArray("epfHistory");
        List<String> wageMonths = new ArrayList<>();
        for (int i = 0; i < epfHistory.length(); i++) {
          wageMonths.add(epfHistory.getJSONObject(i).get("wageMonth").toString());
        }
        JSONArray matches = nameLookup.getJSONArray("matches");

        boolean pfContributionInLast45Days = false;
        if (matches.length() != 0) {
          JSONObject matchesEpfHistory = matches.getJSONObject(0).getJSONObject("epfHistory");
          for (String wageMonth : wageMonths) {
            boolean epfOfMonth = Boolean.parseBoolean(matchesEpfHistory.get(wageMonth).toString());
            pfContributionInLast45Days = pfContributionInLast45Days || epfOfMonth;
          }
        }
        logger.info("isNameExact : {}", isNameExact);
        logger.info("isNameUnique : {}", isNameUnique);
        logger.info("pfContributionInLast45Days : {}", pfContributionInLast45Days);

        if (isNameExact && isNameUnique && pfContributionInLast45Days) {
          // TODO: 'Employment FI done’ message will be shown on the interface, no other employment
          // verification protocol to be initiated
          epfoApiDetailsRepository.updateEmploymentFiStatus(
              Integer.parseInt(applicationId), "Employment FI done");

          logger.info("******* epfo check completed ******");
        } else {
          // ‘Employment FI could not be completed’ message will be shown on the interface. Next in
          // line protocols, as defined below will be triggered
          epfoApiDetailsRepository.updateEmploymentFiStatus(
              Integer.parseInt(applicationId), "Employment FI could not be completed");
          // call form26AS api

        }
      }

      EPFOApiDetails epfoApiDetails =
          epfoApiDetailsRepository.findByApplicationId(Integer.parseInt(applicationId));
      String fiStatus = epfoApiDetails.getEmploymentFiStatus();
      if (!fiStatus.equalsIgnoreCase("Employment FI done")) {
        try {
          boolean isApplicantEmailPresent;
          boolean isEmailValid = false;
          String officialEmailId = String.valueOf(object.get("official_email_id"));

          if (officialEmailId == null || officialEmailId.equals("")) {
            isApplicantEmailPresent = false;
          } else {
            isApplicantEmailPresent = true;
            Map<String, Object> emailApiRequest = new HashMap<>();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", officialEmailId);
            emailApiRequest.put("applicationId", applicationId);
            emailApiRequest.put("request", requestBody);

            isEmailValid = emailAuth(emailApiRequest, isApplicantEmailPresent);
          }

          if (isEmailValid) {
            Map<String, Object> emailLinkApiPayload = new HashMap<>();
            emailLinkApiPayload =
                createEmailLinkApiPayload(applicationId, officialEmailId, applicantName);
            try {
              ResponseEntity<Object> response = emailAuthLinkBased(emailLinkApiPayload);
              logger.info("******* email authentication link based completed *******");
            } catch (UnirestException e) {
              logger.info("Error in email authentication link based");
              e.printStackTrace();
            }
          } else {
            String supervisorEmail = object.getString("supervisor_email");
            boolean isSupervisorEmailValid = false;

            Map<String, Object> emailApiPayload = new HashMap<>();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", supervisorEmail);
            emailApiPayload.put("applicationId", applicationId);
            emailApiPayload.put("request", requestBody);

            isSupervisorEmailValid = emailAuth(emailApiPayload, isApplicantEmailPresent);

            if (isSupervisorEmailValid) {
              sendEmailToSupervisor(object, applicantName, applicationId, supervisorEmail);
            }
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
    return new ApiResponse(200, "SUCCESS", "Ok");
  }

  public void sendEmailToSupervisor(
      JSONObject object, String applicantName, String applicationId, String supervisorEmail)
      throws JSONException, MessagingException {
    // mail sending with java mail sender
    HashMap<String, Object> mailVar = new HashMap<>();
    String supervisorName = object.getString("supervisor_name");
    PragatiAppDetails pragatiAppDetails =
        pragatiAppDetailsRepository.findByApplicationId(Integer.parseInt(applicationId));
    String kulizaAppId = pragatiAppDetails.getKulizaAppId();
    logger.info("kulizaAppId : {}", kulizaAppId);
    logger.info("linkToOpenProcess : {}", linkToOpenProcess);
    String employmentDetailsFormLink =
        linkToOpenProcess
            .concat("EmailBasedVerificationProcess")
            .concat("&applicationId=")
            .concat(kulizaAppId);

    mailVar.put("supervisorName", supervisorName);
    mailVar.put("applicantNameWithSalutation", applicantName);
    mailVar.put("employmentDetailsFormLink", employmentDetailsFormLink);

    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("supervisor_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);

    helper.setTo(supervisorEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Regarding employment details for an employee");
    helper.setText(body, true);

    javaMailSender.send(mail);
    logger.info("Mail sent successfully for CAPTURING APPLICANT EMPLOYMENT DETAILS ........");
  }

  public String employerSearchApi(String employerName, int applicationId) throws JSONException {
    JSONObject apiRequest = new JSONObject();
    apiRequest.put("name", employerName);
    apiRequest.put("otherName", true);
    apiRequest.put("nameMatch", true);
    apiRequest.put("nameMatchThreshold", true);
    apiRequest.put("consent", "Y");

    JSONObject response = null;
    try {
      String url = ibHelper.getUrl("karza-apis/employer-search");
      HttpResponse<JsonNode> httpResponse =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asJson();
      logger.info("--- EmployerSearchApi request : {} ", apiRequest);
      logger.info("--- EmployerSearchApi response : {} ", httpResponse.getBody());
      response = new JSONObject(httpResponse.getBody().toString());
    } catch (UnirestException e) {
      logger.info("Error in hitting employerSearch");
      e.printStackTrace();
    }

    JSONArray entities = response.getJSONArray("result");
    logger.info("kscan result entities : {}", entities);
    KScanApiDetails kScanApiDetails = new KScanApiDetails();
    kScanApiDetails.setEmpSearchRequest(apiRequest.toString());
    kScanApiDetails.setEmpSearchResponse(response.toString());
    kScanApiDetails.setApplicationId(applicationId);

    boolean isUniqueMatch = false;
    boolean isNameFound = false;

    if (entities.length() == 0) {
      kScanApiDetails.setNameFound(false);
      isNameFound = false;
    } else {
      kScanApiDetails.setNameFound(true);
      isNameFound = true;
    }

    String kid = null;
    if (entities.length() == 1) {
      kid = String.valueOf(entities.getJSONObject(0).get("kid"));
      String matchScore = entities.getJSONObject(0).get("score").toString();
      kScanApiDetails.setMatchScore(matchScore);
      logger.info("setting unique match as true");
      kScanApiDetails.setUniqueMatch(true);
      isUniqueMatch = true;
      isNameFound = true;
    }
    logger.info("------ kid : {} ", kid);
    logger.info("entities length : {}", entities.length());

    if (kid == null) {
      logger.info("setting unique match as false");
      kScanApiDetails.setUniqueMatch(false);
      isUniqueMatch = false;
    }

    KScanApiDetails applicationIdExist =
        kScanApiDetailsRepository.findByApplicationId(applicationId);
    if (applicationIdExist != null) {
      kScanApiDetailsRepository.updateEmpSearchRequest(applicationId, apiRequest.toString());
      kScanApiDetailsRepository.updateEmpSearchResponse(applicationId, response.toString());
      kScanApiDetailsRepository.updateIsUniqueMatch(applicationId, isUniqueMatch);
      kScanApiDetailsRepository.updateIsNameFound(applicationId, isNameFound);
    } else {
      kScanApiDetailsRepository.save(kScanApiDetails);
    }
    return kid;
  }

  public ResponseEntity<Object> epfoCheck(Map<String, Object> request)
      throws JSONException, UnirestException {
    JSONObject requestJsonObj = new JSONObject(new Gson().toJson(request));
    JSONObject apiRequest = requestJsonObj.getJSONObject("request");

    String applicationIdStr = requestJsonObj.get("applicationId").toString();
    if (applicationIdStr.equals("")) {
      JSONObject response = new JSONObject();
      response.put("message", "Application id is null");
      response.put("error", "Y");
      response.put("status", HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
    }
    int applicationId = Integer.parseInt(applicationIdStr);
    logger.info("epfoCheck request : {} ", request.get("request"));
    EPFOApiDetails epfoApiDetails = new EPFOApiDetails();
    epfoApiDetails.setApplicationId(applicationId);
    epfoApiDetails.setRequest(requestJsonObj.toString());

    HttpResponse<String> response = null;
    try {
      String url = ibHelper.getUrl("karza-apis/employment-verification-advanced-");
      response =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asString();
      logger.info("----- epfoCheck response : {}", response.getBody());
    } catch (UnirestException e) {
      logger.info("Error in hitting epfoCheck");
      e.printStackTrace();
    }

    epfoApiDetails.setResponse(response.getBody());

    EPFOApiDetails applicationIdExist = epfoApiDetailsRepository.findByApplicationId(applicationId);
    if (applicationIdExist != null) {
      epfoApiDetailsRepository.updateRequest(applicationId, requestJsonObj.toString());
      epfoApiDetailsRepository.updateResponse(applicationId, response.getBody());
    } else {
      epfoApiDetailsRepository.save(epfoApiDetails);
    }
    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
  }

  public boolean emailAuth(Map<String, Object> request, boolean isApplicantEmailPresent)
      throws JSONException {
    JSONObject requestJsonObj = new JSONObject(new Gson().toJson(request));
    JSONObject apiRequest = requestJsonObj.getJSONObject("request");
    String applicationIdStr = requestJsonObj.get("applicationId").toString();
    int applicationId = Integer.parseInt(applicationIdStr);

    JSONObject response = new JSONObject();
    try {
      logger.info("------ emailAuth request : {}", apiRequest);
      String url = ibHelper.getUrl("karza-apis/email-authentication");
      HttpResponse<String> emailAuthResponse =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asString();

      response = new JSONObject(emailAuthResponse.getBody());
      logger.info("------ emailAuth response : {}", emailAuthResponse.getBody());
    } catch (UnirestException e) {
      logger.info("Error in hitting emailAuth");
      e.printStackTrace();
    }

    CustomerEmail customerEmail = new CustomerEmail();
    customerEmail.setApplicationId(applicationId);
    customerEmail.setEmail(apiRequest.get("email").toString());
    customerEmail.setVerification(response.get("result").toString());
    customerEmail.setApplicantEmailPresent(isApplicantEmailPresent);

    CustomerEmail isCustomerEmailExist = customerEmailRepository.findByApplicationId(applicationId);
    if (isCustomerEmailExist != null) {
      customerEmailRepository.updateEmail(applicationId, apiRequest.get("email").toString());
      customerEmailRepository.updateVerification(applicationId, response.get("result").toString());
      customerEmailRepository.updateIsApplicantEmailPresent(applicationId, isApplicantEmailPresent);
    } else {
      customerEmailRepository.save(customerEmail);
    }

    return Boolean.parseBoolean(response.get("result").toString());
  }

  public ResponseEntity<Object> emailAuthLinkBased(Map<String, Object> request)
      throws JSONException, UnirestException {

    JSONObject requestJsonObj = new JSONObject(new Gson().toJson(request));
    JSONObject apiRequest = requestJsonObj.getJSONObject("request");

    String applicationIdStr = requestJsonObj.get("applicationId").toString();
    if (applicationIdStr.equals("")) {
      JSONObject response = new JSONObject();
      response.put("message", "Application id is null");
      response.put("error", "Y");
      response.put("status", HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
    }

    int applicationId = Integer.parseInt(applicationIdStr);
    EmailAuthApiDetails emailAuthApiDetails = new EmailAuthApiDetails();
    emailAuthApiDetails.setApplicationId(applicationId);
    emailAuthApiDetails.setRequest(requestJsonObj.toString());

    HttpResponse<String> response = null;
    try {
      logger.info("------ emailAuthLinkBased request : {}", apiRequest);
      String url = ibHelper.getUrl("karza-apis/email-authentication-link-based");
      response =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asString();
      logger.info("------- emailAuthLinkBased response : {}", response.getBody());
    } catch (UnirestException e) {
      logger.info("Error in hitting emailAuthLinkBased");
      e.printStackTrace();
    }

    emailAuthApiDetails.setResponse(response.getBody());

    JSONObject jsonObject = new JSONObject(response.getBody());
    String requestId = jsonObject.get("requestId").toString();

    customerEmailRepository.updateReqId(applicationId, requestId);

    EmailAuthApiDetails applicationIdExist =
        emailAuthApiDetailsRepository.findByApplicationId(applicationId);
    if (applicationIdExist != null) {
      emailAuthApiDetailsRepository.updateRequest(applicationId, requestJsonObj.toString());
      emailAuthApiDetailsRepository.updateResponse(applicationId, response.getBody());
    } else {
      emailAuthApiDetailsRepository.save(emailAuthApiDetails);
    }
    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
  }

  public ResponseEntity<Object> kscanEpfProfile(Map<String, Object> request)
      throws JSONException, UnirestException {
    JSONObject requestJsonObject = new JSONObject(new Gson().toJson(request));
    JSONObject apiRequest = requestJsonObject.getJSONObject("request");

    String applicationIdStr = requestJsonObject.get("applicationId").toString();
    if (applicationIdStr.equals("")) {
      JSONObject response = new JSONObject();
      response.put("message", "Application id is null");
      response.put("error", "Y");
      response.put("status", HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
    }
    int applicationId = Integer.parseInt(applicationIdStr);
    KScanApiDetails kScanApiDetails = new KScanApiDetails();
    kScanApiDetails.setApplicationId(applicationId);
    kScanApiDetails.setRequest(requestJsonObject.toString());

    HttpResponse<String> response = null;
    try {
      String url = ibHelper.getUrl("karza-apis/epf-profile-api");
      logger.info("---- kscanEpfProfile request : {}", apiRequest);

      response =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asString();

      logger.info("---  kscanEpfProfile response : {}", response.getBody());
    } catch (UnirestException e) {
      logger.info("Error in hitting kscanEpfProfile");
      e.printStackTrace();
    }

    kScanApiDetails.setResponse(response.getBody());
    KScanApiDetails applicationIdExist =
        kScanApiDetailsRepository.findByApplicationId(applicationId);
    if (applicationIdExist != null) {
      kScanApiDetailsRepository.updateRequest(applicationId, requestJsonObject.toString());
      kScanApiDetailsRepository.updateResponse(applicationId, response.getBody());
    } else {
      kScanApiDetailsRepository.save(kScanApiDetails);
    }
    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
  }

  public ResponseEntity<Object> form26asCheck(Map<String, Object> request)
      throws JSONException, UnirestException {
    JSONObject requestJsonObject = new JSONObject(new Gson().toJson(request));
    JSONObject apiRequest = requestJsonObject.getJSONObject("request");

    String applicationIdStr = requestJsonObject.get("applicationId").toString();
    if (applicationIdStr.equals("")) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Application id is null");
      response.put("error", "Y");
      response.put("status", HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    int applicationId = Integer.parseInt(applicationIdStr);

    Form26asApiDetails form26asApiDetails = new Form26asApiDetails();
    form26asApiDetails.setApplicationId(applicationId);
    form26asApiDetails.setRequest(requestJsonObject.toString());

    HttpResponse<String> response = null;
    try {

      String url = ibHelper.getUrl("karza-apis/gst---itr-credlink-generation--send--version-v2");
      logger.info("Form26As request : {}", apiRequest);
      response =
          Unirest.post(url).header("Content-Type", "application/json").body(apiRequest).asString();
      logger.info("Form26As response : {}", response.getBody());
    } catch (UnirestException e) {
      logger.info("Error in hitting form26asCheck");
      e.printStackTrace();
    }
    form26asApiDetails.setResponse(response.getBody());
    JSONObject jsonObject = new JSONObject(response.getBody());
    form26asApiDetails.setRequestId(String.valueOf(jsonObject.get("requestId")));

    Form26asApiDetails applicationIdExist =
        form26asApiDetailsRepository.findByApplicationId(applicationId);

    if (applicationIdExist != null) {
      form26asApiDetailsRepository.updateRequest(applicationId, requestJsonObject.toString());
      form26asApiDetailsRepository.updateResponse(applicationId, response.getBody());
    } else {
      form26asApiDetailsRepository.save(form26asApiDetails);
    }
    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
  }

  public void processForm26AsResponse(JSONObject callBackResponse) throws JSONException {
    JSONObject result = callBackResponse.getJSONObject("result");
    String requestId = callBackResponse.getString("requestId");
    String employerName = result.getJSONObject("profile").getString("name");
    logger.info("employer name from form26AS : {}", employerName);
    JSONArray form26asDataArray = result.getJSONArray("26asData");

    // comment below line after testing
    JSONObject lastAssessmentYearData =
        form26asDataArray.getJSONObject(form26asDataArray.length() - 1);
    logger.info("lastAssessmentYearData : {}", lastAssessmentYearData);

    boolean isTaxCreditedInLastQuarter = false;
    String currentAY = capriUtilService.getCurrentAssessmentYear();
    for (int i = 0; i < form26asDataArray.length(); i++) {
      JSONObject form26asData = form26asDataArray.getJSONObject(i);
      String form26asAY = form26asData.getString("assessmentYear");
      logger.info("form26asAY : {}", form26asAY);
      if (form26asAY.equals(currentAY)) {
        JSONObject tdsDetails = form26asData.getJSONObject("data").getJSONObject("tdsDetails");
        JSONArray deductorWise =
            tdsDetails.getJSONArray("tds").getJSONObject(0).getJSONArray("deductorWise");
        List<String> quarterMonths = capriUtilService.getCurrentQuarterMonths();
        for (int j = 0; j < deductorWise.length(); j++) {
          JSONObject deductor = deductorWise.getJSONObject(j);
          String transactionDate = deductor.getString("transactionDate");
          String transactionMonth = transactionDate.substring(3, 5);
          if (quarterMonths.contains(transactionMonth)) {
            double amountCredited = Double.parseDouble((String) deductor.get("amountCredited"));
            if (amountCredited > 0) {
              isTaxCreditedInLastQuarter = true;
            }
          }
        }
      }
    }
    logger.info("isTaxCreditedInLastQuarter : {}", isTaxCreditedInLastQuarter);
    form26asApiDetailsRepository.updateIsTaxCredited(requestId, isTaxCreditedInLastQuarter);
    if (isTaxCreditedInLastQuarter) {
      // TODO: Employment FI done’ message will be displayed on the interface.

    } else {
      // TODO: a message saying ‘Tax credit not found in last 3 months in Form 26AS check’ will be
      // displayed.

    }
  }

  private Map<String, Object> createForm26AsCheckPayload(
      String applicationId, String pan, String officialEmailId) {
    Map<String, Object> form26asRequestBody = new HashMap<>();
    Map<String, Object> requestBody = new HashMap<>();
    ArrayList<Object> additionalContact = new ArrayList<>();
    Map<String, Object> addContact = new HashMap<>();
    addContact.put("mobile", "");
    addContact.put("email", "");
    additionalContact.add(addContact);
    requestBody.put("additionalContact", additionalContact);
    requestBody.put("consent", "y");
    Map<String, Object> panData = new HashMap<>();
    panData.put("mobile", "");
    panData.put("email", officialEmailId);
    panData.put("pan", pan);
    panData.put("panName", "");
    panData.put("additionalData", false);
    List<String> itrReportType = new ArrayList<>();
    itrReportType.add("26as_itr");
    panData.put("itrReportType", itrReportType);
    List<String> years = new ArrayList<>();
    //    String ay = capriUtilService.getCurrentAssessmentYear();
    //    years.add(ay);
    panData.put("years", years);
    requestBody.put("panData", panData);
    requestBody.put("businessName", "");
    requestBody.put("applicationId", "");
    requestBody.put("refId", "");
    form26asRequestBody.put("applicationId", applicationId);
    form26asRequestBody.put("request", requestBody);

    return form26asRequestBody;
  }

  private Map<String, Object> createEmailLinkApiPayload(
      String applicationId, String officialEmailId, String applicantName) {
    Map<String, Object> emailLinkApiRequest = new HashMap<>();
    Map<String, Object> requestBody1 = new HashMap<>();
    requestBody1.put("email", officialEmailId);
    requestBody1.put("entityId", "");
    requestBody1.put("employeeName", "");
    requestBody1.put("employerName", "");
    requestBody1.put("verificationType", "LINK");
    requestBody1.put("templateId", "CAPRI_GLOB");
    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("customer", applicantName);
    requestBody1.put("templateVariables", templateVariables);
    Map<String, Object> config = new HashMap<>();
    config.put("trackerEnabled", true);
    config.put("expiryInMinutes", 120);
    config.put("forceLocation", true);
    requestBody1.put("config", config);
    Map<String, Object> notification = new HashMap<>();
    List<String> emailList = new ArrayList<>();
    emailList.add(officialEmailId);
    notification.put("webhook", true);
    Map<String, Object> webHookConfig = new HashMap<>();
    webHookConfig.put("url", "https://los.capriglobal.dev/journey/rollBackResponse");
    notification.put("webhookConfig", webHookConfig);
    notification.put("emails", emailList);
    requestBody1.put("notification", notification);

    emailLinkApiRequest.put("applicationId", applicationId);
    emailLinkApiRequest.put("request", requestBody1);

    return emailLinkApiRequest;
  }
}
