package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "Createmandate")
public class CreateMandate extends BaseModel {
  String customer_identifier;
  String auth_mode;
  String mandate_type;
  String corporate_config_id;

  @Column(unique = true)
  String processInstanceId;

  String enachStatus;
  boolean notifyCustomer;

  int reTriggerCount;
  String mandateId;

  @OneToOne(cascade = javax.persistence.CascadeType.ALL)
  @Cascade(CascadeType.ALL)
  MandateData mandate_data;

  public CreateMandate() {}

  public String getCustomer_identifier() {
    return customer_identifier;
  }

  public void setCustomer_identifier(String customer_identifier) {
    this.customer_identifier = customer_identifier;
  }

  public String getAuth_mode() {
    return auth_mode;
  }

  public void setAuth_mode(String auth_mode) {
    this.auth_mode = auth_mode;
  }

  public String getMandate_type() {
    return mandate_type;
  }

  public void setMandate_type(String mandate_type) {
    this.mandate_type = mandate_type;
  }

  public String getCorporate_config_id() {
    return corporate_config_id;
  }

  public void setCorporate_config_id(String corporate_config_id) {
    this.corporate_config_id = corporate_config_id;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getEnachStatus() {
    return enachStatus;
  }

  public void setEnachStatus(String enachStatus) {
    this.enachStatus = enachStatus;
  }

  public boolean isNotifyCustomer() {
    return notifyCustomer;
  }

  public void setNotifyCustomer(boolean notifyCustomer) {
    this.notifyCustomer = notifyCustomer;
  }

  public int getReTriggerCount() {
    return reTriggerCount;
  }

  public void setReTriggerCount(int reTriggerCount) {
    this.reTriggerCount = reTriggerCount;
  }

  public MandateData getMandate_data() {
    return mandate_data;
  }

  public void setMandate_data(MandateData mandate_data) {
    this.mandate_data = mandate_data;
  }

  public String getMandateId() {
    return mandateId;
  }

  public void setMandateId(String mandateId) {
    this.mandateId = mandateId;
  }
}
