package com.kuliza.workbench.model;

public class DocSigner {
  String identifier;
  String name;
  String reason;
  String sign_type;

  public DocSigner() {}

  public DocSigner(String identifier, String name, String reason, String sign_type) {
    this.identifier = identifier;
    this.name = name;
    this.reason = reason;
    this.sign_type = sign_type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getSign_type() {
    return sign_type;
  }

  public void setSign_type(String sign_type) {
    this.sign_type = sign_type;
  }
}
