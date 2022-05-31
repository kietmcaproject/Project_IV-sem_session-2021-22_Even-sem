package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.model.BcmAllocationDetails;
import com.kuliza.workbench.repository.BcmAllocationDetailsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BcmAllocationService {
  @Autowired BcmAllocationDetailsRepository bcmAllocationDetailsRepository;

  public ApiResponse allocateBcm(Map<String, String> request) {
    String branchName = request.get("branchName");
    String bcmAssignee = "";
    List<String> bcmRoleList = new ArrayList<>();
    bcmRoleList.add("One");
    bcmRoleList.add("Two");
    bcmRoleList.add("Three");
    bcmRoleList.add("Four");
    bcmRoleList.add("five");
    bcmRoleList.add("six");
    if (!bcmRoleList.isEmpty()) {
      BcmAllocationDetails branchNameExists = null;
      try {
        branchNameExists = bcmAllocationDetailsRepository.findByBranchName(branchName);
      } catch (Exception e) {
      }
      System.out.println(branchNameExists);
      BcmAllocationDetails bcmAllocationDetails = new BcmAllocationDetails();
      if (branchNameExists == null) {
        System.out.println("in if condition.................");
        bcmAllocationDetails.setBranchName(branchName);
        bcmAllocationDetails.setCount(1);
        bcmAllocationDetailsRepository.save(bcmAllocationDetails);
        bcmAssignee = bcmRoleList.get(0);
      } else {
        System.out.println("in else condition.................");
        int count = branchNameExists.getCount();
        bcmAssignee = bcmRoleList.get(count);
        if (count == bcmRoleList.size() - 1) {
          System.out.println("in second if condition.................");
          bcmAllocationDetailsRepository.updateCount(branchName, 0);
        } else {
          System.out.println("in second else condition.................");
          bcmAllocationDetailsRepository.updateCount(branchName, count + 1);
        }
      }
    }
    return new ApiResponse(HttpStatus.OK, "Success", bcmAssignee);
  }
}
