package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.CustomerEmail;
import com.kuliza.workbench.model.KScanApiDetails;
import com.kuliza.workbench.repository.CustomerEmailRepository;
import com.kuliza.workbench.repository.KScanApiDetailsRepository;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("R2CScenariosService")
public class R2CScenariosService {

  private static final Logger logger = LoggerFactory.getLogger(R2CScenariosService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Autowired KScanApiDetailsRepository kScanApiDetailsRepository;

  @Autowired CustomerEmailRepository customerEmailRepository;

  @Autowired NameSimilarityService nameSimilarityService;

  public DelegatePlanItemInstance r2cTablePopulation(DelegatePlanItemInstance planItemInstance) {
    logger.info(" ---------------------- in r2cScenarioService------------------------");

    String caseInstanceId = planItemInstance.getCaseInstanceId();
    Double aDouble =
        Double.parseDouble(
            cmmnRuntimeService.getVariable(caseInstanceId, "applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();

    String finalSubmitRequestBody =
        cmmnRuntimeService.getVariable(caseInstanceId, "finalSubmitRequestBody").toString();
    logger.info("------------- finalSubmitRequestBody : {} ", finalSubmitRequestBody);
    JSONObject finalSubmitJsonObj;

    JSONArray applicants = null;
    try {
      finalSubmitJsonObj = new JSONObject(finalSubmitRequestBody);
      applicants = finalSubmitJsonObj.getJSONArray("applicants");
    } catch (Exception e) {
      e.printStackTrace();
    }
    logger.info("------------ applicants: " + applicants);

    try {

      JSONArray r2cTable = new JSONArray();

      for (int i = 1; i <= applicantCount; i++) {
        int currentApplicantIndex = 0;
        for (int j = 0; j < applicants.length(); j++) {
          if (cmmnRuntimeService
              .getVariable(caseInstanceId, "applicant" + i + "_id")
              .toString()
              .equalsIgnoreCase(applicants.getJSONObject(j).get("applicant_id").toString())) {
            currentApplicantIndex = j;

            try {
              boolean nameMatched =
                  Boolean.parseBoolean(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .get("name_matched")
                          .toString());
              if (!nameMatched) {
                JSONObject row = new JSONObject();
                row.put("r2cUserType_hl", i);
                row.put(
                    "r2cApplicantNames_hl",
                    applicants
                        .getJSONObject(currentApplicantIndex)
                        .get("applicant_name")
                        .toString());
                row.put("r2cIdentificationCode_hl", "R2C01");
                row.put(
                    "r2cDescription_hl",
                    "Name match between Aadhaar name and PAN name < Defined threshold");
                row.put(
                    "r2cRecommendedNextStep_hl",
                    "Review the name on Aadhaar card and Pan card, confirm that dual name affidavit is collected at the time of login");
                r2cTable.put(row);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }

            try {
              boolean dobMatched =
                  Boolean.parseBoolean(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .get("dob_matched")
                          .toString());
              if (!dobMatched) {
                JSONObject row = new JSONObject();
                row.put("r2cUserType_hl", i);
                row.put(
                    "r2cApplicantNames_hl",
                    applicants
                        .getJSONObject(currentApplicantIndex)
                        .get("applicant_name")
                        .toString());
                row.put("r2cIdentificationCode_hl", "R2C02");
                row.put(
                    "r2cDescription_hl",
                    "Aadhaar captures full Date of Birth; and Date of Birth on Aadhaar is different from that on PAN card");
                row.put(
                    "r2cRecommendedNextStep_hl",
                    "Review the Date of Birth as on Aadhaar card and Pan card, confirm that dual DOB affidavit is collected at the time of login");
                r2cTable.put(row);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }

            try {
              boolean isAgeValid =
                  Boolean.parseBoolean(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .get("is_age_valid")
                          .toString());

              if (!isAgeValid) {
                JSONObject row = new JSONObject();
                row.put("r2cUserType_hl", i);
                row.put(
                    "r2cApplicantNames_hl",
                    applicants
                        .getJSONObject(currentApplicantIndex)
                        .get("applicant_name")
                        .toString());
                row.put("r2cIdentificationCode_hl", "R2C03");
                row.put(
                    "r2cDescription_hl",
                    "Aadhaar only captures Year of Birth; and Year of Birth on Aadhaar is not the same as that on PAN card");
                row.put(
                    "r2cRecommendedNextStep_hl",
                    "Review the Date of Birth as on Aadhaar card and Pan card, confirm that dual DOB affidavit is collected at the time of login");
                r2cTable.put(row);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }

            int noOfEmiFromNovelBankAnalysis = 0;
            try {
              // NovelApi response may not be available at the time of 'finalSubmitApi'
              Map<String, Object> variables =
                  cmmnRuntimeService.getVariables(planItemInstance.getCaseInstanceId());

              JSONObject novelApiResponse =
                  new JSONObject(variables.getOrDefault("novelApiResponse_" + i, ""));
              logger.info("----------- novelApiResponse : {}", novelApiResponse);

              String accountNameFromNovel = "";
              String aadhaarNameFromFinalSubmit = "";
              JSONArray analysisData = new JSONArray();

              if (novelApiResponse.length() != 0
                  && novelApiResponse.get("empty").equals("true")
                  && novelApiResponse.has("data")) {
                accountNameFromNovel =
                    novelApiResponse
                        .getJSONArray("data")
                        .getJSONObject(0)
                        .get("accountName")
                        .toString();
                analysisData =
                    novelApiResponse
                        .getJSONArray("data")
                        .getJSONObject(0)
                        .getJSONArray("analysisData");

                for (int l = analysisData.length() - 1; l >= 0; l--) {
                  JSONObject analysis = analysisData.getJSONObject(i);
                  if (analysis.get("month").toString().equals("Grand Total")) {
                    noOfEmiFromNovelBankAnalysis =
                        Integer.parseInt(analysis.get("noOfEMI").toString());
                    logger.info(
                        "number of emi from novel bank statement analysis = {}",
                        noOfEmiFromNovelBankAnalysis);
                    break;
                  }
                }
              }
              aadhaarNameFromFinalSubmit =
                  applicants
                      .getJSONObject(currentApplicantIndex)
                      .getJSONObject("aadhaar")
                      .get("aadhaar_name")
                      .toString();

              logger.info(
                  "------------ accountNameFromNovel : {},  aadhaarNameFromFinalSubmit : {}",
                  accountNameFromNovel,
                  aadhaarNameFromFinalSubmit);

              double nameMatchResult =
                  nameSimilarityService.nameMatchApi(
                      aadhaarNameFromFinalSubmit, accountNameFromNovel);
              logger.info(
                  "nameMatchResult of aadhaar and bank account name is = {}", nameMatchResult);
              if (nameMatchResult < 0.95) {
                JSONObject row = new JSONObject();
                row.put("r2cUserType_hl", i);
                row.put(
                    "r2cApplicantNames_hl",
                    applicants
                        .getJSONObject(currentApplicantIndex)
                        .get("applicant_name")
                        .toString());
                row.put("r2cIdentificationCode_hl", "R2C05");
                row.put(
                    "r2cDescription_hl",
                    "Name mismatch between Aadhaar and bank account Name Match result < threshold as per login BRD");
                row.put(
                    "r2cRecommendedNextStep_hl",
                    "Review customer name on Aadhaar card and bank statement. If required, request another bank statement with customer name");
                r2cTable.put(row);
              }
            } catch (JSONException e) {
              logger.info("INFO - Novel api response may not be available at this moment");
            }

            JSONArray account = null;
            int noOfLoanProductOnCIR = 0;
            try {
              JSONObject creditReport =
                  (JSONObject)
                      cmmnRuntimeService.getVariable(
                          caseInstanceId, "applicant" + currentApplicantIndex + "CreditReport");
              account = creditReport.getJSONArray("Account");
              logger.info("credit reports account = {}", account);
              noOfLoanProductOnCIR = account.length();
            } catch (Exception e) {
              logger.info("Some error while acc");
            }

            logger.info("number of loan products on applicant CIR = {}", noOfLoanProductOnCIR);

            if (noOfLoanProductOnCIR > noOfEmiFromNovelBankAnalysis) {
              JSONObject row = new JSONObject();
              row.put("r2cUserType_hl", i);
              row.put(
                  "r2cApplicantNames_hl",
                  applicants.getJSONObject(currentApplicantIndex).get("applicant_name").toString());
              row.put("r2cIdentificationCode_hl", "R2C07");
              row.put(
                  "r2cDescription_hl",
                  "No. of loan products on the applicant CIR is found to be more than the number of EMIs running in the bank statement");
              row.put(
                  "r2cRecommendedNextStep_hl",
                  "Review the collected bank statement / CIRs / SOAs and update obligation amount if required");
              r2cTable.put(row);
            } else if (noOfLoanProductOnCIR < noOfEmiFromNovelBankAnalysis) {
              JSONObject row = new JSONObject();
              row.put("r2cUserType_hl", i);
              row.put(
                  "r2cApplicantNames_hl",
                  applicants.getJSONObject(currentApplicantIndex).get("applicant_name").toString());
              row.put("r2cIdentificationCode_hl", "R2C08");
              row.put(
                  "r2cDescription_hl",
                  "No. of loan products on the applicant CIR is found to be less than the number of EMIs running in the bank statement");
              row.put(
                  "r2cRecommendedNextStep_hl",
                  "Review the collected bank statement / CIRs / SOAs and update obligation amount if required");
              r2cTable.put(row);
            } else {

            }

            try {
              boolean hasRentalIncome =
                  Boolean.parseBoolean(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .getJSONObject("secondary_income")
                          .get("has_rental_income")
                          .toString());
              boolean hasPensionIncome =
                  Boolean.parseBoolean(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .getJSONObject("secondary_income")
                          .get("has_pension_income")
                          .toString());
              if ((!hasRentalIncome) || (!hasPensionIncome)) {
                JSONObject row = new JSONObject();
                row.put("r2cUserType_hl", i);
                row.put(
                    "r2cApplicantNames_hl",
                    applicants
                        .getJSONObject(currentApplicantIndex)
                        .get("applicant_name")
                        .toString());
                row.put("r2cIdentificationCode_hl", "R2C11");
                row.put("r2cDescription_hl", "Where applicant has secondary income");
                row.put(
                    "r2cRecommendedNextStep_hl",
                    "Review and update the rental and pension income, as the case maybe");
                r2cTable.put(row);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }

            try {
              if (cmmnRuntimeService
                  .getVariable(caseInstanceId, "isApplicant" + i + "NF")
                  .toString()
                  .equalsIgnoreCase("no")) {
                double totalExperience =
                    Double.parseDouble(
                        cmmnRuntimeService
                            .getVariable(caseInstanceId, "applicant" + i + "workExperience_hl")
                            .toString());
                String currentEmploymentVintage =
                    CommonHelperFunctions.getStringValue(
                        applicants
                            .getJSONObject(currentApplicantIndex)
                            .getJSONObject("employment_check")
                            .get("current_employment_vintage")
                            .toString());
                logger.info(
                    "----------- currentEmploymentVintage(String) : {}", currentEmploymentVintage);
                String years = "", months = "";
                int totalMonths = 0;
                double currentEmploymentVintageTotalYears;
                currentEmploymentVintage = currentEmploymentVintage.replaceAll("\\s", "");
                currentEmploymentVintage = currentEmploymentVintage.toUpperCase();
                if (currentEmploymentVintage.contains("Y")) {
                  years =
                      currentEmploymentVintage.substring(0, currentEmploymentVintage.indexOf("Y"));
                }
                if (currentEmploymentVintage.contains("M")) {
                  months =
                      currentEmploymentVintage.substring(
                          currentEmploymentVintage.indexOf("S") + 1,
                          currentEmploymentVintage.indexOf("M"));
                }
                totalMonths = (Integer.parseInt(years) * 12) + Integer.parseInt(months);
                currentEmploymentVintageTotalYears = (double) totalMonths / 12;

                logger.info(
                    "------------- totalExperience : {},  currentEmploymentVintageTotalYears : {}",
                    totalExperience,
                    currentEmploymentVintageTotalYears);

                if (currentEmploymentVintageTotalYears < 1) {
                  JSONObject row = new JSONObject();
                  row.put("r2cUserType_hl", i);
                  row.put(
                      "r2cApplicantNames_hl",
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .get("applicant_name")
                          .toString());
                  row.put("r2cIdentificationCode_hl", "R2C12");
                  row.put("r2cDescription_hl", "Vintage of current employment < 1 year");
                  row.put("r2cRecommendedNextStep_hl", ""); // TODO: Not yet provided by client
                  r2cTable.put(row);
                }

                if (totalExperience < 1) {
                  JSONObject row = new JSONObject();
                  row.put("r2cUserType_hl", i);
                  row.put(
                      "r2cApplicantNames_hl",
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .get("applicant_name")
                          .toString());
                  row.put("r2cIdentificationCode_hl", "R2C13");
                  row.put("r2cDescription_hl", "Vintage of total employment < 1 year");
                  row.put("r2cRecommendedNextStep_hl", ""); // TODO: Not yet provided by client
                  r2cTable.put(row);
                }
              }
            } catch (NumberFormatException e) {
              e.printStackTrace();
            } catch (JSONException e) {
              e.printStackTrace();
            }

            try {
              JSONObject residentialVerification =
                  new JSONObject(
                      applicants
                          .getJSONObject(currentApplicantIndex)
                          .getJSONObject("residential_verification")
                          .toString());
            } catch (Exception e) {
              JSONObject row = new JSONObject();
              row.put("r2cUserType_hl", i);
              row.put(
                  "r2cApplicantNames_hl",
                  applicants.getJSONObject(currentApplicantIndex).get("applicant_name").toString());
              row.put("r2cIdentificationCode_hl", "R2C20");
              row.put(
                  "r2cDescription_hl",
                  "Customer consent for digital residence verification not received");
              row.put(
                  "r2cRecommendedNextStep_hl",
                  "Review FI report received from the external FI vendor");
              r2cTable.put(row);
            }
          }
        }
      }
      String applicationId =
          CommonHelperFunctions.getStringValue(
              cmmnRuntimeService.getVariable(caseInstanceId, "applicationId_hl"));
      if (!applicationId.equals("")) {
        try {
          KScanApiDetails kScanApiDetails =
              kScanApiDetailsRepository.findByApplicationId(Integer.parseInt(applicationId));
          boolean isUniqueMatch = kScanApiDetails.isUniqueMatch();
          if (!isUniqueMatch) {
            JSONObject row = new JSONObject();
            row.put("r2cUserType_hl", 1);
            row.put(
                "r2cApplicantNames_hl",
                cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
            row.put("r2cIdentificationCode_hl", "R2C14");
            row.put("r2cDescription_hl", "Unique match not found on Kscan");
            row.put(
                "r2cRecommendedNextStep_hl",
                "Confirm and update the constitution of the employer at the time of PD");
            r2cTable.put(row);
          }
          boolean isNameFound = kScanApiDetails.isNameFound();

          if (!isNameFound) {
            JSONObject row = new JSONObject();
            row.put("r2cUserType_hl", 1);
            row.put(
                "r2cApplicantNames_hl",
                cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
            row.put("r2cIdentificationCode_hl", "R2C15");
            row.put("r2cDescription_hl", "Name not found on Kscan");
            row.put(
                "r2cRecommendedNextStep_hl",
                "Confirm and update the constitution of the employer at the time of PD");
            r2cTable.put(row);
          }
        } catch (Exception e) {
          logger.info("Error while setting r2c codes i.e., R2C14 or R2C15");
          logger.info("Error message : {}", e.getMessage());
        }

        try {
          CustomerEmail customerEmail =
              customerEmailRepository.findByApplicationId(Integer.parseInt(applicationId));
          boolean isApplicantEmailVerified = Boolean.parseBoolean(customerEmail.getVerification());
          boolean isApplicantEmailPresent = customerEmail.isApplicantEmailPresent();

          if (isApplicantEmailPresent) {
            if (!isApplicantEmailVerified) {
              JSONObject row = new JSONObject();
              row.put("r2cUserType_hl", 1);
              row.put(
                  "r2cApplicantNames_hl",
                  cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
              row.put("r2cIdentificationCode_hl", "R2C16");
              row.put(
                  "r2cDescription_hl",
                  "If the email id verification fails i.e., the applicant’s declared work email id does not exist");
              String cs =
                  CommonHelperFunctions.getStringValue(
                      planItemInstance.getVariable("customerSchemeApp1_hl"));
              if (cs.equalsIgnoreCase("CAT A")) {
                row.put("r2cRecommendedNextStep_hl", "Conduct Tele PD with the employer");
              } else if (cs.equalsIgnoreCase("CAT B") || cs.equalsIgnoreCase("CAT C")) {
                row.put(
                    "r2cRecommendedNextStep_hl", "BCM to conduct physical PD with the employer");
              }
              r2cTable.put(row);
            }
          } else {
            if (!isApplicantEmailVerified) {
              JSONObject row = new JSONObject();
              row.put("r2cUserType_hl", 1);
              row.put(
                  "r2cApplicantNames_hl",
                  cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
              row.put("r2cIdentificationCode_hl", "R2C18");
              row.put(
                  "r2cDescription_hl",
                  "If the email id verification fails i.e., the supervisor’s declared work email id does not exist");
              String cs =
                  CommonHelperFunctions.getStringValue(
                      planItemInstance.getVariable("customerSchemeApp1_hl"));
              if (cs.equalsIgnoreCase("CAT A")) {
                row.put("r2cRecommendedNextStep_hl", "Conduct Tele PD with the employer");
              } else if (cs.equalsIgnoreCase("CAT B") || cs.equalsIgnoreCase("CAT C")) {
                row.put(
                    "r2cRecommendedNextStep_hl", "BCM to conduct physical PD with the employer");
              }
              r2cTable.put(row);
            }
          }

          boolean isLinkTriggered = customerEmail.isLinkTriggered();
          if (!isLinkTriggered) {
            JSONObject row = new JSONObject();
            row.put("r2cUserType_hl", 1);
            row.put(
                "r2cApplicantNames_hl",
                cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
            row.put("r2cIdentificationCode_hl", "R2C17");
            row.put(
                "r2cDescription_hl",
                "Customer did not respond to the trigger link (for employment verification) in 2 hours");
            String cs =
                CommonHelperFunctions.getStringValue(
                    planItemInstance.getVariable("customerSchemeApp1_hl"));
            if (cs.equalsIgnoreCase("CAT A")) {
              row.put("r2cRecommendedNextStep_hl", "Conduct Tele PD with the employer");
            } else if (cs.equalsIgnoreCase("CAT B") || cs.equalsIgnoreCase("CAT C")) {
              row.put("r2cRecommendedNextStep_hl", "BCM to conduct physical PD with the employer");
            }
            r2cTable.put(row);
          }

        } catch (Exception e) {
          logger.info("Error while setting r2c codes i.e., R2C16 or R2C17 or R2C18");
          logger.info("Error message : {}", e.getMessage());
        }
      }

      try {
        boolean isAllCibilScoreMoreThan600 = true;
        for (int i = 1; i <= applicantCount; i++) {
          int cibilScore =
              Integer.parseInt(
                  cmmnRuntimeService
                      .getVariable(caseInstanceId, "applicant" + i + "CirNumber_hl")
                      .toString());
          logger.info("applicant{} cibil score is = {}", i, cibilScore);
          isAllCibilScoreMoreThan600 = isAllCibilScoreMoreThan600 && cibilScore >= 600;
        }

        if (isAllCibilScoreMoreThan600) {
          JSONObject row = new JSONObject();
          row.put("r2cUserType_hl", 1);
          row.put(
              "r2cApplicantNames_hl",
              cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
          row.put("r2cIdentificationCode_hl", "R2C04");
          row.put(
              "r2cDescription_hl",
              "IF Multiple CIR reports found:\n"
                  + "All CIBIL scores >= X, Or, \n"
                  + "First CIBIL score >= X, other CIBIL scores <X \n"
                  + "Where:\n"
                  + "X = Threshold = 600");
          row.put(
              "r2cRecommendedNextStep_hl",
              "Review all CIR reports corresponding to the customer and match obligations between CIR reports and bank statements. Where required, update the obligation amount\n"
                  + "\n"
                  + "Where EMI is not found in bank statement, request additional bank statement from the customer");
          r2cTable.put(row);
        } else {
          int firstCibilScore =
              Integer.parseInt(
                  cmmnRuntimeService
                      .getVariable(caseInstanceId, "applicant1CirNumber_hl")
                      .toString());
          if (firstCibilScore >= 600) {
            JSONObject row = new JSONObject();
            row.put("r2cUserType_hl", 1);
            row.put(
                "r2cApplicantNames_hl",
                cmmnRuntimeService.getVariable(caseInstanceId, "applicant1Name_hl"));
            row.put("r2cIdentificationCode_hl", "R2C04");
            row.put(
                "r2cDescription_hl",
                "IF Multiple CIR reports found:\n"
                    + "All CIBIL scores >= X, Or, \n"
                    + "First CIBIL score >= X, other CIBIL scores <X \n"
                    + "Where:\n"
                    + "X = Threshold = 600");
            row.put(
                "r2cRecommendedNextStep_hl",
                "Review all CIR reports corresponding to the customer and match obligations between CIR reports and bank statements. Where required, update the obligation amount\n"
                    + "\n"
                    + "Where EMI is not found in bank statement, request additional bank statement from the customer");
            r2cTable.put(row);
          }
        }
      } catch (Exception e) {
        logger.info("Error while setting r2c codes i.e., R2C04");
        logger.info("Error message : {}", e.getMessage());
      }

      logger.info("--------- refertoCredit_hl (JsonArray) : {}", r2cTable);
      logger.info("--------- refertoCredit_hl (JsonArray) : {}", r2cTable);
      if (r2cTable.length() != 0) {
        cmmnRuntimeService.setVariable(caseInstanceId, "refertoCredit_hl", r2cTable.toString());
      } else {
        cmmnRuntimeService.setVariable(caseInstanceId, "refertoCredit_hl", "");
      }
      logger.info("--------- refertoCredit_hl (JsonArray) : {}", r2cTable);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return planItemInstance;
  }

  enum Code {
    R2C01,
    R2C02,
    R2C03,
    R2C04,
    R2C05,
    R2C06,
    R2C07,
    R2C08,
    R2C09,
    R2C10,
    R2C11,
    R2C12,
    R2C13,
    R2C14,
    R2C15,
    R2C16,
    R2C17,
    R2C18,
    R2C19,
    R2C20
  }

  //  public static JSONArray r2cTablePopulationTest() throws IOException {
  //    logger.info(" ---------------------- in r2cScenarioService------------------------");
  //
  //    int applicantCount = 2;
  //
  //    String response = FileUtils.readFileToString(new File("/home/kuliza-523/applicants.json"));
  //
  //
  //
  //    JSONArray applicants = CommonHelperFunctions.objectToJSONArray(response);
  //
  //    logger.info("------------ applicants: " + applicants);
  //
  //    JSONArray r2cTable = new JSONArray();
  //    try {
  //      String[] applicant_id = {"0", "107", "108"};
  //
  //      for (int i = 1; i <= applicantCount; i++) {
  //        int currentApplicantIndex = 0;
  //        for (int j = 0; j < applicants.length(); j++) {
  //          if
  // (applicant_id[i].equalsIgnoreCase(applicants.getJSONObject(j).get("applicant_id").toString()))
  // {
  //            currentApplicantIndex = j;
  //
  //            boolean nameMatched =
  //              Boolean.parseBoolean(
  //                applicants.getJSONObject(currentApplicantIndex).get("name_matched").toString());
  //            boolean dobMatched =
  //              Boolean.parseBoolean(
  //                applicants.getJSONObject(currentApplicantIndex).get("dob_matched").toString());
  //
  //            logger.info("nameMatched: {},  dobMatched : {} ", nameMatched, dobMatched);
  //            if (!nameMatched) {
  //              JSONObject row = new JSONObject();
  //              row.put("r2cUserType_hl", i);
  //              row.put(
  //                "r2cApplicantNames_hl",
  //
  // applicants.getJSONObject(currentApplicantIndex).get("applicant_name").toString());
  //              row.put("r2cIdentificationCode_hl", Code.R2C01.toString());
  //              row.put(
  //                "r2cDescription_hl",
  //                "Name match between Aadhaar name and PAN name < Defined threshold");
  //              row.put(
  //                "r2cRecommendedNextStep_hl",
  //                "Review the name on Aadhaar card and Pan card, confirm that dual name affidavit
  // is collected at the time of login");
  //              r2cTable.put(row);
  //            }
  //
  //            if (!dobMatched) {
  //              JSONObject row = new JSONObject();
  //              row.put("r2cUserType_hl", i);
  //              row.put(
  //                "r2cApplicantNames_hl",
  //
  // applicants.getJSONObject(currentApplicantIndex).get("applicant_name").toString());
  //              row.put("r2cIdentificationCode_hl", Code.R2C02.toString());
  //              row.put(
  //                "r2cDescription_hl",
  //                "Aadhaar captures full Date of Birth; and Date of Birth on Aadhaar is different
  // from that on PAN card");
  //              row.put(
  //                "r2cRecommendedNextStep_hl",
  //                "Review the Date of Birth as on Aadhaar card and Pan card, confirm that dual DOB
  // affidavit is collected at the time of login");
  //              r2cTable.put(row);
  //
  //          }
  //        }
  //
  //        }
  //      }
  //    } catch (Exception e) {
  //      e.printStackTrace();
  //    }
  //
  //    System.out.println("r2cTable: "+ r2cTable);
  //    return r2cTable;
  //  }

  //      public static void main(String[] args) throws JSONException, IOException {
  ////        String response = FileUtils.readFileToString(new
  ////   File("/home/kuliza-523/finalSubmit2.txt"));
  ////
  ////        System.out.println(" response: " + response);
  ////
  ////        JSONObject jsonObject = new JSONObject(response);
  ////
  ////        System.out.println("jsonObject : "+ jsonObject);
  ////        JSONArray applicants = new JSONArray();
  ////        applicants = jsonObject.getJSONArray("applicants");
  ////
  ////        System.out.println("applicant :"+ applicants);
  ////            Gson gson = new Gson();
  ////            Map<String,Object> attributes = gson.fromJson(gson.toJson(response),Map.class);
  ////            System.out.println("attributes: "+ attributes);
  ////              map.put("name", "anupam");
  ////              map.put("k1", "b");
  ////              map.put("k2", "c");
  ////              map.put("k3", "d");
  ////
  ////        System.out.println("map : " + map);
  ////              JSONObject object = new JSONObject(response);
  ////
  ////              System.out.println(object);
  ////
  ////        r2cTablePopulationTest();
  //
  //        String response = FileUtils.readFileToString(new
  // File("/home/kuliza-523/rTocTable.txt"));
  ////        System.out.println("response : "+ response);
  //
  //        String stringWithoutBraces = response.replaceAll("[\\[\\]\\{\\}]", "");
  //        System.out.println("without baracktes : "+ stringWithoutBraces);
  //        String[] array = stringWithoutBraces.split(",");
  //        for (int i = 0; i < array.length; i++) {
  //          array[i] = array[i].trim();
  //          if (!array[i].contains("=")) {
  //            array[i-1] = array[i-1] + array[i];
  //            array[i] = array[i].replace(array[i], "");
  //          }
  //        }
  //        Map<String, Object> map = new HashMap<>();
  //        JSONObject object = new JSONObject();
  //        JSONArray table = new JSONArray();
  //        for (String keyValue : array) {
  //          String[] pair;
  //          if (keyValue.contains("=")) {
  //            pair = keyValue.split("=");
  //            object.put(pair[0], pair[1]);
  //            if (pair[0].equals("key")) {
  //              table.put(object);
  //              object = new JSONObject();
  //            }
  //          }
  //        }
  //        System.out.println("Table :"+ table);
  //      }
}
