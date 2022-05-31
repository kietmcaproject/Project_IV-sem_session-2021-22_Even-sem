package com.kuliza.workbench.util;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;

public class WorkbenchConstants {
  // **************** Workbench URLs *************************

  public static final String WORKBENCH_API_PREFIX = "/workbench/api";
  public static final String WORKBENCH_STARTER_API_PREFIX = "/getting-started";
  public static final String WORKBENCH_STARTER_PROP_ENDPOINT = "/global-props";
  public static final String IB_API_ENDPOINT = "/ibApi";
  public static final String CUSTOM_API = "/custom";
  public static final String JASYPT_ENCRYPTOR_PASSWORD = "(H+MbQeThWmZq4t7";
  public static File DIGIOPATH;

  public static File getDIGIOPATH() {
    return DIGIOPATH;
  }

  @Value("file.path")
  public static void setDIGIOPATH(File DIGIOPATH) {
    WorkbenchConstants.DIGIOPATH = DIGIOPATH;
  }

  public static String IB_HOST;
  public static String IB_HOST_END;
  public static final String APPLICATION_NEW_STATUS = "new application";

  public static final String DMS_ACCESS_KEY =
      "cTg3UWtKUTJGSGliJTJGa3J1eXliejUzbHQzMWclMkJDR1l2RGo1MzUlMkJtcGhJdHhjd0ElM0Q=";
}
