package com.kuliza.workbench.model;

import java.util.List;

public class FinalCibilRequest {
  String applicationId;
  List<CibilRequest> data;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public List<CibilRequest> getData() {
    return data;
  }

  public void setData(List<CibilRequest> data) {
    this.data = data;
  }
}
