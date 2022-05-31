package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.ArrayList;
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

@Service("LocalityCheckService")
public class LocalityCheckService {
  private static final Logger logger = LoggerFactory.getLogger(LocalityCheckService.class);

  @Autowired RuntimeService runtimeService;
  private static final int FIFTY_THOUSAND = 50000;
  private static final int THIRTY_THOUSAND = 30000;
  private static final int SIX_LAKHS = 600000;

  public DelegateExecution checker(DelegateExecution execution) throws JSONException {
    String processInstanceId = execution.getProcessInstanceId();
    Map<String, Object> variables = execution.getVariables();

    String customerLocality = variables.getOrDefault("customerLocalityTech_hl", "").toString();
    int monthlyHouseholdIncome = Integer.parseInt(variables.get("pdIncomeCm_hl").toString());
    String municipalCorporation = variables.get("approvalAuthority_hl").toString();

    List<String> municipalList = new ArrayList<>();
    municipalList.add("Municipal Corporation");
    municipalList.add("Municipal Council");
    municipalList.add("Nagar Parishad");
    municipalList.add("Nagar Palika");
    municipalList.add("Society");
    if (municipalList.contains(municipalCorporation)) {
      runtimeService.setVariable(
          processInstanceId, "approvalAuthorityClassification_hl", "Municipal");
    }

    List<String> gramPanchayatList = new ArrayList<>();
    gramPanchayatList.add("Lal Dora");
    gramPanchayatList.add("Gram Panchayat");
    if (gramPanchayatList.contains(municipalCorporation)) {
      runtimeService.setVariable(
          processInstanceId, "approvalAuthorityClassification_hl", "Gram Panchayat");
    }

    if (customerLocality.equalsIgnoreCase("Urban")) {
      if (monthlyHouseholdIncome < FIFTY_THOUSAND) {
        boolean isMunicipalCorp = false;
        if (municipalList.contains(municipalCorporation)) {
          isMunicipalCorp = true;
        }
        boolean isBuildingPlan =
            variables.get("approvedBuildingDesignPlan_hl").toString().equalsIgnoreCase("yes");
        boolean isNACertiAvail =
            variables.get("naConversionCertificate_hl").toString().equalsIgnoreCase("yes");
        boolean isPMAYeligible =
            variables.get("propertyEligiblePMAYCLSS_hl").toString().equalsIgnoreCase("yes");
        boolean isAvgSurDevPercent =
            Integer.parseInt(variables.get("averageSurroundingDevelopment_hl").toString()) > 80;

        logger.info(
            "------ isMunicipalCorp : {} \n ,------ isBuildingPlan : {} \n ,------ isNACertiAvail : {} \n ,------ isPMAYeligible : {} \n ,------ isAvgSurDevPercent : {} \n ",
            isMunicipalCorp,
            isBuildingPlan,
            isNACertiAvail,
            isPMAYeligible,
            isAvgSurDevPercent);
        if (isMunicipalCorp
            && isBuildingPlan
            && isNACertiAvail
            && isPMAYeligible
            && isAvgSurDevPercent) {
          runtimeService.setVariable(processInstanceId, "localityCheckAHL", "Go Ahead");
        }
      } else {
        runtimeService.setVariable(processInstanceId, "localityCheckAHL", "Non prime");
      }
    }

    //    String finalSubmitRequestBody =
    // execution.getVariable("finalSubmitRequestBody").toString();
    //    logger.info("------------- finalSubmitRequestBody : {} ", finalSubmitRequestBody);
    //    JSONObject finalSubmitJsonObj = new JSONObject(finalSubmitRequestBody);
    String propertyOwner =
        CommonHelperFunctions.getStringValue(execution.getVariable("propertyOwner_hl"));

    int applicantCount =
        CommonHelperFunctions.getIntegerValue(execution.getVariable("applicantCount_hl"));
    String applicantType = "";
    String gender = "";
    String aadhaarName = "";
    String applicantName = "";
    for (int i = 0; i < applicantCount; i++) {
      applicantType = execution.getVariable("applicant" + (i + 1) + "ApplicantType_hl").toString();
      gender = execution.getVariable("applicant" + (i + 1) + "Gender_hl").toString();
      aadhaarName = execution.getVariable("applicant" + (i + 1) + "AadhaarName_hl").toString();
      applicantName = execution.getVariable("applicant" + (i + 1) + "Name_hl").toString();
    }
    boolean isNACertiAvail =
        variables.get("naConversionCertificate_hl").toString().equalsIgnoreCase("yes");
    boolean isAvgSurDevPercent =
        Integer.parseInt(variables.get("averageSurroundingDevelopment_hl").toString()) > 50;
    if (customerLocality.equalsIgnoreCase("Rural")) {
      if (monthlyHouseholdIncome < THIRTY_THOUSAND
          && propertyOwner.equalsIgnoreCase("Main applicant of loan")
          && applicantType.equalsIgnoreCase("Primary Applicant")
          && gender.equalsIgnoreCase("Female")
          && applicantName.equalsIgnoreCase(aadhaarName)
          && (monthlyHouseholdIncome * 12) < SIX_LAKHS) {
        runtimeService.setVariable(processInstanceId, "isWomenPropertyOwner_hl", "yes");
        if (isNACertiAvail && isAvgSurDevPercent) {
          runtimeService.setVariable(processInstanceId, "localityCheckAHL", "Go Ahead");
        } else {
          runtimeService.setVariable(processInstanceId, "localityCheckAHL", "Non prime");
        }
      } else {
        runtimeService.setVariable(processInstanceId, "localityCheckAHL", "Non prime");
      }
    }

    String cVerification = variables.get("applicant1CirVerification_hl").toString();
    JSONArray cirVerification = new JSONArray(cVerification);
    JSONObject cirObject = cirVerification.getJSONObject(0);
    int cibilScore = Integer.parseInt(cirObject.get("applicant1CirScore_hl").toString());
    String employmentClass = variables.getOrDefault("employmentType1_app", "").toString();
    String grossSalary = variables.getOrDefault("grossSalary_hl", "").toString();
    String jobNature = variables.getOrDefault("applicant1JobNature_hl", "").toString();
    String totalExperience = variables.getOrDefault("applicant1workExperience_hl", "").toString();
    String isApplicant1GovtEmp = variables.getOrDefault("is" + "Applicant1GovtEmp", "").toString();

    if (cibilScore > 700) {
      runtimeService.setVariable(processInstanceId, "employmentCheckAHL", "Go Ahead");
    } else {
      runtimeService.setVariable(processInstanceId, "employmentCheckAHL", "Non Prime");
    }

    //    String employmentCheckAHL = variables.get("employmentCheckAHL").toString();

    //    if (employmentCheckAHL.equalsIgnoreCase("Go Ahead")) {
    if (cibilScore > 700) {
      if (isApplicant1GovtEmp.equalsIgnoreCase("No")
          && employmentClass.equalsIgnoreCase("class 3")
          && Integer.parseInt(grossSalary) > THIRTY_THOUSAND
          && cibilScore > 700) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 1");
      }

      if (isApplicant1GovtEmp.equalsIgnoreCase("No")
          && employmentClass.equalsIgnoreCase("class 3")
          && Integer.parseInt(grossSalary) < THIRTY_THOUSAND
          && cibilScore > 700) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 2");
      }

      if (isApplicant1GovtEmp.equalsIgnoreCase("No")
          && employmentClass.equalsIgnoreCase("class 4")
          && cibilScore > 700) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 2");
      }
      if (jobNature.equalsIgnoreCase("Skilled")
          && isApplicant1GovtEmp.equalsIgnoreCase("Yes")
          && Integer.parseInt(grossSalary) > THIRTY_THOUSAND
          && Integer.parseInt(totalExperience) >= 1) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 2");
      }

      if (jobNature.equalsIgnoreCase("Skilled")
          && isApplicant1GovtEmp.equalsIgnoreCase("No")
          && Integer.parseInt(grossSalary) < THIRTY_THOUSAND
          && Integer.parseInt(totalExperience) >= 1) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 3");
      }

      if ((!jobNature.equalsIgnoreCase("Skilled"))
          && isApplicant1GovtEmp.equalsIgnoreCase("No")
          && Integer.parseInt(totalExperience) >= 3) {
        runtimeService.setVariable(processInstanceId, "ahlCustomerSchemeString_hl", "CAT 3");
      }
    }

    String loanPurpose = variables.get("endUseOfLoanString_hl").toString();
    boolean isLoanPurposeA = loanPurpose.equalsIgnoreCase("Purchase of House for Self-usage");
    boolean isLoanPurposeB = loanPurpose.equalsIgnoreCase("Purchase of House for investment");
    boolean isLoanPurposeC = loanPurpose.equalsIgnoreCase("Self-construction");
    boolean isLoanPurposeD = loanPurpose.equalsIgnoreCase("Plot Purchase + Construction");

    if (customerLocality.equalsIgnoreCase("Urban")) {
      if (isLoanPurposeA || isLoanPurposeB || isLoanPurposeC || isLoanPurposeD) {
        runtimeService.setVariable(processInstanceId, "loanPurposeCheckAHL", "Go Ahead");
      } else {
        runtimeService.setVariable(processInstanceId, "loanPurposeCheckAHL", "Non prime");
      }
    }

    if (customerLocality.equalsIgnoreCase("Rural")) {
      if (isLoanPurposeC || isLoanPurposeD) {
        runtimeService.setVariable(processInstanceId, "loanPurposeCheckAHL", "Go Ahead");
      } else {
        runtimeService.setVariable(processInstanceId, "loanPurposeCheckAHL", "Non prime");
      }
    }
    return execution;
  }
}
