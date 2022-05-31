package com.kuliza.workbench.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TechAppRequest {

  @NotNull(message = "Application Number is null")
  @NotEmpty(message = "Application Number is mandatory")
  private String application_number;

  @NotNull(message = "Collateral Id is null")
  @NotEmpty(message = "Collateral Id is mandatory")
  private String collateral_id;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @NotNull(message = "Valuation Date is mandatory")
  private Date val_date;

  @Size(max = 30)
  @NotEmpty(message = "Valuation Report no is mandatory")
  private String val_report;

  @Size(max = 50)
  @NotEmpty(message = "Valuer name is mandatory")
  private String val_name;

  @NotEmpty(message = "Final status is mandatory")
  private String final_status;

  @Size(max = 3000)
  private String remarks;

  @Digits(integer = 9, fraction = 2)
  @NotNull(message = "Land Area  (in sq. ft.) is mandatory")
  private float land_area_sqft;

  @Digits(integer = 15, fraction = 2)
  @NotNull(message = "Land Rate (Rs/sq.ft.) is mandatory")
  private float land_rate_sqft;

  @Digits(integer = 9, fraction = 2)
  @NotNull(message = "Carpet Area (in sq. ft.) is mandatory")
  private float carpet_area_sqft;

  @Digits(integer = 9, fraction = 2)
  @NotNull(message = "Construction Area / Built Up / Super Built Up Area (in sq.ft.) is mandatory")
  private float con_builtup_area_sqft;

  @Digits(integer = 15, fraction = 2)
  @NotNull(message = "Other amenities Value / Car parking / PLC is mandatory")
  private float other_amenities_value;

  @Digits(integer = 15, fraction = 2)
  @NotNull(
      message = "Construction Area / Built Up / Super Built Up Area Rate (Rs/(sq.ft.) is mandatory")
  private float con_builtup_area_value;

  @Size(max = 150)
  @NotEmpty(message = "Stage of Construction is mandatory")
  private String stage_of_construction;

  @Digits(integer = 15, fraction = 2)
  @NotNull(message = "Total Valuation is mandatory")
  private float total_valuation;

  @Digits(integer = 3, fraction = 2)
  @NotNull(message = "% of Recommendation is mandatory")
  @Max(100)
  private float perc_recomm;

  @Digits(integer = 3, fraction = 2)
  @NotNull(message = "% of completion is mandatory")
  @Max(100)
  private float perc_compl;

  @Size(max = 300)
  @NotEmpty(message = "Any Deviation is mandatory")
  private String any_deviation;

  @Size(max = 300)
  @NotEmpty(message = "Property Visited is mandatory")
  private String property_visited;

  private String doc_name;

  @Pattern(
      regexp = "PDF",
      flags = Pattern.Flag.CASE_INSENSITIVE,
      message = "Document Type is not compatible")
  private String doc_type;

  private String doc_content;

  public String getApplication_number() {
    return application_number;
  }

  public void setApplication_number(String application_number) {
    this.application_number = application_number;
  }

  public String getCollateral_id() {
    return collateral_id;
  }

  public void setCollateral_id(String collateral_id) {
    this.collateral_id = collateral_id;
  }

  public Date getVal_date() {
    return val_date;
  }

  public void setVal_date(Date val_date) {
    this.val_date = val_date;
  }

  public String getVal_report() {
    return val_report;
  }

  public void setVal_report(String val_report) {
    this.val_report = val_report;
  }

  public String getVal_name() {
    return val_name;
  }

  public void setVal_name(String val_name) {
    this.val_name = val_name;
  }

  public String getFinal_status() {
    return final_status;
  }

  public void setFinal_status(String final_status) {
    this.final_status = final_status;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public float getLand_area_sqft() {
    return land_area_sqft;
  }

  public void setLand_area_sqft(float land_area_sqft) {
    this.land_area_sqft = land_area_sqft;
  }

  public float getLand_rate_sqft() {
    return land_rate_sqft;
  }

  public void setLand_rate_sqft(float land_rate_sqft) {
    this.land_rate_sqft = land_rate_sqft;
  }

  public float getCarpet_area_sqft() {
    return carpet_area_sqft;
  }

  public void setCarpet_area_sqft(float carpet_area_sqft) {
    this.carpet_area_sqft = carpet_area_sqft;
  }

  public float getCon_builtup_area_sqft() {
    return con_builtup_area_sqft;
  }

  public void setCon_builtup_area_sqft(float con_builtup_area_sqft) {
    this.con_builtup_area_sqft = con_builtup_area_sqft;
  }

  public float getOther_amenities_value() {
    return other_amenities_value;
  }

  public void setOther_amenities_value(float other_amenities_value) {
    this.other_amenities_value = other_amenities_value;
  }

  public float getCon_builtup_area_value() {
    return con_builtup_area_value;
  }

  public void setCon_builtup_area_value(float con_builtup_area_value) {
    this.con_builtup_area_value = con_builtup_area_value;
  }

  public String getStage_of_construction() {
    return stage_of_construction;
  }

  public void setStage_of_construction(String stage_of_construction) {
    this.stage_of_construction = stage_of_construction;
  }

  public float getTotal_valuation() {
    return total_valuation;
  }

  public void setTotal_valuation(float total_valuation) {
    this.total_valuation = total_valuation;
  }

  public float getPerc_recomm() {
    return perc_recomm;
  }

  public void setPerc_recomm(float perc_recomm) {
    this.perc_recomm = perc_recomm;
  }

  public float getPerc_compl() {
    return perc_compl;
  }

  public void setPerc_compl(float perc_compl) {
    this.perc_compl = perc_compl;
  }

  public String getAny_deviation() {
    return any_deviation;
  }

  public void setAny_deviation(String any_deviation) {
    this.any_deviation = any_deviation;
  }

  public String getProperty_visited() {
    return property_visited;
  }

  public void setProperty_visited(String property_visited) {
    this.property_visited = property_visited;
  }

  public String getDoc_type() {
    return doc_type;
  }

  public void setDoc_type(String doc_type) {
    this.doc_type = doc_type;
  }

  public String getDoc_name() {
    return doc_name;
  }

  public void setDoc_name(String doc_name) {
    this.doc_name = doc_name;
  }

  public String getDoc_content() {
    return doc_content;
  }

  public void setDoc_content(String doc_content) {
    this.doc_content = doc_content;
  }
}
