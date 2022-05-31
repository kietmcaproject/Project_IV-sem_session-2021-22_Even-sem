package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "global_props")
public class StarterModel extends BaseModel {

  @Column(nullable = false)
  private String propertyName;

  @Column(nullable = false)
  private String propertyValue;

  public StarterModel() {
    super();
    this.setIsDeleted(false);
  }

  public StarterModel(String propertyName, String propertyValue) {
    this();
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
  }
}
