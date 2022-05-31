package com.kuliza.workbench.model;

import java.util.Map;

public class DocEstampRequest {
  Map<String, Object> tags;
  String sign_on_page;
  String note_content;
  String note_on_page;

  public DocEstampRequest() {}

  public DocEstampRequest(
      Map<String, Object> tags, String sign_on_page, String note_content, String note_on_page) {
    this.tags = tags;
    this.sign_on_page = sign_on_page;
    this.note_content = note_content;
    this.note_on_page = note_on_page;
  }

  public Map<String, Object> getTags() {
    return tags;
  }

  public void setTags(Map<String, Object> tags) {
    this.tags = tags;
  }

  public String getSign_on_page() {
    return sign_on_page;
  }

  public void setSign_on_page(String sign_on_page) {
    this.sign_on_page = sign_on_page;
  }

  public String getNote_content() {
    return note_content;
  }

  public void setNote_content(String note_content) {
    this.note_content = note_content;
  }

  public String getNote_on_page() {
    return note_on_page;
  }

  public void setNote_on_page(String note_on_page) {
    this.note_on_page = note_on_page;
  }
}
