package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "from26as_api_details")
public class Form26asApiDetails extends BaseModel {
  @Column(unique = true)
  int applicationId;

  @Column(length = 4000)
  String request;

  @Column(columnDefinition = "TEXT")
  String response;

  String requestId;

  boolean isTaxCredited;

  public Form26asApiDetails() {}

  public int getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(int applicationId) {
    this.applicationId = applicationId;
  }

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public boolean isTaxCredited() {
    return isTaxCredited;
  }

  public void setTaxCredited(boolean taxCredited) {
    isTaxCredited = taxCredited;
  }
}
