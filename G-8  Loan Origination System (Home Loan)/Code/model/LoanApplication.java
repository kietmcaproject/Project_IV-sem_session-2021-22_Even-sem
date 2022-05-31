package com.kuliza.workbench.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Table(name = "LoanApplication")
public class LoanApplication extends BaseModel {
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String lanNo;

  private String status;
  private String inquiryNumber;
  private String mobileNumber;
  private String processInstanceId;

  public LoanApplication() {}

  public LoanApplication(
      String lanNo,
      String status,
      String inquiryNumber,
      String mobileNumber,
      String processInstanceId) {
    this.lanNo = lanNo;
    this.status = status;
    this.inquiryNumber = inquiryNumber;
    this.mobileNumber = mobileNumber;
    this.processInstanceId = processInstanceId;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getLanNo() {
    return lanNo;
  }

  public void setLanNo(String lanNo) {
    this.lanNo = lanNo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getInquiryNumber() {
    return inquiryNumber;
  }

  public void setInquiryNumber(String inquiryNumber) {
    this.inquiryNumber = inquiryNumber;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LoanApplication that = (LoanApplication) o;
    return inquiryNumber.equals(that.inquiryNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inquiryNumber);
  }
}
