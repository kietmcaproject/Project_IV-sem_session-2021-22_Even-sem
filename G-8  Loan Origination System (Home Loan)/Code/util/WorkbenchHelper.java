package com.kuliza.workbench.util;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbenchHelper extends CommonHelperFunctions {

  private static final Logger logger = LoggerFactory.getLogger(CommonHelperFunctions.class);

  public static boolean isNullOrEmpty(String str) {
    logger.info("Checking if {} is null or empty", str);
    return (str == null || str.isEmpty()) ? true : false;
  }
}
