package com.kuliza.workbench.model;

import javax.persistence.Entity;

@Entity
public class MandateData extends BaseModel {
  String maximum_amount;
  String instrument_type;
  String first_collection_date;
  boolean is_recurring;
  String frequency;
  String management_category;
  String customer_name;
  String customer_account_number;
  String destination_bank_id;
  String destination_bank_name;
  String customer_account_type;

  public MandateData() {}

  public String getMaximum_amount() {
    return maximum_amount;
  }

  public void setMaximum_amount(String maximum_amount) {
    this.maximum_amount = maximum_amount;
  }

  public String getInstrument_type() {
    return instrument_type;
  }

  public void setInstrument_type(String instrument_type) {
    this.instrument_type = instrument_type;
  }

  public String getFirst_collection_date() {
    return first_collection_date;
  }

  public void setFirst_collection_date(String first_collection_date) {
    this.first_collection_date = first_collection_date;
  }

  public boolean isIs_recurring() {
    return is_recurring;
  }

  public void setIs_recurring(boolean is_recurring) {
    this.is_recurring = is_recurring;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public String getManagement_category() {
    return management_category;
  }

  public void setManagement_category(String management_category) {
    this.management_category = management_category;
  }

  public String getCustomer_name() {
    return customer_name;
  }

  public void setCustomer_name(String customer_name) {
    this.customer_name = customer_name;
  }

  public String getCustomer_account_number() {
    return customer_account_number;
  }

  public void setCustomer_account_number(String customer_account_number) {
    this.customer_account_number = customer_account_number;
  }

  public String getDestination_bank_id() {
    return destination_bank_id;
  }

  public void setDestination_bank_id(String destination_bank_id) {
    this.destination_bank_id = destination_bank_id;
  }

  public String getDestination_bank_name() {
    return destination_bank_name;
  }

  public void setDestination_bank_name(String destination_bank_name) {
    this.destination_bank_name = destination_bank_name;
  }

  public String getCustomer_account_type() {
    return customer_account_type;
  }

  public void setCustomer_account_type(String customer_account_type) {
    this.customer_account_type = customer_account_type;
  }
}
