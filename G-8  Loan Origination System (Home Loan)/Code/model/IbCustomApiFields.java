package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ib_custom_api")
public class IbCustomApiFields extends BaseModel {

  @Column(name = "slug_name")
  private String slugName;

  @Column(name = "api_name")
  private String apiName;

  @Column(name = "lan_no")
  private String lanNo;

  @Column(name = "request")
  private String request;

  public IbCustomApiFields() {}

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  public IbCustomApiFields(String slugName, String apiName, String lanNo, String request) {
    this.slugName = slugName;
    this.apiName = apiName;
    this.lanNo = lanNo;
    this.request = request;
  }

  public String getSlugName() {
    return slugName;
  }

  public void setSlugName(String slugName) {
    this.slugName = slugName;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getLanNo() {
    return lanNo;
  }

  public void setLanNo(String lanNo) {
    this.lanNo = lanNo;
  }
}
