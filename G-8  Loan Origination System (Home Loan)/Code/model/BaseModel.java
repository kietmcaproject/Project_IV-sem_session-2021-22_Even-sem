package com.kuliza.workbench.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class BaseModel {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(required = false, hidden = true)
  @OrderBy(clause = "id ASC")
  private long id;

  @CreationTimestamp private ZonedDateTime created;

  @UpdateTimestamp private ZonedDateTime modified;

  @Type(type = "org.hibernate.type.NumericBooleanType")
  @ColumnDefault("0")
  @Column(nullable = false)
  private Boolean isDeleted = false;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @JsonIgnore
  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  @JsonIgnore
  public ZonedDateTime getCreated() {
    return created == null ? null : (ZonedDateTime) created;
  }

  @JsonIgnore
  public ZonedDateTime getModified() {
    return modified == null ? null : (ZonedDateTime) modified;
  }
}
