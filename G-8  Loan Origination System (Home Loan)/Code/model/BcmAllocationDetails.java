package com.kuliza.workbench.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BcmAllocationDetails")
public class BcmAllocationDetails extends BaseModel {
  @Column(unique = true)
  String branchName;

  int count;

  public BcmAllocationDetails(String branchName, int count) {
    this.branchName = branchName;
    this.count = count;
  }

  public BcmAllocationDetails() {}

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
