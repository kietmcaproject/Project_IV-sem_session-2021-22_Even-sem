package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "emp_verif_details")
public class EmpVerifDetails extends BaseModel {
  @Column(unique = true)
  int applicationId;

  @Column(length = 4000)
  String request;

  @Column(columnDefinition = "TEXT")
  String response;

  public EmpVerifDetails() {}

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
}
