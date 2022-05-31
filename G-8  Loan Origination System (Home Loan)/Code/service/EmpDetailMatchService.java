package com.kuliza.workbench.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("EmpDetailMatchService")
public class EmpDetailMatchService {
  private static final Logger logger = LoggerFactory.getLogger(EmpDetailMatchService.class);

  public DelegateExecution match(DelegateExecution execution) {
    logger.info("inside EmpDetailMatchService - match() method");

    return execution;
  }
}
