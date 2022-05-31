package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "kscan_api_details")
public class KScanApiDetails extends BaseModel {
  @Column(unique = true)
  int applicationId;

  @Column(length = 4000)
  String request;

  @Column(columnDefinition = "TEXT")
  String response;

  boolean isUniqueMatch;

  boolean isNameFound;

  @Column(length = 4000)
  String empSearchRequest;

  @Column(columnDefinition = "TEXT")
  String empSearchResponse;

  String matchScore;

  public KScanApiDetails() {}

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

  public boolean isUniqueMatch() {
    return isUniqueMatch;
  }

  public void setUniqueMatch(boolean uniqueMatch) {
    isUniqueMatch = uniqueMatch;
  }

  public boolean isNameFound() {
    return isNameFound;
  }

  public void setNameFound(boolean nameFound) {
    isNameFound = nameFound;
  }

  public String getEmpSearchRequest() {
    return empSearchRequest;
  }

  public void setEmpSearchRequest(String empSearchRequest) {
    this.empSearchRequest = empSearchRequest;
  }

  public String getEmpSearchResponse() {
    return empSearchResponse;
  }

  public void setEmpSearchResponse(String empSearchResponse) {
    this.empSearchResponse = empSearchResponse;
  }

  public String getMatchScore() {
    return matchScore;
  }

  public void setMatchScore(String matchScore) {
    this.matchScore = matchScore;
  }
}
