package com.kuliza.workbench.model;

import java.util.List;

public class DocPayload {
  List<DocTemplate> templates;
  List<DocSigner> signers;
  List<DocEstampRequest> estamp_request;
  //  Map<String, Object> sign_coordinates;

  int expire_in_days;
  String display_on_page;
  boolean send_sign_link;
  boolean notify_signers;

  public DocPayload() {}

  public List<DocTemplate> getTemplates() {
    return templates;
  }

  public void setTemplates(List<DocTemplate> templates) {
    this.templates = templates;
  }

  public List<DocSigner> getSigners() {
    return signers;
  }

  public void setSigners(List<DocSigner> signers) {
    this.signers = signers;
  }

  public List<DocEstampRequest> getEstamp_request() {
    return estamp_request;
  }

  public void setEstamp_request(List<DocEstampRequest> estamp_request) {
    this.estamp_request = estamp_request;
  }

  public int getExpire_in_days() {
    return expire_in_days;
  }

  public void setExpire_in_days(int expire_in_days) {
    this.expire_in_days = expire_in_days;
  }

  public String getDisplay_on_page() {
    return display_on_page;
  }

  public void setDisplay_on_page(String display_on_page) {
    this.display_on_page = display_on_page;
  }

  public boolean isSend_sign_link() {
    return send_sign_link;
  }

  public void setSend_sign_link(boolean send_sign_link) {
    this.send_sign_link = send_sign_link;
  }

  public boolean isNotify_signers() {
    return notify_signers;
  }

  public void setNotify_signers(boolean notify_signers) {
    this.notify_signers = notify_signers;
  }

  @Override
  public String toString() {
    return "DocPayload{"
        + "templates="
        + templates
        + ", signers="
        + signers
        + ", estamp_request="
        + estamp_request
        + ", expire_in_days="
        + expire_in_days
        + ", display_on_page='"
        + display_on_page
        + '\''
        + ", send_sign_link="
        + send_sign_link
        + ", notify_signers="
        + notify_signers
        + '}';
  }
}
