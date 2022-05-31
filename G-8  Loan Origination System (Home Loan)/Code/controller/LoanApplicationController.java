package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.AbstractResponse;
import com.kuliza.workbench.model.Fields;
import com.kuliza.workbench.model.LoanApplication;
import com.kuliza.workbench.service.LoanApplicationService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoanApplicationController {
  @Autowired LoanApplicationService loanApplicationService;

  @PostMapping("/los-onboarding")
  public LoanApplication loanApplicationGeneration(@Valid @RequestBody Fields fields)
      throws Exception {
    return loanApplicationService.createNewApplication(fields);
  }

  @GetMapping("/getDetails")
  public AbstractResponse getDetails(@RequestParam String inquiryNumber) throws Exception {
    return loanApplicationService.getApplicationDetails(inquiryNumber);
  }
}
