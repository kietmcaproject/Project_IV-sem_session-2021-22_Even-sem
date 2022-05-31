package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.CibilRequest;
import com.kuliza.workbench.model.FinalCibilRequest;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.kuliza.workbench.util.MasterHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.math.NumberUtils;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CibilApiService {
  private static final Logger logger = LoggerFactory.getLogger(CibilApiService.class);
  public static final int MISSING_VALUE = -99999;
  private static final int NINE_TIMES_NINE = 999999999;

  @Autowired RuntimeService runtimeService;
  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;
  @Autowired MasterHelper masterHelper;
  @Autowired IbHelper ibHelper;
  @Autowired CapriUtilService capriUtilService;

  public ApiResponse cibilApi(FinalCibilRequest jsonArray)
      throws UnirestException, JSONException, IOException, ParseException, TransformerException {
    HttpResponse<String> response;
    String score;
    JSONObject finalJsonObject = new JSONObject();
    JSONArray applicantsResponse = new JSONArray();
    Map<String, Object> request = new HashMap<>();
    request.put("header", null);
    Map<String, Object> body = new HashMap<>();
    List<CibilRequest> data1 = jsonArray.getData();

    int applicantNo = 0;
    int counter = 1;
    for (int i = 0; i < data1.size(); i++) {
      CibilRequest cibilRequest = data1.get(i);
      String applicantType = cibilRequest.getApplicantType();

      // logic to make cibil request applicant order independent
      if (applicantType.equalsIgnoreCase("Main")
          || applicantType.equalsIgnoreCase("Primary Applicant")
          || applicantType.equalsIgnoreCase("Primary")) {
        applicantNo = 1;
      } else {
        applicantNo = ++counter;
      }

      body.put(
          "tem:ExecuteXMLString.tem:request",
          "\n<DCRequest xmlns=\"http://transunion.com/dc/extsvc\">\n<Authentication type=\"OnDemand\">\n<UserId>NB6877DC01_UAT003</UserId>\n<Password>7d(J=/Y;kNk=r</Password>\n</Authentication>\n<RequestInfo>\n<SolutionSetId>374</SolutionSetId>\n<ExecuteLatestVersion>true</ExecuteLatestVersion>\n<ExecutionMode>NewWithContext</ExecutionMode>\n</RequestInfo>\n<Fields>\n<Field key=\"Applicants\">\n&lt;Applicants&gt;\n&lt;Applicant&gt;\n&lt;ApplicantType&gt;"
              + cibilRequest.getApplicantType()
              + "&lt;/ApplicantType&gt;\n&lt;ApplicantFirstName&gt;"
              + cibilRequest.getApplicantFName()
              + "&lt;/ApplicantFirstName&gt;\n&lt;ApplicantMiddleName&gt;"
              + cibilRequest.getApplicantMName()
              + "&lt;/ApplicantMiddleName&gt;\n&lt;ApplicantLastName&gt;"
              + cibilRequest.getApplicantLName()
              + "&lt;/ApplicantLastName&gt;\n&lt;DateOfBirth&gt;"
              + cibilRequest.getDob()
              + "&lt;/DateOfBirth&gt;\n&lt;Gender&gt;"
              + cibilRequest.getGender()
              + "&lt;/Gender&gt;\n&lt;EmailAddress&gt;"
              + cibilRequest.getEmailAddress()
              + "&lt;/EmailAddress&gt;\n&lt;CompanyName&gt;"
              + cibilRequest.getCompanyName()
              + "&lt;/CompanyName&gt;\n&lt;Identifiers&gt;\n&lt;Identifier&gt;\n&lt;IdNumber&gt;"
              + cibilRequest.getIdNumber()
              + "&lt;/IdNumber&gt;\n&lt;IdType&gt;"
              + cibilRequest.getIdType()
              + "&lt;/IdType&gt;\n&lt;/Identifier&gt;\n&lt;Identifier&gt;\n&lt;IdNumber&gt;"
              + cibilRequest.getIdNumber()
              + "&lt;/IdNumber&gt;\n&lt;IdType&gt;"
              + cibilRequest.getIdType()
              + "&lt;/IdType&gt;\n&lt;/Identifier&gt;\n&lt;/Identifiers&gt;\n&lt;Telephones&gt;\n&lt;Telephone&gt;\n&lt;TelephoneExtension&gt;"
              + cibilRequest.getTelephoneExtension()
              + "&lt;/TelephoneExtension&gt;\n&lt;TelephoneNumber&gt;"
              + cibilRequest.getTelephoneNumber()
              + "&lt;/TelephoneNumber&gt;\n&lt;TelephoneType&gt;"
              + cibilRequest.getTelephoneType()
              + "&lt;/TelephoneType&gt;\n&lt;/Telephone&gt;\n&lt;Telephone&gt;\n&lt;TelephoneExtension&gt;"
              + cibilRequest.getTelephoneExtension()
              + "&lt;/TelephoneExtension&gt;\n&lt;TelephoneNumber&gt;"
              + cibilRequest.getTelephoneNumber()
              + "&lt;/TelephoneNumber&gt;\n&lt;TelephoneType&gt;"
              + cibilRequest.getTelephoneType()
              + "&lt;/TelephoneType&gt;\n&lt;/Telephone&gt;\n&lt;/Telephones&gt;\n&lt;Addresses&gt;\n&lt;Address&gt;\n&lt;AddressLine1&gt;"
              + cibilRequest.getAddressLine1()
              + "&lt;/AddressLine1&gt;\n&lt;AddressLine2&gt;"
              + cibilRequest.getAddressLine2()
              + "&lt;/AddressLine2&gt;\n&lt;AddressLine3&gt;"
              + cibilRequest.getAddressLine3()
              + "&lt;/AddressLine3&gt;\n&lt;AddressLine4&gt;"
              + cibilRequest.getAddressLine4()
              + "&lt;/AddressLine4&gt;\n&lt;AddressLine5&gt;"
              + cibilRequest.getAddressLine5()
              + "&lt;/AddressLine5&gt;\n&lt;AddressType&gt;"
              + cibilRequest.getAddressType()
              + "&lt;/AddressType&gt;\n&lt;City&gt;"
              + cibilRequest.getCity()
              + "&lt;/City&gt;\n&lt;PinCode&gt;"
              + cibilRequest.getPinCode()
              + "&lt;/PinCode&gt;\n&lt;ResidenceType&gt;"
              + cibilRequest.getResidenceType()
              + "&lt;/ResidenceType&gt;\n&lt;StateCode&gt;"
              + cibilRequest.getStateCode()
              + "&lt;/StateCode&gt;\n&lt;/Address&gt;\n&lt;/Addresses&gt;\n&lt;NomineeRelation&gt;"
              + cibilRequest.getNomineeRelation()
              + "&lt;/NomineeRelation&gt;\n&lt;NomineeName&gt;"
              + cibilRequest.getNomineeName()
              + "&lt;/NomineeName&gt;\n&lt;MemberRelationType4&gt;"
              + cibilRequest.getMemberRelationType4()
              + "&lt;/MemberRelationType4&gt;\n&lt;MemberRelationName4&gt;"
              + cibilRequest.getMemberRelationName4()
              + "&lt;/MemberRelationName4&gt;\n&lt;MemberRelationType3&gt;"
              + cibilRequest.getMemberRelationType3()
              + "&lt;/MemberRelationType3&gt;\n&lt;MemberRelationName3&gt;"
              + cibilRequest.getMemberRelationName3()
              + "&lt;/MemberRelationName3&gt;\n&lt;MemberRelationType2&gt;"
              + cibilRequest.getMemberRelationType2()
              + "&lt;/MemberRelationType2&gt;\n&lt;MemberRelationName2&gt;"
              + cibilRequest.getMemberRelationName2()
              + "&lt;/MemberRelationName2&gt;\n&lt;MemberRelationType1&gt;"
              + cibilRequest.getMemberRelationType1()
              + "&lt;/MemberRelationType1&gt;\n&lt;MemberRelationName1&gt;"
              + cibilRequest.getMemberRelationName1()
              + "&lt;/MemberRelationName1&gt;\n&lt;KeyPersonRelation&gt;"
              + cibilRequest.getKeyPersonRelation()
              + "&lt;/KeyPersonRelation&gt;\n&lt;KeyPersonName&gt;"
              + cibilRequest.getKeyPersonName()
              + "&lt;/KeyPersonName&gt;\n&lt;MemberOtherId3&gt;"
              + cibilRequest.getMemberOtherId3()
              + "&lt;/MemberOtherId3&gt;\n&lt;MemberOtherId3Type&gt;"
              + cibilRequest.getMemberOtherId3Type()
              + "&lt;/MemberOtherId3Type&gt;\n&lt;MemberOtherId2&gt;"
              + cibilRequest.getMemberOtherId2()
              + "&lt;/MemberOtherId2&gt;\n&lt;MemberOtherId2Type&gt;"
              + cibilRequest.getMemberOtherId2Type()
              + "&lt;/MemberOtherId2Type&gt;\n&lt;MemberOtherId1&gt;"
              + cibilRequest.getMemberOtherId1()
              + "&lt;/MemberOtherId1&gt;\n&lt;MemberOtherId1Type&gt;"
              + cibilRequest.getMemberOtherId1Type()
              + "&lt;/MemberOtherId1Type&gt;\n&lt;Accounts&gt;\n&lt;Account&gt;\n&lt;AccountNumber&gt;"
              + cibilRequest.getAccountNumber()
              + "&lt;/AccountNumber&gt;\n&lt;/Account&gt;\n&lt;/Accounts&gt;\n&lt;/Applicant&gt;\n&lt;/Applicants&gt;\n</Field>\n<Field key=\"ApplicationData\">\n&lt;ApplicationData&gt;\n&lt;Purpose&gt;"
              + cibilRequest.getPurpose()
              + "&lt;/Purpose&gt;\n&lt;Amount&gt;"
              + cibilRequest.getAmount()
              + "&lt;/Amount&gt;\n&lt;ScoreType&gt;"
              + cibilRequest.getScoreType()
              + "&lt;/ScoreType&gt;\n&lt;GSTStateCode&gt;"
              + cibilRequest.getGstStateCode()
              + "&lt;/GSTStateCode&gt;\n&lt;MemberCode&gt;NB68779999_UATC2CNPE&lt;/MemberCode&gt;\n&lt;Password&gt;*wpexitbvolSjoaimmntu3kp&lt;/Password&gt;\n&lt;CibilBureauFlag&gt;"
              + cibilRequest.getCibilBureauFlag()
              + "&lt;/CibilBureauFlag&gt;\n&lt;DSTuNtcFlag&gt;"
              + cibilRequest.getDstuntcFlag()
              + "&lt;/DSTuNtcFlag&gt;\n&lt;IDVerificationFlag&gt;"
              + cibilRequest.getIdVerificationFlag()
              + "&lt;/IDVerificationFlag&gt;\n&lt;MFIBureauFlag&gt;"
              + cibilRequest.getMfiBureauFlag()
              + "&lt;/MFIBureauFlag&gt;\n&lt;NTCProductType&gt;"
              + cibilRequest.getNtcProductType()
              + "&lt;/NTCProductType&gt;\n&lt;ConsumerConsentForUIDAIAuthentication&gt;"
              + cibilRequest.getConsumerConsentForUIDAIAuthentication()
              + "&lt;/ConsumerConsentForUIDAIAuthentication&gt;\n&lt;MFIEnquiryAmount&gt;"
              + cibilRequest.getMfiEnquiryAmount()
              + "&lt;/MFIEnquiryAmount&gt;\n&lt;MFILoanPurpose&gt;"
              + cibilRequest.getMfiLoanPurpose()
              + "&lt;/MFILoanPurpose&gt;\n&lt;MFICenterReferenceNo&gt;"
              + cibilRequest.getMfiCenterReferenceNo()
              + "&lt;/MFICenterReferenceNo&gt;\n&lt;MFIBranchReferenceNo&gt;"
              + cibilRequest.getMfiBranchReferenceNo()
              + "&lt;/MFIBranchReferenceNo&gt;\n&lt;FormattedReport&gt;"
              + cibilRequest.getFormattedReport()
              + "&lt;/FormattedReport&gt;\n&lt;/ApplicationData&gt;\n</Field>\n<Field key=\"FinalTraceLevel\">2</Field>\n</Fields>\n</DCRequest>\n");
      request.put("body", body);
      logger.info("Request body starts........................................................");
      logger.info("Request :- {}", request);

      response =
          Unirest.post(ibHelper.getSoapUrl("soap/" + "soap-demo/executexmlstringbasichttp"))
              .header("Content-Type", "application/json")
              .body(new JSONObject(request))
              .asString();
      logger.info("Response :- {}", response.getBody());

      String a = response.getBody();
      String b = a.replaceFirst("(?:=)+", ":");

      JSONObject object = new JSONObject(b);
      try {
        JSONObject fullResponseInJson = XML.toJSONObject(b);
        logger.info("fullResponseInJson :- {}", fullResponseInJson);
      } catch (Exception e) {
      }
      logger.info("Object :- {}", object);
      JSONObject data = object.getJSONObject("ExecuteXMLStringResponse");
      try {

        JSONObject object2 = XML.toJSONObject((data).getString("ExecuteXMLStringResult"));
        logger.info("Object2 :- {}", object2);
        JSONObject object3 =
            (JSONObject)
                object2
                    .getJSONObject("DCResponse")
                    .getJSONObject("ContextData")
                    .getJSONArray("Field")
                    .get(0);
        logger.info("Object3 :- {}", object3);
        String object4 = object3.getString("content");
        JSONObject object5 = XML.toJSONObject(object4);
        logger.info("Object5 :- {}", object5);

        PragatiAppDetails pragatiAppDetails =
            pragatiAppDetailsRepository.findByApplicationId(
                Integer.parseInt(jsonArray.getApplicationId()));
        logger.info("pragatiAppDetails :- {}", pragatiAppDetails.getProcessInstanceId());
        String applicationId = pragatiAppDetails.getProcessInstanceId();

        JSONObject object6 =
            XML.toJSONObject(
                object5
                    .getJSONObject("Applicants")
                    .getJSONObject("Applicant")
                    .getJSONObject("DsCibilBureau")
                    .getJSONObject("Response")
                    .getJSONObject("CibilBureauResponse")
                    .getString("BureauResponseXml"));
        logger.info("Response Cibil :- {}", object6);

        //        try {
        //          if (object6.has("Response")) {
        //            return new ApiResponse(
        //                HttpStatus.OK,
        //                "Error",
        //
        // object6.getJSONObject("Response").getString("ErrorDescription").toString());
        //          }
        //        } catch (JSONException e) {
        //          e.printStackTrace();
        //        }
        JSONObject creditReport = object6.getJSONObject("CreditReport");

        runtimeService.setVariable(
            applicationId, "applicant" + applicantNo + "CreditReport", creditReport.toString());

        score =
            object6.getJSONObject("CreditReport").getJSONObject("ScoreSegment").getString("Score");
        logger.info(" Score :- {}", score);
        runtimeService.setVariable(
            applicationId, "isApplicant" + applicantNo + "CibilScoreNegative", "no");
        if (score.contains("-")) {
          score = "-1";
        }

        logger.info("Cibil Score :- {}", score);

        logger.info("jsonArray ApplicationId :- {}", jsonArray.getApplicationId());

        JSONArray applicant1CirVerification = new JSONArray();
        JSONObject applicant1Cir = new JSONObject();
        //  applicant1Cir.put("applicant"+(i+1)+"CirReport_hl", creditReport);
        int scoreInInt = Integer.parseInt(score);
        logger.info("variable Set sucessFully :- {}", applicationId);
        applicant1Cir.put("applicant" + applicantNo + "CirScore_hl", scoreInInt);
        logger.info("variable Set sucessFully :- {}", applicationId);
        applicant1Cir.put("cibilScore_hl", scoreInInt);
        applicant1CirVerification.put(applicant1Cir);
        logger.info("applicant1CirVerification :- {}", applicant1CirVerification);
        runtimeService.setVariable(
            applicationId,
            "applicant" + applicantNo + "CirVerification_hl",
            CommonHelperFunctions.getStringValue(applicant1CirVerification));
        logger.info("Integer value :- {}", scoreInInt);
        runtimeService.setVariable(
            applicationId, "applicant" + applicantNo + "CirNumber_hl", scoreInInt);
        if (scoreInInt < 300) {
          runtimeService.setVariable(
              applicationId, "isApplicant" + applicantNo + "CibilScoreNegative", "yes");
        }
        logger.info("cibilRequest.getApplicantType :- {}", cibilRequest.getApplicantType());

        if (!(scoreInInt < 300
            || cibilRequest.getApplicantType().equalsIgnoreCase("Non-Financial Co-Applicant"))) {
          try {
            String tlFlatResponse =
                bureauModelTlFlat(creditReport, applicantNo, applicationId, applicantType);
            logger.info("TL FLAT RESPONSE :- {}", tlFlatResponse);
            String tlLongResponse =
                bureauModelTlLong(tlFlatResponse, applicantNo, applicationId, applicantType);
            logger.info("TL LONG RESPONSE :- {}", tlLongResponse);

            JSONArray cvFlat = CVFlatSheetCreation(applicationId, creditReport, applicantNo);
            JSONArray enqFlat = ENQFlatSheetCreation(creditReport);
            loanToUnSecuredLoanRatio(
                applicationId, new JSONArray(tlFlatResponse), enqFlat, applicantNo);
          } catch (Exception e) {
            e.printStackTrace();
          }

          if (applicantNo == 1) { // it was i == 0
            JSONArray account = null;
            try {
              account = creditReport.getJSONArray("Account");
            } catch (Exception e) {
            }
            JSONArray monthlyObligationTable = new JSONArray();
            String reportingMemberShortName, dateOpenedOrDisbursed;
            int accountType,
                highCreditOrSanctionedAmount,
                emiAmount,
                repaymentTenure,
                currentBalance,
                referenceNumber;
            for (int k = 0; k < account.length(); k++) {
              JSONObject accountNonSummarySegmentFields = null;
              JSONObject obligation = null;
              try {
                JSONObject jsonObject = (JSONObject) creditReport.getJSONArray("Account").get(k);
                accountNonSummarySegmentFields =
                    jsonObject.getJSONObject("Account_NonSummary_Segment_Fields");
                obligation = new JSONObject();
                logger.info("Mapping Start for Monthly Obligation Table................");
              } catch (JSONException e) {
                throw new RuntimeException(e);
              }

              try {
                reportingMemberShortName =
                    accountNonSummarySegmentFields.getString("ReportingMemberShortName");
                obligation.put("applicant1FinancerName_hl", reportingMemberShortName);
              } catch (Exception e) {
              }
              try {
                accountType = accountNonSummarySegmentFields.getInt("AccountType");
                obligation.put("applicant1ObligLoanType_hl", accountType);
              } catch (Exception e) {
              }
              try {
                highCreditOrSanctionedAmount =
                    accountNonSummarySegmentFields.getInt("HighCreditOrSanctionedAmount");
                obligation.put("applicant1ObligLoanAmount_hl", highCreditOrSanctionedAmount);
              } catch (Exception e) {
              }
              try {
                dateOpenedOrDisbursed =
                    accountNonSummarySegmentFields.getString("DateOpenedOrDisbursed");
                dateOpenedOrDisbursed =
                    new StringBuilder(dateOpenedOrDisbursed)
                        .insert(dateOpenedOrDisbursed.length() - 4, "/")
                        .toString();
                dateOpenedOrDisbursed =
                    new StringBuilder(dateOpenedOrDisbursed)
                        .insert(dateOpenedOrDisbursed.length() - 7, "/")
                        .toString();
                obligation.put("applicant1ObligLoanYear_hl", dateOpenedOrDisbursed);
              } catch (Exception e) {
              }
              try {
                emiAmount = accountNonSummarySegmentFields.getInt("EmiAmount");
                obligation.put("applicant1ObligEmi_hl", emiAmount);
              } catch (Exception e) {
              }
              try {
                repaymentTenure = accountNonSummarySegmentFields.getInt("RepaymentTenure");
                obligation.put("applicant1ObligTenure_hl", repaymentTenure);
              } catch (Exception e) {
              }
              try {
                currentBalance = accountNonSummarySegmentFields.getInt("CurrentBalance");
                obligation.put("applicant1Pos_hl", currentBalance);
              } catch (Exception e) {
              }
              try {
                referenceNumber = creditReport.getJSONObject("Header").getInt("ReferenceNumber");
                obligation.put("applicant1ObligLoanNumber_hl", referenceNumber);
              } catch (Exception e) {
              }
              try {
                if (accountNonSummarySegmentFields.has("ClosedDate")) {
                  obligation.put("applicant1ObligLoanStatus_hl", "Closed");
                } else {
                  obligation.put("applicant1ObligLoanStatus_hl", "Live");
                }
              } catch (Exception e) {
              }
              monthlyObligationTable.put(obligation);
            }
            logger.info("Monthly Obligation Table :-{}", monthlyObligationTable);

            runtimeService.setVariable(
                applicationId,
                "applicant1MonthlyObligationTable_hl",
                monthlyObligationTable.toString());
          }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ApplicantType", applicantType);
        logger.info("jsonObject sucessFully :- {}", jsonObject);

        int applicantScore = Integer.parseInt(score);
        jsonObject.put("applicantCibilScore", applicantScore);
        if (applicantScore >= 600) {
          jsonObject.put("CibilResponse", "Approve");
        } else {
          jsonObject.put("CibilResponse", "Reject");
        }
        applicantsResponse.put(jsonObject);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    logger.info("Applicants Response :- {}", applicantsResponse);

    int primaryApplicantScore = 0, coApplicantScore = 0;
    JSONArray totalCoApplicants = new JSONArray();

    for (int i = 0; i < applicantsResponse.length(); i++) {
      JSONObject applicant = applicantsResponse.getJSONObject(i);
      JSONObject coApplicants = new JSONObject();
      CibilRequest cibilRequest = data1.get(i);
      String applicantType = cibilRequest.getApplicantType();
      if (applicantType.equalsIgnoreCase("Main")
          || applicantType.equalsIgnoreCase("Primary Applicant")
          || applicantType.equalsIgnoreCase("Primary")) {
        primaryApplicantScore = applicant.getInt("applicantCibilScore");
      } else {
        coApplicantScore = applicant.getInt("applicantCibilScore");
        coApplicants.put("coApplicantScore", coApplicantScore);
        totalCoApplicants.put(coApplicants);
      }
    }
    logger.info("total co applicants :- {}", totalCoApplicants);

    JSONObject finalResponse = new JSONObject();
    if (applicantsResponse.length() == 1) {
      if (primaryApplicantScore >= 600) {
        finalResponse.put("finalResponse", "Approve");
      } else {
        finalResponse.put("finalResponse", "Reject");
      }
    }
    if (applicantsResponse.length() > 1 && primaryApplicantScore < 600) {
      finalResponse.put("finalResponse", "Reject");
    }

    if (applicantsResponse.length() > 1 && primaryApplicantScore >= 600) {
      boolean flag = false;
      for (int i = 0; i < totalCoApplicants.length(); i++) {
        int coApplicant = totalCoApplicants.getJSONObject(i).getInt("coApplicantScore");
        if (coApplicant < 600) {
          flag = true;
        }
      }
      if (flag) {
        finalResponse.put("finalResponse", "Review");
      } else {
        finalResponse.put("finalResponse", "Approve");
      }
    }
    applicantsResponse.put(finalResponse);
    finalJsonObject.put("applicationId", jsonArray.getApplicationId());
    finalJsonObject.put("cibilResponse", applicantsResponse);
    logger.info("Final Response :- {}", finalJsonObject);
    //    CibilSample cibilSample = new CibilSample();
    //    cibilSample.setProperties(String.valueOf(finalJsonObject));
    //    cibilSampleRepository.save(cibilSample);

    Map<String, Object> res =
        CommonHelperFunctions.getHashMapFromJsonString(finalJsonObject.toString());

    return new ApiResponse(HttpStatus.OK, "SUCCESS", res);
  }

  public String bureauModelTlFlat(
      JSONObject creditReport, int applicantNo, String processInstanceId, String applicantType)
      throws JSONException, java.text.ParseException, UnirestException {
    JSONArray account = creditReport.getJSONArray("Account");
    logger.info("account array :- {}", account);
    JSONArray tlFlat = new JSONArray();
    SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
    String currentDate = capriUtilService.getCurrentDate("dd MM yyyy");
    logger.info("CURRENT DATE :- {}", currentDate);
    Date currentDateValue = myFormat.parse(currentDate);
    logger.info("CURRENT DATE Value:- {}", currentDateValue);
    String openedDt = null;
    try {
      openedDt = creditReport.getJSONObject("Header").get("DateProcessed").toString();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    logger.info("OPENEDDT :- {}", openedDt);
    openedDt = new StringBuilder(openedDt).insert(2, " ").toString();
    openedDt = new StringBuilder(openedDt).insert(5, " ").toString();
    logger.info("OPENEDDT AFTER SPACE :- {}", openedDt);
    Date openedDtFormat = myFormat.parse(openedDt);
    logger.info("OPENEDDT FORMATTED :- {}", openedDtFormat);
    int countOfClosedLoans = 0;
    int maxDaysBetweenAcctOpen = 0;
    int totalCurrentLoanAmount = 0;
    int totalCreditTaken = 0;
    double ratio;
    int highCredit = 0;
    Set<String> set = new HashSet<>();
    String var;

    for (int i = 0; i < account.length(); i++) {

      JSONObject row = new JSONObject();
      String reportDt = null;
      JSONObject jsonObject = (JSONObject) creditReport.getJSONArray("Account").get(i);
      JSONObject accountNonSummarySegmentFields =
          jsonObject.getJSONObject("Account_NonSummary_Segment_Fields");
      try {
        reportDt = accountNonSummarySegmentFields.get("DateReportedAndCertified").toString();
      } catch (JSONException e) {
        e.printStackTrace();
      }
      logger.info("REPORT DATE :- {}", reportDt);
      String closedDt = "NA";
      try {
        closedDt = accountNonSummarySegmentFields.getString("DateClosed");
      } catch (Exception e) {
      }
      logger.info("CLOSED DATE :- {}", closedDt);
      boolean isAfter = false;

      if (!closedDt.equalsIgnoreCase("NA")) {
        closedDt = new StringBuilder(closedDt).insert(2, " ").toString();
        closedDt = new StringBuilder(closedDt).insert(5, " ").toString();
        logger.info("CLOSED DATE AFTER SPACE :- {}", closedDt);
        Date closedDt1 = myFormat.parse(closedDt);
        logger.info("CLOSED DATE FORMATTED :- {}", closedDt1);
        isAfter = openedDtFormat.after(closedDt1);
      }

      try {
        highCredit = accountNonSummarySegmentFields.getInt("HighCreditOrSanctionedAmount");
      } catch (Exception e) {
      }
      try {
      } catch (Exception e) {
        e.printStackTrace();
      }
      logger.info("HIGH CREDIT :- {}, ISAfter :- {}", highCredit, isAfter);
      if (!(openedDt == null || reportDt == null || highCredit > 999999999 || isAfter)) {
        logger.info("IF CONDITION STARTS...................................................");
        row.put("acct_num", creditReport.getJSONObject("Header").getInt("ReferenceNumber"));
        row.put("acct_type", accountNonSummarySegmentFields.get("AccountType").toString());

        if (highCredit < 0) {
          highCredit = 0;
        }
        row.put("high_credit", highCredit);

        String curBalance = null;
        try {
          curBalance = accountNonSummarySegmentFields.get("CurrentBalance").toString();
        } catch (JSONException e) {
          e.printStackTrace();
        }
        logger.info("CURRENT BALANCE :- {}", curBalance);
        if (Integer.parseInt(curBalance) < 0) {
          curBalance = "0";
        }
        row.put("cur_balance", curBalance);
        row.put("report_dt", reportDt);
        row.put("opened_dt", openedDt);
        row.put("close_dt", closedDt);
        String pmtStartDate = null;
        String pmtHistoryEndDate = null;
        try {
          pmtStartDate = accountNonSummarySegmentFields.get("PaymentHistoryStartDate").toString();
        } catch (JSONException e) {
          e.printStackTrace();
        }

        try {
          pmtHistoryEndDate =
              accountNonSummarySegmentFields.get("PaymentHistoryEndDate").toString();
        } catch (JSONException e) {
          e.printStackTrace();
        }
        row.put("pmt_start_dt", pmtStartDate);
        row.put("pmt_end_dt", pmtHistoryEndDate);
        try {
          row.put("p_hist_01", accountNonSummarySegmentFields.get("PaymentHistory1").toString());
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_02", accountNonSummarySegmentFields.get("PaymentHistory2").toString());
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_03", accountNonSummarySegmentFields.get("PaymentHistory3").toString());
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_04", accountNonSummarySegmentFields.get("PaymentHistory4").toString());
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_05", accountNonSummarySegmentFields.get("PaymentHistory5").toString());
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_06", accountNonSummarySegmentFields.getString("PaymentHistory6"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_07", accountNonSummarySegmentFields.getString("PaymentHistory7"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_08", accountNonSummarySegmentFields.getString("PaymentHistory8"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_09", accountNonSummarySegmentFields.getString("PaymentHistory9"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_10", accountNonSummarySegmentFields.getString("PaymentHistory10"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_11", accountNonSummarySegmentFields.getString("PaymentHistory11"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_12", accountNonSummarySegmentFields.getString("PaymentHistory12"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_13", accountNonSummarySegmentFields.getString("PaymentHistory13"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_14", accountNonSummarySegmentFields.getString("PaymentHistory14"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_15", accountNonSummarySegmentFields.getString("PaymentHistory15"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_16", accountNonSummarySegmentFields.getString("PaymentHistory16"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_17", accountNonSummarySegmentFields.getString("PaymentHistory17"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_18", accountNonSummarySegmentFields.getString("PaymentHistory18"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_19", accountNonSummarySegmentFields.getString("PaymentHistory19"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_20", accountNonSummarySegmentFields.getString("PaymentHistory20"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_21", accountNonSummarySegmentFields.getString("PaymentHistory21"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_22", accountNonSummarySegmentFields.getString("PaymentHistory22"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_23", accountNonSummarySegmentFields.getString("PaymentHistory23"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_24", accountNonSummarySegmentFields.getString("PaymentHistory24"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_25", accountNonSummarySegmentFields.getString("PaymentHistory25"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_26", accountNonSummarySegmentFields.getString("PaymentHistory26"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_27", accountNonSummarySegmentFields.getString("PaymentHistory27"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_28", accountNonSummarySegmentFields.getString("PaymentHistory28"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_29", accountNonSummarySegmentFields.getString("PaymentHistory29"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_30", accountNonSummarySegmentFields.getString("PaymentHistory30"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_31", accountNonSummarySegmentFields.getString("PaymentHistory31"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_32", accountNonSummarySegmentFields.getString("PaymentHistory32"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_33", accountNonSummarySegmentFields.getString("PaymentHistory33"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_34", accountNonSummarySegmentFields.getString("PaymentHistory34"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_35", accountNonSummarySegmentFields.getString("PaymentHistory35"));
        } catch (Exception e) {
        }
        try {
          row.put("p_hist_36", accountNonSummarySegmentFields.getString("PaymentHistory36"));
        } catch (Exception e) {
        }

        var = row.toString();
        if (!set.contains(var)) {
          tlFlat.put(row);
          set.add(row.toString());
        }
      }
    }
    logger.info("TL FLAT ARRAY :- {}", tlFlat);

    int maxValue = 0;
    for (int i = 0; i < tlFlat.length(); i++) {
      JSONObject object = (JSONObject) tlFlat.get(i);

      Date openedDt1 = myFormat.parse(openedDt);
      logger.info("OPENED DATE 2:- {}", openedDt1.getTime());
      long diffOpen = currentDateValue.getTime() - openedDt1.getTime();
      logger.info(
          "DIFFERENCE OPEN DATE :- {}", TimeUnit.DAYS.convert(diffOpen, TimeUnit.MILLISECONDS));
      object.put("days_between_acct_open", TimeUnit.DAYS.convert(diffOpen, TimeUnit.MILLISECONDS));

      JSONObject jsonObject = (JSONObject) creditReport.getJSONArray("Account").get(i);
      JSONObject accountNonSummarySegmentFields =
          jsonObject.getJSONObject("Account_NonSummary_Segment_Fields");
      String closedDt = "NA";
      try {
        closedDt = accountNonSummarySegmentFields.getString("DateClosed");
      } catch (Exception e) {
      }
      if (!closedDt.equalsIgnoreCase("NA")) {
        closedDt = new StringBuilder(closedDt).insert(2, " ").toString();
        closedDt = new StringBuilder(closedDt).insert(5, " ").toString();
        Date closedDt1 = myFormat.parse(closedDt);
        long diffClose = currentDateValue.getTime() - closedDt1.getTime();
        logger.info(
            "DIFFERENCE Close DATE :- {}", TimeUnit.DAYS.convert(diffClose, TimeUnit.MILLISECONDS));
        object.put(
            "days_between_acct_close", TimeUnit.DAYS.convert(diffClose, TimeUnit.MILLISECONDS));
      } else {
        object.put("days_between_acct_close", "NA");
      }

      JSONObject masterObject = masterHelper.getRequestFromMaster("capri-account-type-map");
      logger.info("Response already there...........");

      JSONArray masterData = masterObject.getJSONArray("data");

      for (int j = 0; j < masterData.length(); j++) {
        JSONObject data = masterData.getJSONObject(j);
        if (object.getString("acct_type").equals(data.get("acct_type"))) {
          object.put("description_category1", data.getString("description_category1"));
          object.put("description_category2", data.getString("description_category2"));
          object.put("description_category3", data.getString("description_category3"));
          object.put("description_category4", data.getString("description_category4"));
        }
      }
      logger.info("Response after master data :- {}", object);

      object.put("TL_ID", i + 1);

      try {
        if ((object.getString("description_category4").equals("ALL"))
            && (!object.getString("close_dt").equals("NA"))) {
          countOfClosedLoans++;
        }
      } catch (Exception e) {
      }

      try {
        if (object.getString("description_category2").equals("NonCC")) {
          try {
            maxDaysBetweenAcctOpen = (int) TimeUnit.DAYS.convert(diffOpen, TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            e.printStackTrace();
          }
          logger.info("Mx days b/w acct open :- {}", maxDaysBetweenAcctOpen);
          if (maxDaysBetweenAcctOpen > maxValue) {
            maxValue = maxDaysBetweenAcctOpen;
            logger.info("Mx value :- {}", maxValue);
          }
        }
      } catch (Exception e) {
      }

      try {
        if ((object.getString("description_category3").equals("UnSec_exclCONSKCC"))
            && (object.getString("close_dt").equals("NA"))) {
          try {
            totalCurrentLoanAmount =
                totalCurrentLoanAmount + accountNonSummarySegmentFields.getInt("CurrentBalance");
            logger.info("Total current loan Amount :- {}", totalCurrentLoanAmount);

          } catch (JSONException e) {
            e.printStackTrace();
          }
          try {
            highCredit = accountNonSummarySegmentFields.getInt("HighCreditOrSanctionedAmount");
          } catch (Exception e) {
          }
          totalCreditTaken = totalCreditTaken + highCredit;
          logger.info("Total credit taken :- {}", totalCreditTaken);
        }
      } catch (Exception e) {
      }
    }
    logger.info("TL FLAT ARRAY: {}", tlFlat);

    if (totalCurrentLoanAmount == 0 || totalCreditTaken == 0) {
      ratio = MISSING_VALUE;
    } else {
      ratio = (double) totalCurrentLoanAmount / (double) totalCreditTaken;
    }

    if (maxValue == 0) {
      maxValue = MISSING_VALUE;
    }

    if ((totalCurrentLoanAmount == 0) || (totalCreditTaken == 0)) {
      ratio = MISSING_VALUE;
    }

    runtimeService.setVariable(
        processInstanceId, "applicant" + applicantNo + "CountClosedLoans_hl", countOfClosedLoans);
    runtimeService.setVariable(
        processInstanceId, "applicant" + applicantNo + "BureauHistory_hl", maxValue);
    runtimeService.setVariable(
        processInstanceId, "applicant" + applicantNo + "OutStandingLoan_hl", ratio);

    logger.info("Count of Closed Loans applicant" + applicantNo + ":- {}", countOfClosedLoans);
    logger.info("Credit Vintage (Non-CC) applicant " + applicantNo + ": {}", maxValue);
    logger.info("Outstanding % of Un Sec Loan applicant" + applicantNo + ": {}", ratio);

    return tlFlat.toString();
  }

  public String bureauModelTlLong(
      String tlFlatResponse, int applicantNo, String processInstanceId, String applicantType)
      throws JSONException, java.text.ParseException {
    JSONArray tlFlatArrayResponse = new JSONArray(tlFlatResponse);
    JSONArray tlLong = new JSONArray();

    int count = 0;
    int maxDpdAcrossAllTradelinesInL3M = 0;
    int maxDpdInL12MOnAutoLoans = 0;
    int countOfClosedCdLoans = 0;
    for (int i = 0; i < tlFlatArrayResponse.length(); i++) {
      JSONObject object = (JSONObject) tlFlatArrayResponse.get(i);
      logger.info("flat object: {}", object);

      int maxDpdL3MValue = 0;
      int maxDpdL12MValue = 0;
      for (int j = 0; j < 36; j++) {
        JSONObject tlLongRow = new JSONObject();
        if (object.has("p_hist_0" + (j + 1))) {
          tlLongRow.put("DPD_month_string", "p_hist_0" + (j + 1));
          tlLongRow.put("DPD_month_num", -i);
          String dpdValue = object.getString("p_hist_0" + (j + 1));
          try {
            if (Integer.parseInt(dpdValue) < 0) {
              dpdValue = "NA";
            }
          } catch (NumberFormatException e) {
            dpdValue = "NA";
          }
          tlLongRow.put("DPD_value", dpdValue);
          //      tlLongRow.put("DPD_month", object.getString("pmt_start_dt"));
          String pmtStartDate = object.getString("pmt_start_dt");
          logger.info("pmtstartdate :- {}", pmtStartDate);
          pmtStartDate = new StringBuilder(pmtStartDate).insert(2, " ").toString();
          pmtStartDate = new StringBuilder(pmtStartDate).insert(5, " ").toString();
          logger.info("pmtstartdateafter space :- {}", pmtStartDate);
          SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
          Date pmtStartDateFormat = myFormat.parse(pmtStartDate);
          Calendar c = Calendar.getInstance();
          c.setTime(pmtStartDateFormat);
          c.add(Calendar.MONTH, -i);
          Date date = c.getTime();
          String result = myFormat.format(date);
          logger.info("pmtstartdateResult :- {}", result);
          tlLongRow.put("DPD_month", result);

          String currentDate = myFormat.format(new Date());
          Date currentDateValue = myFormat.parse(currentDate);
          Date result1 = myFormat.parse(result);
          long diffResult = currentDateValue.getTime() - result1.getTime();
          tlLongRow.put(
              "days_between_acct_dpd", TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS));
          tlLong.put(tlLongRow);

          try {
            if ((tlLongRow.getInt("days_between_acct_dpd") <= 1095)
                && (Integer.parseInt(tlLongRow.getString("DPD_value")) >= 60)) {
              count++;
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          try {
            if ((TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS) <= 90)
                && (object.getString("description_category4").equals("ALL"))) {
              try {
                maxDpdAcrossAllTradelinesInL3M =
                    (int) TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS);
              } catch (Exception e) {
                e.printStackTrace();
              }
              logger.info("maxDpdAcrossAllTradelinesInL3M :- {}", maxDpdAcrossAllTradelinesInL3M);
              if (maxDpdAcrossAllTradelinesInL3M > maxDpdL3MValue) {
                maxDpdL3MValue = maxDpdAcrossAllTradelinesInL3M;
                logger.info("Mx value :- {}", maxDpdL3MValue);
              }
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

          try {
            if ((TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS) <= 365)
                && object.getString("description_category1").equals("AUTO")) {
              maxDpdInL12MOnAutoLoans =
                  (int) TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
          logger.info("maxDpdInL12MOnAutoLoans :- {}", maxDpdInL12MOnAutoLoans);
          if (maxDpdInL12MOnAutoLoans > maxDpdL12MValue) {
            maxDpdL12MValue = maxDpdInL12MOnAutoLoans;
            logger.info("Mx L12value :- {}", maxDpdL12MValue);
          }

          try {
            if ((tlLongRow.getInt("days_between_acct_dpd") <= 365)
                && (object.getString("description_category1").equals("CONSUMER"))
                && !(object.getString("close_dt").equals("NA"))) {
              countOfClosedCdLoans++;
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }

    if (maxDpdAcrossAllTradelinesInL3M == 0) {
      maxDpdAcrossAllTradelinesInL3M = MISSING_VALUE;
    }

    if (maxDpdInL12MOnAutoLoans == 0) {
      maxDpdInL12MOnAutoLoans = MISSING_VALUE;
    }

    runtimeService.setVariable(
        processInstanceId, "applicant" + applicantNo + "60PlusDpdInL3y_hl", count);
    runtimeService.setVariable(
        processInstanceId,
        "applicant" + applicantNo + "MaxDpdAllL3m_hl",
        maxDpdAcrossAllTradelinesInL3M);
    runtimeService.setVariable(
        processInstanceId,
        "applicant" + applicantNo + "MaxDpdAutoL12m_hl",
        maxDpdInL12MOnAutoLoans);
    runtimeService.setVariable(
        processInstanceId,
        "applicant" + applicantNo + "closedcdloansCount_hl",
        countOfClosedCdLoans);

    logger.info("60+ DPD on any loans in last 3Y applicant " + applicantNo + " :- {}", count);
    logger.info(
        "Max DPD 3 Months applicant" + applicantNo + " :- {}", maxDpdAcrossAllTradelinesInL3M);
    logger.info(
        "Max DPD 12 Months Auto applicant" + applicantNo + " :- {}", maxDpdInL12MOnAutoLoans);
    logger.info(
        "Count of Closed CD Loans applicant" + applicantNo + " :- {}", countOfClosedCdLoans);

    return tlLong.toString();
  }

  public JSONArray CVFlatSheetCreation(
      String processInstanceId, JSONObject creditReport, int applicantNo) throws JSONException {
    JSONArray CV_Flat = new JSONArray();

    JSONObject row = new JSONObject();
    row.put("acct_num", creditReport.getJSONObject("Header").get("ReferenceNumber").toString());
    row.put("opened_dt", creditReport.getJSONObject("Header").get("DateProcessed").toString());
    row.put(
        "PAYMNT63",
        creditReport
            .getJSONObject("ScoreSegment")
            .getJSONObject("BureauCharacterstics")
            .get("PAYMNT63")
            .toString());
    row.put("v3_score", creditReport.getJSONObject("ScoreSegment").get("Score").toString());
    CV_Flat.put(0, row);

    HashMap<String, Integer> acctNumbers = new HashMap<>();
    JSONArray nonDupCV_Flat = new JSONArray();
    for (int i = 0; i < CV_Flat.length(); i++) {
      JSONObject cvFlatRow = CV_Flat.getJSONObject(i);
      String acctNumber = cvFlatRow.get("acct_num").toString();
      if (acctNumbers.containsKey(acctNumber)) {
        int index = acctNumbers.get(acctNumber);
        if (Integer.parseInt(cvFlatRow.get("opened_dt").toString())
            > Integer.parseInt(nonDupCV_Flat.getJSONObject(index).get("opened_dt").toString())) {
          nonDupCV_Flat.getJSONObject(index).put("opened_dt", cvFlatRow.get("opened_dt"));
          nonDupCV_Flat.getJSONObject(index).put("PAYMNT63", cvFlatRow.get("PAYMNT63"));
          nonDupCV_Flat.getJSONObject(index).put("CV_Final", cvFlatRow.get("PAYMNT63"));
        }
      } else {
        acctNumbers.put(acctNumber, i);
        cvFlatRow.put("CV_Final", cvFlatRow.get("PAYMNT63"));
        nonDupCV_Flat.put(cvFlatRow);
      }
    }

    logger.info("--------- CV_Flat :- {}", CV_Flat);
    logger.info("--------- nonDupCV_Flat :- {}", nonDupCV_Flat);

    String missedPayment = nonDupCV_Flat.getJSONObject(0).get("CV_Final").toString();
    logger.info("applicant{}MissedPayment_hl : {}", applicantNo, missedPayment);

    if (missedPayment.equals("")) {
      missedPayment = "0";
    }

    try {
      double cvFinal = 0;
      if (NumberUtils.isParsable(missedPayment)) {
        cvFinal = Double.parseDouble(missedPayment);
        if (cvFinal < 0 || cvFinal == 999) {
          cvFinal = 0;
        }
        cvFinal = cvFinal / 100;
        runtimeService.setVariable(
            processInstanceId, "applicant" + applicantNo + "MissedPayment_hl", cvFinal);
      }
    } catch (Exception e) {
      logger.info("Error while parsing CV_Final (i.e, PAYMNT63) as ");
    }
    return nonDupCV_Flat;
  }

  public JSONArray ENQFlatSheetCreation(JSONObject creditReport) throws JSONException {
    JSONArray ENQ_Flat = new JSONArray();

    JSONArray enquiry = creditReport.getJSONArray("Enquiry");

    for (int i = 0; i < enquiry.length(); i++) {
      if (enquiry.getJSONObject(i).get("DateOfEnquiryFields") == null
          || enquiry.getJSONObject(i).get("DateOfEnquiryFields").equals("")
          || Integer.parseInt(enquiry.getJSONObject(i).get("EnquiryAmount").toString())
              > NINE_TIMES_NINE) {
        continue;
      }

      JSONObject row = new JSONObject();
      row.put("acct_num", creditReport.getJSONObject("Header").get("ReferenceNumber").toString());
      row.put("dateofenquiry", enquiry.getJSONObject(i).get("DateOfEnquiryFields").toString());
      row.put("enquirypurpose", enquiry.getJSONObject(i).get("EnquiryPurpose").toString());
      row.put("enquiryamount", enquiry.getJSONObject(i).get("EnquiryAmount").toString());
      ENQ_Flat.put(row);
    }

    JSONObject masterObject = masterHelper.getRequestFromMaster("capri-account-type-map");
    logger.info("account type map master response :- {}", masterObject);
    JSONArray acctTypeMap = masterObject.getJSONArray("data");

    logger.info("acctTypeMap : {}", acctTypeMap);
    int enqId = 1;
    JSONArray newENQ_Flat = new JSONArray();
    for (int i = 0; i < ENQ_Flat.length(); i++) {
      String enquiryPurpose = ENQ_Flat.getJSONObject(i).get("enquirypurpose").toString();
      for (int j = 0; j < acctTypeMap.length(); j++) {
        if (enquiryPurpose.equals(acctTypeMap.getJSONObject(j).get("acct_type").toString())) {
          JSONObject object = ENQ_Flat.getJSONObject(i);
          object.put(
              "description_category1",
              acctTypeMap.getJSONObject(j).get("description_category1").toString());
          object.put(
              "description_category3",
              acctTypeMap.getJSONObject(j).get("description_category3").toString());
          object.put(
              "description_category4",
              acctTypeMap.getJSONObject(j).get("description_category4").toString());
          object.put("ENQ_ID", enqId++);
          newENQ_Flat.put(object);
        }
      }
    }

    logger.info("---------- EnqFlat sheet : {} ", ENQ_Flat);
    logger.info("---------- newEnqFlat sheet : {} ", newENQ_Flat);

    return newENQ_Flat;
  }

  public void loanToUnSecuredLoanRatio(
      String processInstanceId, JSONArray TL_Flat, JSONArray ENQ_Flat, int applicantNo)
      throws JSONException {

    int X = 0, Y = 0;
    for (int i = 0; i < TL_Flat.length(); i++) {
      String description_category3 = null;
      try {
        description_category3 = TL_Flat.getJSONObject(i).get("description_category3").toString();
      } catch (JSONException e) {
        e.printStackTrace();
      }
      if (description_category3.equalsIgnoreCase("UnSec_exclCONSKCC")) {
        X++;
      }
    }
    logger.info("--------------- X (Total Number of Un-Secured Loans taken by a customer): {} ", X);

    for (int i = 0; i < ENQ_Flat.length(); i++) {
      String description_category3 =
          ENQ_Flat.getJSONObject(i).get("description_category3").toString();
      if (description_category3.equalsIgnoreCase("UnSec_exclCONSKCC")) {
        Y++;
      }
    }
    logger.info("--------------- Y (Total Number of Enquiries made for Un-Secured Loans): {} ", Y);

    if (X == 0 || Y == 0) {
      logger.info("applicant{}LoanToUnsecLoanRatio_hl : {}", applicantNo, MISSING_VALUE);
      runtimeService.setVariable(
          processInstanceId, "applicant" + applicantNo + "LoanToUnsecLoanRatio_hl", MISSING_VALUE);
    } else {
      double ratio = (double) X / (double) Y;

      logger.info("applicant{}LoanToUnsecLoanRatio_hl : {}", applicantNo, ratio);
      runtimeService.setVariable(
          processInstanceId, "applicant" + applicantNo + "LoanToUnsecLoanRatio_hl", ratio);
    }
  }

  //  public void master() throws UnirestException {
  //    //            String token = iamService.getAdminAccessToken();
  ////    logger.info("username :- {}", iamConfig.getAdminUsername());
  ////    logger.info("password :- {}", iamConfig.getAdminPassword());
  ////    Form form = new Form();
  ////    form.param(AuthConstants.AUTH_GRANT_TYPE, AuthConstants.AUTH_PASSWORD);
  ////    form.param(AuthConstants.AUTH_USER_NAME, iamConfig.getAdminUsername());
  ////    form.param(AuthConstants.AUTH_PASSWORD, iamConfig.getAdminPassword());
  ////    AccessTokenResponse accessTokenResponse = iamService.grantAccessToken(form);
  ////    String token = accessTokenResponse.getRefreshToken();
  ////    logger.info("iam :- {}", token);
  ////    HttpResponse<JsonNode> master =
  ////        Unirest.get(accountTypeMapMasterUrl).header("Authorization", "Bearer " +
  // token).asJson();
  //        JSONObject master = masterHelper.getRequestFromMaster("capri-account-type-map");
  //    logger.info("Response :- {}", master);
  //  }

  //            public static void main(String[] args) throws IOException, JSONException,
  //         java.text.ParseException, UnirestException {
  //
  //
  //            }
  //            String str = "01122022";
  //            str = new StringBuilder(str).insert(str.length()-4, "/").toString();
  //            str = new StringBuilder(str).insert(str.length()-7, "/").toString();
  //
  //            System.out.println(str);
  //          }
  //          JSONArray array = new JSONArray();
  //          Set<String> set = new HashSet<>();
  //
  //          for (int i = 0; i < 5; i++) {
  //            JSONObject row = new JSONObject();
  //            row.put("a", 1);
  //            row.put("b", 2);
  //            row.put("c", 3);
  //            row.put("d", 4);
  //
  //            String var = row.toString();
  //            System.out.println(var.hashCode());
  //            if(!set.contains(var)) {
  //              array.put(row);
  //              set.add(row.toString());
  //            }
  //
  ////            System.out.println(row.hashCode());
  //          }
  //          System.out.println(array);
  //          System.out.println(set);
  //        }

  //
  //      }
  //      HttpResponse<String> master =
  //
  // Unirest.get("https://los.capriglobal.dev/masters/product/api/masters/capri-account-type-map")
  //                      .header(
  //                              "Authorization",
  //                              "Bearer "+ iamService.getAdminAccessToken()).asString();
  //      logger.info("Response :- {}", master.getBody());
  //    }
  //    SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
  //    String currentDate = myFormat.format(new Date());
  //    Calendar c = Calendar.getInstance();
  //    c.add(Calendar.MONTH, -3);
  //    Date d = c.getTime();
  //    String res = myFormat.format(d);
  //    System.out.println(res);
  //  }
  //
  //
  //      if ((Integer.parseInt(tlLongRow.getString("days_between_acct_dpd")) <= 1095)
  //          && (Integer.parseInt(tlLongRow.getString("DPD_value")) >= 60)) {
  //        count++;
  //      }
  //    }
  //    logger.info("60+ DPD on any loans in last 3Y :- {}", count);
  //  }

  //
  //        public static void main(String[] args) throws IOException, JSONException,
  //   java.text.ParseException, UnirestException {
  //          SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
  //          String start = "01032020";
  //          start = new StringBuilder(start).insert(2, " ").toString();
  //          start = new StringBuilder(start).insert(5, " ").toString();
  //          Date openedDtFormat = myFormat.parse(start);
  //          System.out.println(start);
  //          Calendar c = Calendar.getInstance();
  //          c.setTime(openedDtFormat);
  //          System.out.println(c.getTime());
  //
  //          c.add(Calendar.MONTH, 5);
  //          System.out.println(c);
  //
  //          Date date = c.getTime();
  //          System.out.println(date);
  //          String result = myFormat.format(date);
  //          System.out.println(result);
  //
  //
  //
  //        }
  //          SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
  //          Calendar c = Calendar.getInstance();
  //          c.add(Calendar.MONTH, 1);
  //          String openedDt ="15032022";
  //          openedDt = new StringBuilder(openedDt).insert(2, " ").toString();
  //          openedDt = new StringBuilder(openedDt).insert(5, " ").toString();
  //          Date date = c.getTime();
  //          String result = myFormat.format(date);
  //          System.out.println(result);
  //
  //          String currentDate = myFormat.format(new Date());
  //          Date currentDateValue = myFormat.parse(currentDate);
  //
  //          Date result1 = myFormat.parse(result);
  //          System.out.println(result1);
  //
  //          long diffResult = currentDateValue.getTime() - result1.getTime();
  //          System.out.println(TimeUnit.DAYS.convert(diffResult, TimeUnit.MILLISECONDS));
  //        }
  //          SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
  //          String currentDate = myFormat.format(new Date());
  //          System.out.println(currentDate);
  //          Date currentDateValue = myFormat.parse(currentDate);
  //          System.out.println(currentDateValue);
  //          String openedDt = "01122021";
  //          openedDt = new StringBuilder(openedDt).insert(2, " ").toString();
  //          openedDt = new StringBuilder(openedDt).insert(5, " ").toString();
  //          Date openedDt1 = myFormat.parse(openedDt);
  //          System.out.println(openedDt1);
  //          System.out.println(openedDt1.getDate());
  //          long diffOpen = currentDateValue.getTime() - openedDt1.getTime();
  //          System.out.println(TimeUnit.DAYS.convert(diffOpen, TimeUnit.MILLISECONDS));
  //
  //        }

  //
  //
  //      }
  //        JSONArray tlLong = new JSONArray();
  //        JSONObject object = new JSONObject();
  //
  //        for (int i = 0; i < 36; i++) {
  //          JSONObject tlLongRow = new JSONObject();
  //          tlLongRow.put("DPD_month_string", "p_hist_0" + (i + 1));
  //          tlLongRow.put("DPD_month_num", -i);
  //          tlLongRow.put("DPD_value", "dpdvalue");
  //          tlLong.put(tlLongRow);
  //        }
  //        System.out.println(tlLong);
  //
  //      }
  //
  //
  //        HttpResponse<String> response =
  //
  // Unirest.get("https://los.capriglobal.dev/masters/product/api/masters/capri-account-type-map")
  //                        .header("Authorization", "Bearer " +
  // "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3QmJpejR4a3VlM2ZpZlllMnhtMmEtVWo5RXYxejNGbFQzcjNVUUQ5QjBNIn0.eyJleHAiOjE2NDQ4NDMxNTQsImlhdCI6MTY0NDg0MTM1NCwiYXV0aF90aW1lIjoxNjQ0ODQxMzU0LCJqdGkiOiIzYzE5MDgwYS00NjA0LTRkZmYtOWE5MS0zM2JlMjEyMmM4MWIiLCJpc3MiOiJodHRwczovL2xvcy5jYXByaWdsb2JhbC5kZXYvYXV0aC9yZWFsbXMva3VsaXphX3JlYWxtIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6Ijc1OGI2MDk5LWMxYjUtNGE0YS1iMzQ4LTkyNWUyMmQ1NzI1YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImxlbmRpbmciLCJub25jZSI6Ikg3aUtpV0VEUnVLalB2bkF3elBHNGtITWNSZUJRZnR4YXh2Um5uTnJ5elkiLCJzZXNzaW9uX3N0YXRlIjoiZDMwMDQxZTktOGM1ZS00N2VmLTgxNGItYWJhMzNhN2ZmMDg3IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtYXN0ZXJzX2FkbWluIiwiYWNjZXNzLWlkbSIsIm9mZmxpbmVfYWNjZXNzIiwiYWNjZXNzLXRhc2siLCJhY2Nlc3MtbW9kZWxlciIsInVtYV9hdXRob3JpemF0aW9uIiwiYWNjZXNzLWFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJsZW5kaW5nIjp7InJvbGVzIjpbIm1hc3RlcnNfYWRtaW4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGFkZHJlc3MgZW1haWwgcGhvbmUgbWljcm9wcm9maWxlLWp3dCBvZmZsaW5lX2FjY2VzcyBwcm9maWxlIiwidXBuIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoic3R1ZGlvIGFkbWluIiwiZ3JvdXBzIjpbIm1hc3RlcnNfYWRtaW4iLCJhY2Nlc3MtaWRtIiwib2ZmbGluZV9hY2Nlc3MiLCJhY2Nlc3MtdGFzayIsImFjY2Vzcy1tb2RlbGVyIiwidW1hX2F1dGhvcml6YXRpb24iLCJhY2Nlc3MtYWRtaW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJnaXZlbl9uYW1lIjoic3R1ZGlvIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20ifQ.kgG3n2k_XkhBcWywoWAY2-7AFKYNXmJ6F21RZMIWmOknkTC-S6z3PN2XTd9Q_2bDN1IC37M24QVueKoGBTs5dYP8wtstAiOPwPYI495ccNPUXmp3AbCHX3S4jD37dsLj9PHquEcX5ADwBD2cvOLvXGnPOYrMYayI5PJoep2aKFEwKiADmdxqw7y49V2KNsGglH-V0JAQz6ZoIyLYC4wuwD6p4b6DQbKtKsDmONOzIgFPCNU7x7XLzh_rRfbBD-LbyXAtNT3Lh92_8aclw4BQhkvLZUQgCUUeI0zLo1Y3TM1aYCh4QQ5d21Eqi4VpOrKxqXHKGkNsJQ1Owb6gqSYSWA")
  //                        .asString();
  //        logger.info("Response :- {}", response.getBody());
  //      }

  //        JSONArray tlFlat = new JSONArray();
  //        for(int i = 0; i < 5; i++) {
  //          JSONObject row = new JSONObject();
  //          row.put("acct_num", 2);
  //          row.put("acct_type", 4);
  //          row.put("high_credit", 5);
  //          tlFlat.put(row);
  //
  //        }
  //        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
  //        String currentDate = new SimpleDateFormat("dd MM yyyy").format(new Date());
  //        String date = "14122021";
  //        date = new StringBuilder(date).insert(2, " ").toString();
  //        date = new StringBuilder(date).insert(5, " ").toString();
  //
  //
  //        Date date1 = myFormat.parse(currentDate);
  //        Date date2 = myFormat.parse(date);
  //        System.out.println(date);
  //
  //        long diff = date1.getTime() - date2.getTime();
  //        System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

  //    String response = FileUtils.readFileToString(new File("/home/kuliza-407/Desktop/Untitled"));
  //
  //    JSONObject jsonObject = new JSONObject(response);
  //    String data = jsonObject.getString("data");
  //    System.out.println(data);
  //    String data1 = data.replace("=", ":");
  //    System.out.println(data1);
  //    JSONObject data2 = new JSONObject(data);
  //    System.out.println(data2);
  //  }
  //
  //          JSONObject object2 = XML.toJSONObject(new
  //
  // JSONObject(data).getJSONObject("ExecuteXMLStringResponse").getString("ExecuteXMLStringResult"));
  //          //      System.out.println(object2.toString());
  //          JSONObject object3 =
  //                  (JSONObject)
  // object2.getJSONObject("DCResponse").getJSONObject("ContextData").getJSONArray("Field").get(0);
  //
  //          String object4 = object3.getString("content");
  //          JSONObject object5 = XML.toJSONObject(object4);
  //          //        System.out.println(object5);
  //
  //          JSONObject object6 =
  //
  // XML.toJSONObject(object5.getJSONObject("Applicants").getJSONObject("Applicant").getJSONObject("DsCibilBureau").getJSONObject("Response").getJSONObject("CibilBureauResponse").getString("BureauResponseXml"));
  ////          System.out.println(object6);

  //
  //
  //
  //          String score =
  // (object6.getJSONObject("CreditReport").getJSONObject("ScoreSegment").getString("Score"));
  //          int finalScore;
  //          if(score.contains("-")) {
  //            score = "-1";
  //          }
  //          finalScore= Integer.parseInt(score);
  //          Map<String, String> map = new HashMap<>();
  //          if (finalScore >= 600) {
  //            map.put("Response", "Accept");
  //          } else {
  //            map.put("Response", "Reject");
  //          }
  //          System.out.println(map);
  //        }

  public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("CoApplicant");
    list.add("CoApplicant");
    list.add("CoApplicant");
    list.add("Main");
    list.add("CoApplicant");
    list.add("CoApplicant");

    int applicantNo = 0;
    int count = 1;
    for (int i = 0; i < 6; i++) {
      String applicantType = list.get(i);
      if (applicantType.equalsIgnoreCase("Main")) {
        applicantNo = 1;
      } else {
        applicantNo = ++count;
      }

      System.out.println("applicantType : " + applicantType + " applicantNo : " + applicantNo);
    }

    String number = "-7.5";
    if (NumberUtils.isParsable(number)) {
      double d = Double.parseDouble(number);
      if (d < 0 || d == 999) {
        d = 0;
      }
      d = d / 100;
      System.out.println("d : " + d);
    }
  }
}
