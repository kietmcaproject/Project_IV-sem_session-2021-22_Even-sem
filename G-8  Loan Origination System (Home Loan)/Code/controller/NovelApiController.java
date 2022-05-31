package com.kuliza.workbench.controller;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.service.NovelApiService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class NovelApiController {

  @Autowired private NovelApiService novelApiService;

  @RequestMapping(method = RequestMethod.POST, value = "/upload")
  public ResponseEntity<Object> uploadPdf(
      @RequestParam String password,
      @RequestParam String bank,
      @RequestParam String name,
      @RequestParam("file") MultipartFile file)
      throws UnirestException, IOException {

    return CommonHelperFunctions.buildResponseEntity(
        novelApiService.uploadFile(password, bank, name, file));
  }
}
