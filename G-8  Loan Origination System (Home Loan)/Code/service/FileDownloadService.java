package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FileDownloadService {

  @Autowired IbHelper ibHelper;
  private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

  public ApiResponse download(String request) throws UnirestException {
    logger.info("Request body:-", request);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("cart-apis/download-report-data"))
            .header("accept", "application/json")
            .header(
                "Content-Type",
                "multipart/form-data; boundary=----WebKitFormBoundarygn63zH1NOOwM9e8j")
            .queryString("raw-text", request)
            .asString();

    logger.info(response.getBody().toString());

    return new ApiResponse(HttpStatus.OK, "SUCCESS", response.getBody());
  }

  public ApiResponse downloadAsExcel(String request) throws UnirestException {

    logger.info("Request body:-", request);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("cart-apis/download-report-as-excel"))
            .header("accept", "application/json")
            .header(
                "Content-Type",
                "multipart/form-data; boundary=----WebKitFormBoundarygn63zH1NOOwM9e8j")
            .queryString("raw-text", request)
            .asString();

    logger.info(response.getBody().toString());

    return new ApiResponse(HttpStatus.OK, "SUCCESS", response.getBody());
  }
}
