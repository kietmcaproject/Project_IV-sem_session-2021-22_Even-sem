package com.kuliza.workbench.pojo;

import com.kuliza.workbench.model.CibilRequestData;
import java.util.List;

public class CibilResponse {
  private String application_id;
  private List<CibilRequestData> data;

  public List<CibilRequestData> getData() {
    return data;
  }

  public void setData(List<CibilRequestData> data) {
    this.data = data;
  }

  public String getApplication_id() {
    return application_id;
  }

  public void setApplication_id(String application_id) {
    this.application_id = application_id;
  }
}
