package com.kuliza.workbench.model;

import java.util.HashMap;
import java.util.Map;

public class DocTemplate {
  private String template_key;
  private Map<String, Object> template_values = new HashMap<>();

  public DocTemplate() {}

  public DocTemplate(String template_key, Map<String, Object> template_values) {
    this.template_key = template_key;
    this.template_values = template_values;
  }

  public void addTemplate_values(String variableName, String value) {
    template_values.put(variableName, value);
  }

  // getter setter
  public String getTemplate_key() {
    return template_key;
  }

  public void setTemplate_key(String template_key) {
    this.template_key = template_key;
  }

  public Map<String, Object> getTemplate_values() {
    return template_values;
  }

  public void setTemplate_values(Map<String, Object> template_values) {
    this.template_values = template_values;
  }
}
