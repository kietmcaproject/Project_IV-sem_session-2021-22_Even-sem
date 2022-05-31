package com.kuliza.workbench.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IbHelper {
  private static final Logger logger = LoggerFactory.getLogger(IbHelper.class);

  @Value("${base.url}")
  String baseUrl;

  @Value("${ib.url}")
  String ibUrl;

  @Value("${end.url}")
  String endUrl;

  @Value("${end.url.soap}")
  String endSoapUrl;

  public String getUrl(String slug) {
    //    String url =
    //      WorkbenchConstants.IB_HOST + slug + "/" + WorkbenchConstants.IB_HOST_END;

    String newUrl = baseUrl + ibUrl + slug + endUrl;
    logger.info("--------- Url for slug {} : {}", slug, newUrl);
    return newUrl;
  }

  public String getSoapUrl(String slug) {
    //    String url =
    //      WorkbenchConstants.IB_HOST + slug + "/" + WorkbenchConstants.IB_HOST_END;

    String newUrl = baseUrl + ibUrl + slug + endSoapUrl;
    logger.info("--------- Url for slug {} : {}", slug, newUrl);
    return newUrl;
  }
}
