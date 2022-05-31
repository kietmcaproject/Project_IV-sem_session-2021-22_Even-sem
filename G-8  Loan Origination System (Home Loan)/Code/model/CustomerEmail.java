package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customer_email")
public class CustomerEmail extends BaseModel {
  @Column(unique = true)
  int applicationId;

  String reqId;

  String verification;

  String email;

  String employeeDetails;

  String customerVerified;

  boolean isApplicantEmailPresent;

  boolean isLinkTriggered;

  public CustomerEmail() {}

  public int getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(int applicationId) {
    this.applicationId = applicationId;
  }

  public String getReqId() {
    return reqId;
  }

  public void setReqId(String reqId) {
    this.reqId = reqId;
  }

  public String getVerification() {
    return verification;
  }

  public void setVerification(String verification) {
    this.verification = verification;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmployeeDetails() {
    return employeeDetails;
  }

  public void setEmployeeDetails(String employeeDetails) {
    this.employeeDetails = employeeDetails;
  }

  public String getCustomerVerified() {
    return customerVerified;
  }

  public void setCustomerVerified(String customerVerified) {
    this.customerVerified = customerVerified;
  }

  public boolean isApplicantEmailPresent() {
    return isApplicantEmailPresent;
  }

  public void setApplicantEmailPresent(boolean applicantEmailPresent) {
    isApplicantEmailPresent = applicantEmailPresent;
  }

  public boolean isLinkTriggered() {
    return isLinkTriggered;
  }

  public void setLinkTriggered(boolean linkTriggered) {
    isLinkTriggered = linkTriggered;
  }
}
