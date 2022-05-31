package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.FinalLoanAmountService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinalLoanAmountController {

  @Autowired FinalLoanAmountService finalLoanAmountService;

  @RequestMapping(method = RequestMethod.POST, value = "/calc-final-loan-amount")
  public ApiResponse calFinalLoanAmountApi(@RequestBody Map<String, Object> request) {
    return finalLoanAmountService.calcFinalLoanAmount(request);
  }
}
