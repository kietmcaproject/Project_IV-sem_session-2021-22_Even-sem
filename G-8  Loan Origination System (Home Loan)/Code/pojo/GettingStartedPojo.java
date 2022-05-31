package com.kuliza.workbench.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GettingStartedPojo {

  @NotNull(message = "name is a required key")
  @NotEmpty(message = "value cannot be empty")
  String name;

  @NotNull(message = "value is a required key")
  @NotEmpty(message = "value cannot be empty")
  String value;

  public GettingStartedPojo() {
    super();
  }

  public GettingStartedPojo(
      @NotNull(message = "name is a required key") @NotEmpty(message = "value cannot be empty")
          String name,
      @NotNull(message = "value is a required key") @NotEmpty(message = "value cannot be empty")
          String value) {
    super();
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
