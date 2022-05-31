package com.kuliza.workbench.model;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Fields {
  @NotNull(message = "Mobile Number is required")
  @NotEmpty(message = "Mobile Number should not be Empty")
  @Size(min = 10, max = 10, message = "Mobile Number must be of 10 digits")
  private String mobileNumber;

  @NotNull(message = "Inquiry Number is required")
  @NotEmpty(message = "Inquiry Number should not be Empty")
  private String inquiryNumber;

  private String lanNo;
  private String status;
  private String processInstanceId;
  private Map<String, Object> variables = new HashMap<>();

  public Fields() {}

  public Fields(
      String mobileNumber,
      String inquiryNumber,
      String lanNo,
      String status,
      Map<String, Object> variables) {
    this.mobileNumber = mobileNumber;
    this.inquiryNumber = inquiryNumber;
    this.lanNo = lanNo;
    this.status = status;
    this.variables = variables;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLanNo() {
    return lanNo;
  }

  public void setLanNo(String lanNo) {
    this.lanNo = lanNo;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getInquiryNumber() {
    return inquiryNumber;
  }

  public void setInquiryNumber(String inquiryNumber) {
    this.inquiryNumber = inquiryNumber;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }
}
