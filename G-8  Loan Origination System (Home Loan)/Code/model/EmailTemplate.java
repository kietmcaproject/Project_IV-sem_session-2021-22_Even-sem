package com.kuliza.workbench.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "email_template")
public class EmailTemplate extends BaseModel {
  private String templateId;
  private String subject;
  private String body;

  public EmailTemplate() {}

  public EmailTemplate(String templateId, String subject, String body) {
    this.templateId = templateId;
    this.subject = subject;
    this.body = body;
  }

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
