package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Posidex")
public class Posidex extends BaseModel {

  private String assetSrNo;

  @Column(length = 6000)
  private String posidexResponse;

  private String caseInstanceId;

  public Posidex() {}

  public Posidex(String assetSrNo, String posidexResponse) {
    this.assetSrNo = assetSrNo;
    this.posidexResponse = posidexResponse;
  }

  public String getAssetSrNo() {
    return assetSrNo;
  }

  public void setAssetSrNo(String assetSrNo) {
    this.assetSrNo = assetSrNo;
  }

  public String getPosidexResponse() {
    return posidexResponse;
  }

  public void setPosidexResponse(String posidexResponse) {
    this.posidexResponse = posidexResponse;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }
}
