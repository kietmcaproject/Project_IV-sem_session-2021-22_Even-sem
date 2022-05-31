package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PragatiDetails")
public class PragatiAppDetails extends BaseModel {
  @Column(unique = true)
  int applicationId;

  String processInstanceId;
  String kulizaAppId;
  String ucic;
  String caseInstanceId;

  public PragatiAppDetails() {}

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }

  public int getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(int applicationId) {
    this.applicationId = applicationId;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getKulizaAppId() {
    return kulizaAppId;
  }

  public void setKulizaAppId(String kulizaAppId) {
    this.kulizaAppId = kulizaAppId;
  }

  public String getUcic() {
    return ucic;
  }

  public void setUcic(String ucic) {
    this.ucic = ucic;
  }
}
