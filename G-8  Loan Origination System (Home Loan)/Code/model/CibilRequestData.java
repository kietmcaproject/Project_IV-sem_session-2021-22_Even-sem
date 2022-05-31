package com.kuliza.workbench.model;

public class CibilRequestData {
  private String id;
  private String applicant_type;
  private String relationship_with_primary_applicant;
  private String applicant_name;
  private String mobile;
  private String pan;
  private String pan_dob;
  private String pan_name;
  private String pan_father_name;
  private String pan_data_mode;
  private String pan_url;
  private boolean is_pan_valid;
  private String pan_ocr_api;
  private String pan_validation_api;
  private String kyc_request_id;
  private String kyc_request_created_at;
  private String aadhaar_number;
  private String aadhaar_name;
  private String aadhaar_dob;
  private String aadhaar_mobile;
  private String gender;
  private String aadhaar_data_mode;
  private String aadhar_address;
  private String aadhaar_front_url;
  private String aadhaar_back_url;
  private String aadhaar_ocr_api;
  private boolean current_address_same_as_aadhaar;
  private String current_address;
  private boolean name_matched;
  private boolean dob_matched;
  private boolean is_age_valid;
  private boolean pan_available;
  private String secondary_income;
  private String step;
  private String created_at;
  private String updated_at;
  private String fk_application;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getApplicant_type() {
    return applicant_type;
  }

  public void setApplicant_type(String applicant_type) {
    this.applicant_type = applicant_type;
  }

  public String getRelationship_with_primary_applicant() {
    return relationship_with_primary_applicant;
  }

  public void setRelationship_with_primary_applicant(String relationship_with_primary_applicant) {
    this.relationship_with_primary_applicant = relationship_with_primary_applicant;
  }

  public String getApplicant_name() {
    return applicant_name;
  }

  public void setApplicant_name(String applicant_name) {
    this.applicant_name = applicant_name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public String getPan_dob() {
    return pan_dob;
  }

  public void setPan_dob(String pan_dob) {
    this.pan_dob = pan_dob;
  }

  public String getPan_name() {
    return pan_name;
  }

  public void setPan_name(String pan_name) {
    this.pan_name = pan_name;
  }

  public String getPan_father_name() {
    return pan_father_name;
  }

  public void setPan_father_name(String pan_father_name) {
    this.pan_father_name = pan_father_name;
  }

  public String getPan_data_mode() {
    return pan_data_mode;
  }

  public void setPan_data_mode(String pan_data_mode) {
    this.pan_data_mode = pan_data_mode;
  }

  public String getPan_url() {
    return pan_url;
  }

  public void setPan_url(String pan_url) {
    this.pan_url = pan_url;
  }

  public boolean isIs_pan_valid() {
    return is_pan_valid;
  }

  public void setIs_pan_valid(boolean is_pan_valid) {
    this.is_pan_valid = is_pan_valid;
  }

  public String getPan_ocr_api() {
    return pan_ocr_api;
  }

  public void setPan_ocr_api(String pan_ocr_api) {
    this.pan_ocr_api = pan_ocr_api;
  }

  public String getPan_validation_api() {
    return pan_validation_api;
  }

  public void setPan_validation_api(String pan_validation_api) {
    this.pan_validation_api = pan_validation_api;
  }

  public String getKyc_request_id() {
    return kyc_request_id;
  }

  public void setKyc_request_id(String kyc_request_id) {
    this.kyc_request_id = kyc_request_id;
  }

  public String getKyc_request_created_at() {
    return kyc_request_created_at;
  }

  public void setKyc_request_created_at(String kyc_request_created_at) {
    this.kyc_request_created_at = kyc_request_created_at;
  }

  public String getAadhaar_number() {
    return aadhaar_number;
  }

  public void setAadhaar_number(String aadhaar_number) {
    this.aadhaar_number = aadhaar_number;
  }

  public String getAadhaar_name() {
    return aadhaar_name;
  }

  public void setAadhaar_name(String aadhaar_name) {
    this.aadhaar_name = aadhaar_name;
  }

  public String getAadhaar_dob() {
    return aadhaar_dob;
  }

  public void setAadhaar_dob(String aadhaar_dob) {
    this.aadhaar_dob = aadhaar_dob;
  }

  public String getAadhaar_mobile() {
    return aadhaar_mobile;
  }

  public void setAadhaar_mobile(String aadhaar_mobile) {
    this.aadhaar_mobile = aadhaar_mobile;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAadhaar_data_mode() {
    return aadhaar_data_mode;
  }

  public void setAadhaar_data_mode(String aadhaar_data_mode) {
    this.aadhaar_data_mode = aadhaar_data_mode;
  }

  public String getAadhar_address() {
    return aadhar_address;
  }

  public void setAadhar_address(String aadhar_address) {
    this.aadhar_address = aadhar_address;
  }

  public String getAadhaar_front_url() {
    return aadhaar_front_url;
  }

  public void setAadhaar_front_url(String aadhaar_front_url) {
    this.aadhaar_front_url = aadhaar_front_url;
  }

  public String getAadhaar_back_url() {
    return aadhaar_back_url;
  }

  public void setAadhaar_back_url(String aadhaar_back_url) {
    this.aadhaar_back_url = aadhaar_back_url;
  }

  public String getAadhaar_ocr_api() {
    return aadhaar_ocr_api;
  }

  public void setAadhaar_ocr_api(String aadhaar_ocr_api) {
    this.aadhaar_ocr_api = aadhaar_ocr_api;
  }

  public boolean isCurrent_address_same_as_aadhaar() {
    return current_address_same_as_aadhaar;
  }

  public void setCurrent_address_same_as_aadhaar(boolean current_address_same_as_aadhaar) {
    this.current_address_same_as_aadhaar = current_address_same_as_aadhaar;
  }

  public String getCurrent_address() {
    return current_address;
  }

  public void setCurrent_address(String current_address) {
    this.current_address = current_address;
  }

  public boolean isName_matched() {
    return name_matched;
  }

  public void setName_matched(boolean name_matched) {
    this.name_matched = name_matched;
  }

  public boolean isDob_matched() {
    return dob_matched;
  }

  public void setDob_matched(boolean dob_matched) {
    this.dob_matched = dob_matched;
  }

  public boolean isIs_age_valid() {
    return is_age_valid;
  }

  public void setIs_age_valid(boolean is_age_valid) {
    this.is_age_valid = is_age_valid;
  }

  public boolean isPan_available() {
    return pan_available;
  }

  public void setPan_available(boolean pan_available) {
    this.pan_available = pan_available;
  }

  public String getSecondary_income() {
    return secondary_income;
  }

  public void setSecondary_income(String secondary_income) {
    this.secondary_income = secondary_income;
  }

  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public void setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
  }

  public String getFk_application() {
    return fk_application;
  }

  public void setFk_application(String fk_application) {
    this.fk_application = fk_application;
  }
}
