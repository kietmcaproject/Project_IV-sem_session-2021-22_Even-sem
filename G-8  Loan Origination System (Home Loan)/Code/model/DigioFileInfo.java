package com.kuliza.workbench.model;

import java.util.Date;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class DigioFileInfo {

  public DigioFileInfo() {}

  public DigioFileInfo(String fileName, String templateId, String var) {

    this.fileName = fileName;
    this.templateId = templateId;
    this.var = var;
  }

  @Id @GeneratedValue private Long id;

  @CreatedDate private Date createdDate;

  @LastModifiedDate private Date lastModifiedDate;

  private String fileName;

  private String templateId;

  @Lob private String var;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  @Override
  public String toString() {
    return "DigioFileInfo [id="
        + id
        + ", createdDate="
        + createdDate
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", fileName="
        + fileName
        + ", templateId="
        + templateId
        + ", var="
        + var
        + "]";
  }
}
