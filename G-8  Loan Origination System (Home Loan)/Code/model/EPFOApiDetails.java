package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "epfo_api_details")
public class EPFOApiDetails extends BaseModel {
  @Column(unique = true)
  int applicationId;

  @Column(length = 4000)
  String request;

  @Column(columnDefinition = "TEXT")
  String response;

  String employmentFiStatus;

  public EPFOApiDetails() {}

  public EPFOApiDetails(int applicationId, String request, String response) {
    this.applicationId = applicationId;
    this.request = request;
    this.response = response;
  }

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

  public String getEmploymentFiStatus() {
    return employmentFiStatus;
  }

  public void setEmploymentFiStatus(String employmentFiStatus) {
    this.employmentFiStatus = employmentFiStatus;
  }
}
