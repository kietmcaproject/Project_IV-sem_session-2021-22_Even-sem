package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FinalSubmitApiService {
  private static final Logger logger = LoggerFactory.getLogger(FinalSubmitApiService.class);

  @Autowired private RuntimeService runtimeService;
  @Autowired private TaskService taskService;
  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;

  public com.kuliza.lending.common.pojo.ApiResponse finalSubmit(Map<String, Object> request)
      throws JSONException {
    PragatiAppDetails pragatiAppDetails =
        pragatiAppDetailsRepository.findByKulizaAppId(request.get("los_id").toString());
    logger.info("pragatiAppDetails :-{}", pragatiAppDetails.getProcessInstanceId());
    String los_id = pragatiAppDetails.getProcessInstanceId();
    runtimeService.setVariable(
        los_id, "finalSubmitRequestBody", CommonHelperFunctions.getJsonString(request));

    try {
      request = CommonHelperFunctions.fromJson(mappingVariables(request));
      runtimeService.setVariables(los_id, request);
      try {
        String customerNo = runtimeService.getVariable(los_id, "applicant1UcicId_hl").toString();
        logger.info("CUSTOMER NUMBER :-{}", customerNo);
        pragatiAppDetails.setUcic(customerNo);
        pragatiAppDetailsRepository.save(pragatiAppDetails);
      } catch (Exception e) {
      }
      List<Task> tasks =
          taskService
              .createTaskQuery()
              .processInstanceId(los_id)
              .active()
              .orderByTaskCreateTime()
              .desc()
              .list();
      taskService.complete(tasks.get(0).getId());
      return new ApiResponse(HttpStatus.OK, "Data Saved");
    } catch (Exception e) {
      e.printStackTrace();
      return new ApiResponse(HttpStatus.BAD_REQUEST, "Bad Request");
    }
  }

  public JSONObject mappingVariables(Map<String, Object> request) throws JSONException {
    JSONObject object = new JSONObject(new Gson().toJson(request));
    logger.info("request :- {}", object);
    String losId = object.get("los_id").toString();
    String newLosId = "LN" + losId.substring(2);
    object.put("loanId_hl", newLosId);
    String createdAt = object.get("created_at").toString();
    String date = createdAt.substring(0, 10);
    object.put("applicationCreationDate_hl", date);
    object.put("enquiryDate_hl", object.get("created_at").toString());
    object.put("isSentToTechApp_hl", "no");
    object.put("applicationId_hl", object.get("application_id"));
    //    JSONArray applicantsArray = object.getJSONArray("applicants");
    //    logger.info("applicantsArray : {}", applicantsArray);
    //    object.put("applicantsArray_hl", applicantsArray.toString());

    JSONObject basicDetails = object.getJSONObject("basic_details");
    int loanAmount = basicDetails.getJSONObject("loan_eligibility").getInt("loan_amount");
    object.put("loanAmount_hl", loanAmount);
    int tenureInMonths = basicDetails.getJSONObject("loan_eligibility").getInt("tenure_in_months");
    object.put("loanTenure_hl", tenureInMonths);
    double expectedInterestRate =
        basicDetails.getJSONObject("loan_eligibility").getInt("expected_interest_rate");
    object.put("expectedRoi_hl", expectedInterestRate);
    int grossSalary = basicDetails.getJSONObject("loan_eligibility").getInt("gross_salary");
    object.put("grossSalary_hl", grossSalary);
    object.put("salaryBand_hl", grossSalary);
    String maritalStatusOfPrimaryApplicant =
        basicDetails.get("marital_status_of_primary_applicant").toString();
    object.put("applicant1MaritalStatus_hl", maritalStatusOfPrimaryApplicant);

    JSONObject propertyDetails = object.getJSONObject("property_details");
    String end_use_of_loan = propertyDetails.get("end_use_of_loan").toString();
    object.put("endUseOfLoanString_hl", end_use_of_loan);
    logger.info("endUseOfLoanString_hl : {}", end_use_of_loan);
    String propertyAddress =
        propertyDetails.getJSONObject("property_address").get("address").toString();
    String districtOrCity =
        propertyDetails.getJSONObject("property_address").get("district_or_city").toString();
    object.put("propertyCity", districtOrCity);

    String state = propertyDetails.getJSONObject("property_address").get("state").toString();
    object.put("propertyState", state);

    int pinCode = propertyDetails.getJSONObject("property_address").getInt("pincode");
    object.put("propertyPinCode", pinCode);

    String fullPropertyAddress =
        propertyAddress + ", " + districtOrCity + ", " + state + ", " + pinCode;
    object.put("propertyAddress_hl", fullPropertyAddress);
    logger.info("fullPropertyAddresss : {}", fullPropertyAddress);

    JSONArray propertyTechVerification = new JSONArray();
    JSONArray propertyLegalVerification = new JSONArray();
    JSONObject propertyTechVerificationObject = new JSONObject();
    JSONObject propertyLegalVerificationObject = new JSONObject();
    propertyTechVerificationObject.put("propertyAddress_hl", fullPropertyAddress);
    propertyLegalVerificationObject.put("propertyAddress_hl", fullPropertyAddress);
    propertyLegalVerification.put(propertyLegalVerificationObject);
    propertyTechVerification.put(propertyTechVerificationObject);
    object.put("propertyTechVerification_hl", propertyTechVerification);
    object.put("propertyLegalVerification_hl", propertyTechVerification);

    String propertyOwner =
        CommonHelperFunctions.getStringValue(propertyDetails.get("property_owner"));
    object.put("propertyOwner_hl", propertyOwner);

    JSONArray applicant1PropertyDetails = new JSONArray();
    JSONObject applicant1Property = new JSONObject();
    String propertyType = propertyDetails.get("property_type").toString();
    applicant1Property.put("applicant1DocumentType_hl", propertyType);
    applicant1PropertyDetails.put(applicant1Property);
    object.put("applicant1PropertyDetails_hl", applicant1PropertyDetails);

    try {
      JSONObject location = object.getJSONObject("location");
      String address = location.get("address").toString();
      object.put("applicant1Location_hl", address);
      Double lat = location.getDouble("lat");
      object.put("applicant1Latitude_hl", lat);
      Double lng = location.getDouble("lng");
      object.put("applicant1Longitude_hl", lng);
    } catch (Exception e) {
    }

    JSONArray applicants = object.getJSONArray("applicants");
    int applicantCount = applicants.length();
    object.put("applicantCount_hl", applicantCount);
    logger.info("applicantCount :- {}", applicantCount);

    int count = 1;
    for (int i = 0; i < applicantCount; i++) {
      JSONObject applicant = (JSONObject) applicants.get(i);
      String applicantType = applicant.get("applicant_type").toString();

      object.put("applicant" + (i + 1) + "ApplicantType_hl", applicantType);
      try {
        JSONArray applicantBStatementDetails = new JSONArray();
        JSONObject applicantBStatement = new JSONObject();
        String applicantBankStatementUrl =
            applicant.getJSONObject("bank_statements").get("url").toString();
        applicantBStatement.put(
            "applicant" + (i + 1) + "6MBankStatement_hl", applicantBankStatementUrl);
        applicantBStatementDetails.put(applicantBStatement);
        object.put(
            "applicant" + (i + 1) + "BStatementDetails_hl", applicantBStatementDetails.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
      String applicantName = applicant.get("applicant_name").toString();
      object.put("applicant" + (i + 1) + "Name_hl", applicantName);
      try {
        String ucic = applicant.get("ucic").toString();
        object.put("applicant" + (i + 1) + "UcicId_hl", ucic);
      } catch (Exception e) {
        e.printStackTrace();
      }
      int applicantId = applicant.getInt("applicant_id");
      object.put("applicant" + (i + 1) + "_id", applicantId);
      String applicantMobile = applicant.get("mobile").toString();
      object.put("applicant" + (i + 1) + "MobileNumber_hl", applicantMobile);
      String applicantRelationshipWithPrimaryApplicant =
          applicant.get("relationship_with_primary_applicant").toString();
      object.put("applicant" + (i + 1) + "Relation_hl", applicantRelationshipWithPrimaryApplicant);
      try {
        String applicantOfficialEmailId =
            applicant.getJSONObject("employment_check").get("official_email_id").toString();
        object.put("applicant" + (i + 1) + "Email_hl", applicantOfficialEmailId);
        object.put("applicant" + (i + 1) + "OfficialEMail_hl", applicantOfficialEmailId);
        String applicantDesignation =
            applicant.getJSONObject("employment_check").get("designation").toString();
        object.put("applicant" + (i + 1) + "Designation_hl", applicantDesignation);
        String applicantEmployerName =
            applicant.getJSONObject("employment_check").get("employer_name").toString();
        object.put("applicant" + (i + 1) + "EmployerName_hl", applicantEmployerName);
        String applicantEmployerType =
            applicant.getJSONObject("employment_check").get("employer_type").toString();
        object.put("applicant" + (i + 1) + "Constitution_hl", applicantEmployerType);
        if (applicantEmployerType.equalsIgnoreCase("Pvt Ltd")
            || applicantEmployerType.equalsIgnoreCase("Public")) {
          object.put("is" + "Applicant" + (i + 1) + "GovtEmp", "Yes");
        }

        String applicantEmployerLocalAddress =
            applicant
                .getJSONObject("employment_check")
                .getJSONObject("employer_local_address")
                .get("address")
                .toString();
        String applicantEmployerDistrictOrCity =
            applicant
                .getJSONObject("employment_check")
                .getJSONObject("employer_local_address")
                .get("district_or_city")
                .toString();
        String applicantEmployerState =
            applicant
                .getJSONObject("employment_check")
                .getJSONObject("employer_local_address")
                .get("state")
                .toString();
        String applicantEmployerPinCode =
            applicant
                .getJSONObject("employment_check")
                .getJSONObject("employer_local_address")
                .get("pincode")
                .toString();
        String applicantFinalEmployerAddress =
            applicantEmployerLocalAddress
                + ", "
                + applicantEmployerDistrictOrCity
                + ", "
                + applicantEmployerState
                + ", "
                + applicantEmployerPinCode;
        object.put("applicant" + (i + 1) + "EmployerAddress_hl", applicantFinalEmployerAddress);
        Boolean applicant_26asAvailable =
            applicant.getJSONObject("employment_check").getBoolean("26as_available");
        object.put("applicant" + (i + 1) + "Form26AS_hl", applicant_26asAvailable);
        String employmentClass =
            applicant.getJSONObject("employment_check").get("employment_class").toString();
        object.put("employmentType1_app", employmentClass);
      } catch (Exception e) {
        e.printStackTrace();
      }
      String applicantGender = applicant.getJSONObject("aadhaar").get("gender").toString();
      object.put("applicant" + (i + 1) + "Gender_hl", applicantGender);
      String aadhaarName =
          CommonHelperFunctions.getStringValue(
              applicant.getJSONObject("aadhaar").get("aadhaar_name"));
      object.put("applicant" + (i + 1) + "AadhaarName_hl", aadhaarName);
      JSONArray applicantAadharDetails = new JSONArray();
      JSONObject applicantAadhar = new JSONObject();
      String applicantAadhaarName =
          applicant.getJSONObject("aadhaar").get("aadhaar_name").toString();
      applicantAadhar.put("applicant" + (i + 1) + "AadharName_hl", applicantAadhaarName);
      String applicantAadhaarNumber =
          applicant.getJSONObject("aadhaar").get("aadhaar_number").toString();
      applicantAadhar.put("applicant" + (i + 1) + "AadharNumber_hl", applicantAadhaarNumber);
      applicantAadharDetails.put(applicantAadhar);
      object.put("applicant" + (i + 1) + "AadharDetails_hl", applicantAadharDetails);
      try {
        JSONArray applicantPanDetails = new JSONArray();
        JSONObject applicantPan = new JSONObject();
        String applicantPanName = applicant.getJSONObject("pan").get("pan_name").toString();
        applicantPan.put("applicant" + (i + 1) + "PanName_hl", applicantPanName);
        String applicantPanNumber = applicant.getJSONObject("pan").get("pan").toString();
        applicantPan.put("applicant" + (i + 1) + "PanNumber_hl", applicantPanNumber);
        //      try {
        //        String applicantPanUrl = applicant.getJSONObject("pan").get("pan_url");
        //        applicantPan.put("applicant"+(i+1)+"PanCard_hl", applicantPanUrl);
        //      } catch (Exception e) {
        //        e.printStackTrace();
        //      }
        applicantPanDetails.put(applicantPan);
        object.put("applicant" + (i + 1) + "PanDetails_hl", applicantPanDetails);
      } catch (Exception e) {
        e.printStackTrace();
      }
      JSONArray applicantAddressDetails = new JSONArray();
      JSONObject applicantAddress = new JSONObject();
      String applicantCurrentAddress =
          applicant.getJSONObject("current_address").get("address").toString();
      String applicantCurrentAddressDistrict =
          applicant.getJSONObject("current_address").get("district_or_city").toString();
      String applicantCurrentAddressState =
          applicant.getJSONObject("current_address").get("state").toString();
      String applicantCurrentAddressPinCode =
          applicant.getJSONObject("current_address").get("pincode").toString();
      String applicantFinalCurrentAddress =
          applicantCurrentAddress
              + ", "
              + applicantCurrentAddressDistrict
              + ", "
              + applicantCurrentAddressState
              + ", "
              + applicantCurrentAddressPinCode;
      applicantAddress.put(
          "applicant" + (i + 1) + "CurrentAddress_hl", applicantFinalCurrentAddress);
      String applicantPermanentAddress =
          applicant
              .getJSONObject("residential_verification")
              .getJSONObject("permanent_address")
              .get("address")
              .toString();
      String applicantPermanentAddressPinCode =
          applicant
              .getJSONObject("residential_verification")
              .getJSONObject("permanent_address")
              .get("pincode")
              .toString();
      String applicantFinalPermanentAddress =
          applicantPermanentAddress + ", " + applicantPermanentAddressPinCode;
      applicantAddress.put(
          "applicant" + (i + 1) + "PermanentAddress_hl", applicantFinalPermanentAddress);
      String applicantCurrentAddressVintage =
          applicant
              .getJSONObject("residential_verification")
              .get("current_address_vintage")
              .toString();
      String years = "", months = "";
      int totalMonths = 0;
      double totalYears;
      applicantCurrentAddressVintage = applicantCurrentAddressVintage.replaceAll("\\s", "");
      if (applicantCurrentAddressVintage.contains("Y")) {
        years =
            applicantCurrentAddressVintage.substring(
                0, applicantCurrentAddressVintage.indexOf("Y"));
      }
      if (applicantCurrentAddressVintage.contains("M")) {
        months =
            applicantCurrentAddressVintage.substring(
                applicantCurrentAddressVintage.indexOf("s") + 1,
                applicantCurrentAddressVintage.indexOf("M"));
      }
      totalMonths = (Integer.parseInt(years) * 12) + Integer.parseInt(months);
      totalYears = (double) totalMonths / 12;
      applicantAddress.put(
          "applicant" + (i + 1) + "YearsAtCurrentResidency_hl", Math.round(totalYears));
      applicantAddressDetails.put(applicantAddress);
      object.put("applicant" + (i + 1) + "AddressDetails_hl", applicantAddressDetails);
      String applicantDependentsCounts =
          applicant.getJSONObject("residential_verification").get("dependents_counts").toString();
      object.put("applicant" + (i + 1) + "DependentsCount_hl", applicantDependentsCounts);
      object.put("applicant" + (i + 1) + "Dependents_hl", applicantDependentsCounts);
      String applicantJobNature =
          applicant.getJSONObject("residential_verification").get("job_nature").toString();
      object.put("applicant" + (i + 1) + "JobNature_hl", applicantJobNature);
      String applicantEducation =
          applicant.getJSONObject("residential_verification").get("education").toString();
      object.put("applicant" + (i + 1) + "EducationQualification_hl", applicantEducation);
      object.put("applicant" + (i + 1) + "currentresidenceinYears_hl", Math.round(totalYears));


      try {
        String bankName = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("bank_name"));
        String ifscCode = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("ifsc_code").toString());
        String micrCode = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("micr_code").toString());
        String branchName = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("branch_name").toString());
        String accountType = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("account_type").toString());
        String accountNumber = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("account_number").toString());
        object.put("applicant" + (i + 1) + "BankName_hl", bankName);
        object.put("applicant" + (i + 1) + "Ifsc_hl", ifscCode);
        object.put("applicant" + (i + 1) + "MicrNumber_hl", micrCode);
        object.put("applicant" + (i + 1) + "BranchName_hl", branchName);
        object.put("applicant" + (i + 1) + "AccountType_hl", accountType);
        object.put("applicant" + (i + 1) + "AccountNumber_hl", accountNumber);
      } catch (JSONException e) {
      }


      //      JSONArray applicantSalarySlipDetails1 = new JSONArray();
      //      JSONObject applicantSalarySlip = new JSONObject();
      //      String applicantPayslipsUrl = applicant.getJSONObject("payslips").get("url");
      //      applicantSalarySlip.put("applicant"+(i+1)+"SalarySlip_hl", applicantPayslipsUrl);
      //      applicantSalarySlipDetails1.put(applicantSalarySlip);
      //      object.put("applicantSalarySlipDetails1_hl", applicantSalarySlipDetails1);
      logger.info("ObjectForApplicant" + (i + 1) + " :- {}", object);

      if (applicantType.equals("Primary Applicant")) {
        object = saveDetails(object, applicant, applicantType, 0);
      }
    }

    for (int i = 0; i < applicantCount; i++) {
      JSONObject applicant = (JSONObject) applicants.get(i);
      String applicantType = applicant.get("applicant_type").toString();
      if (!applicantType.equals("Primary Applicant")) {
        object = saveDetails(object, applicant, applicantType, count);
        count++;
      }
    }

    JSONArray references = object.getJSONArray("references");
    int referenceCount = references.length();
    logger.info("referenceCount :- {}", applicantCount);
    for (int i = 0; i < referenceCount; i++) {
      JSONObject reference = (JSONObject) references.get(i);
      try {
        String referenceFullName = reference.get("full_name").toString();
        object.put("reference" + (i + 1) + "Name_hl", referenceFullName);
        String referenceResidentialAddress =
            reference.getJSONObject("residential_address").get("address").toString();
        object.put("reference" + (i + 1) + "ResidAddress_hl", referenceResidentialAddress);
        String referenceOfficeAddress =
            reference.getJSONObject("office_address").get("address").toString();
        object.put("reference" + (i + 1) + "OfficeAddress_hl", referenceOfficeAddress);
        String referenceMobileNumber = reference.get("mobile_number").toString();
        object.put("reference" + (i + 1) + "PhoneNumber_hl", referenceMobileNumber);
        String referenceRelationWithApplicant = reference.get("relation_with_applicant").toString();
        object.put("reference" + (i + 1) + "Relationship_hl", referenceRelationWithApplicant);
        String referenceKnownSince = reference.get("known_since").toString();
        object.put("reference" + (i + 1) + "KnownSince_hl", referenceKnownSince);
        String referenceOccupation = reference.get("occupation").toString();
        object.put("reference" + (i + 1) + "Occupation_hl", referenceOccupation);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      JSONObject imdFeeDetails = object.getJSONObject("imd_fee").getJSONObject("details");
      object.put("imdStatus_hl", imdFeeDetails.get("status").toString());
    } catch (JSONException e) {
    }

    JSONObject pdInput = object.getJSONObject("pd_input");
    int monthlyHouseholdIncome = pdInput.getInt("monthly_household_income");
    object.put("pdIncome_hl", monthlyHouseholdIncome);
    String anyonePoliticallyAffiliatedInFamily =
        pdInput.get("anyone_politically_affiliated_in_family").toString();
    object.put("pdPoliticalAffliation_hl", anyonePoliticallyAffiliatedInFamily);
    String employersSector = pdInput.get("employers_sector").toString();
    object.put("pdEmploymentSector_hl", employersSector);
    String employmentType = pdInput.get("employment_type").toString();
    object.put("pdEmploymentType_hl", employmentType);
    int monthlyDiscretionaryInvestments = pdInput.getInt("monthly_discretionary_investments");
    object.put("pdDiscInvestment_hl", monthlyDiscretionaryInvestments);
    int monthlyNonDiscretionaryInvestments =
        pdInput.getInt("monthly_non_discretionary_investments");
    object.put("pdNonDiscInvestment_hl", monthlyNonDiscretionaryInvestments);
    int totalNetWorth = pdInput.getInt("total_net_worth");
    object.put("pdMarketValue_hl", totalNetWorth);
    String contributionToPurchaseInPercent =
        pdInput.get("contribution_to_purchase_in_percent").toString();
    object.put("pdOwnContribution_hl", contributionToPurchaseInPercent);
    object.put("ownContribution_hl", contributionToPurchaseInPercent);
    String source_of_contribution = pdInput.get("source_of_contribution").toString();
    object.put("pdSource_hl", source_of_contribution);
    String severeMedicalConditionInFamily =
        pdInput.get("severe_medical_condition_in_family").toString();
    object.put("pdSevereCondition_hl", severeMedicalConditionInFamily);
    String religion = pdInput.get("religion").toString();
    object.put("pdReligion_hl", religion);
    String caste = pdInput.get("caste").toString();
    object.put("pdCaste_hl", caste);

    JSONObject inquiry = object.getJSONObject("inquiry");
    String source = inquiry.get("source").toString();
    object.put("source_hl", source);
    String status = inquiry.get("status").toString();
    object.put("enquiryStatus_hl", status);

    JSONObject branch = object.getJSONObject("inquiry").getJSONObject("branch");
    String branchName = branch.get("branch_name").toString();
    object.put("loanBranch_hl", branchName);
    object.put("branchName_hl", branchName.replaceAll("\\s", ""));

    //    PragatiAppDetails pragatiAppDetails = new PragatiAppDetails();
    //    pragatiAppDetails.setApplicationId(application_id);
    //    String los_id = (String) request.get("los_id");
    //    pragatiAppDetails.setLosId(los_id);
    //    //    pragatiAppDetails.setFinalSubmitRequest(object.toString());
    //    pragatiAppDetailsRepository.save(pragatiAppDetails);

    return object;
  }

  public JSONObject saveDetails(
      JSONObject object, JSONObject applicant, String applicantType, int i) throws JSONException {
    object.put("applicant" + (i + 1) + "ApplicantType_hl", applicantType);
    try {
      JSONArray applicantBStatementDetails = new JSONArray();
      JSONObject applicantBStatement = new JSONObject();
      String applicantBankStatementUrl =
          applicant.getJSONObject("bank_statements").get("url").toString();
      applicantBStatement.put(
          "applicant" + (i + 1) + "6MBankStatement_hl", applicantBankStatementUrl);
      applicantBStatementDetails.put(applicantBStatement);
      object.put(
          "applicant" + (i + 1) + "BStatementDetails_hl", applicantBStatementDetails.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    String applicantName = applicant.get("applicant_name").toString();
    object.put("applicant" + (i + 1) + "Name_hl", applicantName);
    try {
      String ucic = applicant.get("ucic").toString();
      object.put("applicant" + (i + 1) + "UcicId_hl", ucic);

    } catch (Exception e) {
      e.printStackTrace();
    }

    int applicantId = applicant.getInt("applicant_id");
    object.put("applicant" + (i + 1) + "_id", applicantId);
    String applicantMobile = applicant.get("mobile").toString();
    object.put("applicant" + (i + 1) + "MobileNumber_hl", applicantMobile);
    String applicantRelationshipWithPrimaryApplicant =
        applicant.get("relationship_with_primary_applicant").toString();
    object.put("applicant" + (i + 1) + "Relation_hl", applicantRelationshipWithPrimaryApplicant);
    try {
      String applicantOfficialEmailId =
          applicant.getJSONObject("employment_check").get("official_email_id").toString();
      object.put("applicant" + (i + 1) + "Email_hl", applicantOfficialEmailId);
      object.put("applicant" + (i + 1) + "OfficialEMail_hl", applicantOfficialEmailId);
      String applicantDesignation =
          applicant.getJSONObject("employment_check").get("designation").toString();
      object.put("applicant" + (i + 1) + "Designation_hl", applicantDesignation);
      String applicantEmployerName =
          applicant.getJSONObject("employment_check").get("employer_name").toString();
      object.put("applicant" + (i + 1) + "EmployerName_hl", applicantEmployerName);
      String applicantEmployerType =
          applicant.getJSONObject("employment_check").get("employer_type").toString();
      object.put("applicant" + (i + 1) + "Constitution_hl", applicantEmployerType);
      String applicantEmployerLocalAddress =
          applicant
              .getJSONObject("employment_check")
              .getJSONObject("employer_local_address")
              .get("address")
              .toString();
      String applicantEmployerDistrictOrCity =
          applicant
              .getJSONObject("employment_check")
              .getJSONObject("employer_local_address")
              .get("district_or_city")
              .toString();
      String applicantEmployerState =
          applicant
              .getJSONObject("employment_check")
              .getJSONObject("employer_local_address")
              .get("state")
              .toString();
      String applicantEmployerPinCode =
          applicant
              .getJSONObject("employment_check")
              .getJSONObject("employer_local_address")
              .get("pincode")
              .toString();
      String applicantFinalEmployerAddress =
          applicantEmployerLocalAddress
              + ", "
              + applicantEmployerDistrictOrCity
              + ", "
              + applicantEmployerState
              + ", "
              + applicantEmployerPinCode;
      object.put("applicant" + (i + 1) + "EmployerAddress_hl", applicantFinalEmployerAddress);
      Boolean applicant_26asAvailable =
          applicant.getJSONObject("employment_check").getBoolean("26as_available");
      object.put("applicant" + (i + 1) + "Form26AS_hl", applicant_26asAvailable);
      String employmentClass =
          applicant.getJSONObject("employment_check").get("employment_class").toString();
      object.put("employmentType" + (i + 1) + "_app", employmentClass);
      String totalExperience =
          applicant.getJSONObject("employment_check").get("total_experience").toString();
      String years = "", months = "";
      int totalMonths = 0;
      double totalYears;
      totalExperience = totalExperience.replaceAll("\\s", "");
      if (totalExperience.contains("Y")) {
        years = totalExperience.substring(0, totalExperience.indexOf("Y"));
      }
      if (totalExperience.contains("M")) {
        months =
            totalExperience.substring(
                totalExperience.indexOf("s") + 1, totalExperience.indexOf("M"));
      }
      totalMonths = (Integer.parseInt(years) * 12) + Integer.parseInt(months);
      totalYears = (double) totalMonths / 12;
      object.put("applicant" + (i + 1) + "workExperience_hl", Math.round(totalYears));
    } catch (Exception e) {
      e.printStackTrace();
    }
    String applicantGender = applicant.getJSONObject("aadhaar").get("gender").toString();
    object.put("applicant" + (i + 1) + "Gender_hl", applicantGender);
    String aadhaarName =
        CommonHelperFunctions.getStringValue(
            applicant.getJSONObject("aadhaar").get("aadhaar_name"));
    object.put("applicant" + (i + 1) + "AadhaarName_hl", aadhaarName);
    JSONArray applicantAadharDetails = new JSONArray();
    JSONObject applicantAadhar = new JSONObject();
    String applicantAadhaarName = applicant.getJSONObject("aadhaar").get("aadhaar_name").toString();
    applicantAadhar.put("applicant" + (i + 1) + "AadharName_hl", applicantAadhaarName);
    String applicantAadhaarNumber =
        applicant.getJSONObject("aadhaar").get("aadhaar_number").toString();
    applicantAadhar.put("applicant" + (i + 1) + "AadharNumber_hl", applicantAadhaarNumber);
    applicantAadharDetails.put(applicantAadhar);
    object.put("applicant" + (i + 1) + "AadharDetails_hl", applicantAadharDetails);
    try {
      JSONArray applicantPanDetails = new JSONArray();
      JSONObject applicantPan = new JSONObject();
      String applicantPanName = applicant.getJSONObject("pan").get("pan_name").toString();
      applicantPan.put("applicant" + (i + 1) + "PanName_hl", applicantPanName);
      String applicantPanNumber = applicant.getJSONObject("pan").get("pan").toString();
      applicantPan.put("applicant" + (i + 1) + "PanNumber_hl", applicantPanNumber);
      //      try {
      //        String applicantPanUrl = applicant.getJSONObject("pan").get("pan_url");
      //        applicantPan.put("applicant"+(i+1)+"PanCard_hl", applicantPanUrl);
      //      } catch (Exception e) {
      //        e.printStackTrace();
      //      }
      applicantPanDetails.put(applicantPan);
      object.put("applicant" + (i + 1) + "PanDetails_hl", applicantPanDetails);
    } catch (Exception e) {
      e.printStackTrace();
    }
    JSONArray applicantAddressDetails = new JSONArray();
    JSONObject applicantAddress = new JSONObject();
    String applicantCurrentAddress =
        applicant.getJSONObject("current_address").get("address").toString();
    String applicantCurrentAddressDistrict =
        applicant.getJSONObject("current_address").get("district_or_city").toString();
    String applicantCurrentAddressState =
        applicant.getJSONObject("current_address").get("state").toString();
    String applicantCurrentAddressPinCode =
        applicant.getJSONObject("current_address").get("pincode").toString();
    String applicantFinalCurrentAddress =
        applicantCurrentAddress
            + ", "
            + applicantCurrentAddressDistrict
            + ", "
            + applicantCurrentAddressState
            + ", "
            + applicantCurrentAddressPinCode;
    applicantAddress.put("applicant" + (i + 1) + "CurrentAddress_hl", applicantFinalCurrentAddress);
    String applicantPermanentAddress =
        applicant
            .getJSONObject("residential_verification")
            .getJSONObject("permanent_address")
            .get("address")
            .toString();
    String applicantPermanentAddressPinCode =
        applicant
            .getJSONObject("residential_verification")
            .getJSONObject("permanent_address")
            .get("pincode")
            .toString();
    String applicantFinalPermanentAddress =
        applicantPermanentAddress + ", " + applicantPermanentAddressPinCode;
    applicantAddress.put(
        "applicant" + (i + 1) + "PermanentAddress_hl", applicantFinalPermanentAddress);
    String applicantCurrentAddressVintage =
        applicant
            .getJSONObject("residential_verification")
            .get("current_address_vintage")
            .toString();
    String years = "", months = "";
    int totalMonths = 0;
    double totalYears;
    applicantCurrentAddressVintage = applicantCurrentAddressVintage.replaceAll("\\s", "");
    if (applicantCurrentAddressVintage.contains("Y")) {
      years =
          applicantCurrentAddressVintage.substring(0, applicantCurrentAddressVintage.indexOf("Y"));
    }
    if (applicantCurrentAddressVintage.contains("M")) {
      months =
          applicantCurrentAddressVintage.substring(
              applicantCurrentAddressVintage.indexOf("s") + 1,
              applicantCurrentAddressVintage.indexOf("M"));
    }
    totalMonths = (Integer.parseInt(years) * 12) + Integer.parseInt(months);
    totalYears = (double) totalMonths / 12;
    applicantAddress.put(
        "applicant" + (i + 1) + "YearsAtCurrentResidency_hl", Math.round(totalYears));
    applicantAddressDetails.put(applicantAddress);
    object.put("applicant" + (i + 1) + "AddressDetails_hl", applicantAddressDetails);
    String applicantDependentsCounts =
        applicant.getJSONObject("residential_verification").get("dependents_counts").toString();
    object.put("applicant" + (i + 1) + "DependentsCount_hl", applicantDependentsCounts);
    object.put("applicant" + (i + 1) + "Dependents_hl", applicantDependentsCounts);
    String applicantJobNature =
        applicant.getJSONObject("residential_verification").get("job_nature").toString();
    object.put("applicant" + (i + 1) + "JobNature_hl", applicantJobNature);
    String applicantEducation =
        applicant.getJSONObject("residential_verification").get("education").toString();
    object.put("applicant" + (i + 1) + "EducationQualification_hl", applicantEducation);
    object.put("applicant" + (i + 1) + "currentresidenceinYears_hl", Math.round(totalYears));


    try {
      String bankName = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("bank_name"));
      String ifscCode = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("ifsc_code").toString());
      String micrCode = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("micr_code").toString());
      String branchName = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("branch_name").toString());
      String accountType = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("account_type").toString());
      String accountNumber = CommonHelperFunctions.getStringValue(applicant.getJSONObject("bank_account_details").get("account_number").toString());
      object.put("applicant" + (i + 1) + "BankName_hl", bankName);
      object.put("applicant" + (i + 1) + "Ifsc_hl", ifscCode);
      object.put("applicant" + (i + 1) + "MicrNumber_hl", micrCode);
      object.put("applicant" + (i + 1) + "BranchName_hl", branchName);
      object.put("applicant" + (i + 1) + "AccountType_hl", accountType);
      object.put("applicant" + (i + 1) + "AccountNumber_hl", accountNumber);
    } catch (JSONException e) {
    }

    //      JSONArray applicantSalarySlipDetails1 = new JSONArray();
    //      JSONObject applicantSalarySlip = new JSONObject();
    //      String applicantPayslipsUrl = applicant.getJSONObject("payslips").get("url");
    //      applicantSalarySlip.put("applicant"+(i+1)+"SalarySlip_hl", applicantPayslipsUrl);
    //      applicantSalarySlipDetails1.put(applicantSalarySlip);
    //      object.put("applicantSalarySlipDetails1_hl", applicantSalarySlipDetails1);
    logger.info("ObjectForApplicant" + (i + 1) + " :- {}", object);

    try {
      if (applicantType.equalsIgnoreCase("Non-Financial Co-Applicant")) {
        object.put("isApplicant" + (i + 1) + "NF", "yes");
      } else {
        object.put("isApplicant" + (i + 1) + "NF", "no");
      }
    } catch (Exception e) {
    }

    return object;
  }

  //  public static void main(String[] args) throws IOException, JSONException {
  //    String var = "0 Years 6 Months";
  //    String years = "", months = "";
  //    int totalMonths = 0;
  //    double totalYears;
  //    var = var.replaceAll("\\s", "");
  //    if(var.contains("Y")) {
  //      years = var.substring(0, var.indexOf("Y"));
  //    }
  //    if (var.contains("M")) {
  //      months = var.substring(var.indexOf("s") + 1, var.indexOf("M"));
  //    }
  //    totalMonths = (Integer.parseInt(years) * 12) + Integer.parseInt(months);
  //    totalYears = (double)totalMonths / 12;
  //    System.out.println(Math.round(totalYears));
  //  }
  //            String response =
  //                    FileUtils.readFileToString(new
  // File("/home/kuliza-407/Desktop/finalsubmitjson"));
  //            HashMap<String, Object> map = new HashMap<String, Object>();
  //
  //            JSONObject object = new JSONObject(response);
  //            String createdAt = object.get("created_at");
  //            String date = createdAt.substring(0, 10);
  //            System.out.println(date);
  //
  //          }

  ////          JSONArray applicants = object.getJSONArray("applicants");
  ////          JSONObject applicant1 = (JSONObject) applicants.get(0);
  //
  //          JSONObject propertyDetails = object.getJSONObject("property_details");
  //          String end_use_of_loan = propertyDetails.get("end_use_of_loan");
  //          object.put("loanPurpose_hl", end_use_of_loan);
  //          String propertyAddress =
  // propertyDetails.getJSONObject("property_address").get("address");
  //          String districtOrCity =
  // propertyDetails.getJSONObject("property_address").get("district_or_city");
  //          String state = propertyDetails.getJSONObject("property_address").get("state");
  //          int pinCode = propertyDetails.getJSONObject("property_address").getInt("pincode");
  ////          String fullPropertyAddress = propertyAddress + ", " + districtOrCity + ", " + state
  // + ", " + pinCode;
  //          String fullPropertyAddress = new
  // StringBuilder(propertyAddress).append(districtOrCity).append(state).append(pinCode).toString();
  //
  //          System.out.println(fullPropertyAddress);
  //          //    Iterator<?> keys = jsonObject.keys();
  //          //
  //          //    while( keys.hasNext() ){
  //          //      String key = (String)keys.next();
  //          //      String value = jsonObject.get(key);
  //          //      map.put(key, value);
  //          //
  //          //    }
  //
  //          //    JSONObject object = new JSONObject(new Gson().toJson(map));
  //          //    object.getJSONObject("property_details");
  //
  //          //    JSONObject propertyDetails =  new JSONObject (object.get("los_id"));
  //
  //          //    logger.info("request :- {}", request);
  //          //    JSONObject basicDetails = (JSONObject) map.get("basic_details");
  //
  //          //    JSONObject data = jsonObject.getJSONObject("basic_details");
  //          //    JSONObject r1 = (JSONObject) jsonObject.getJSONArray("applicants").get(0);
  //          //    String data =
  //
  //
  ////          String x = String.valueOf(applicants.length());
  ////     JSONObject jsonObject1 = new JSONObject();
  ////          JSONArray data = new JSONArray();
  ////          JSONObject applicantName = new JSONObject();
  ////          for (int i = 0; i < applicants.length(); i++) {
  ////              JSONObject applicant = (JSONObject) applicants.get(i);
  ////              applicantName.put("name", applicant.get("applicant_name"));
  ////              data.put(applicantName);
  ////              jsonObject1.put("data", data);
  ////          }

}
