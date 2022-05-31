package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sms_template")
public class SMSTemplate extends BaseModel {
  @Column(unique = true)
  private String smsIndex;

  private String message;

  public SMSTemplate() {}

  public SMSTemplate(String smsIndex, String message) {
    this.smsIndex = smsIndex;
    this.message = message;
  }

  public String getSmsIndex() {
    return smsIndex;
  }

  public void setSmsIndex(String smsIndex) {
    this.smsIndex = smsIndex;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
