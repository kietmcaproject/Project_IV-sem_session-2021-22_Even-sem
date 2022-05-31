package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.engine_common.pojo.FileDownloadRequest;
import com.kuliza.workbench.hook.DMSCustomService;
import com.kuliza.workbench.service.DmsService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DmsUploadController {
  @Autowired DmsService dmsService;
  @Autowired DMSCustomService dmsCustomService;

  @PostMapping("/dms-upload-doc")
  public Map<String, Object> dmsUpload(
      @RequestParam("file") MultipartFile doc, @RequestParam Map<String, String> request)
      throws JSONException, IOException, UnirestException {

    return dmsService.uploadDoc(doc, request);
  }

  @PostMapping("/dms-get-list-of-doc")
  public ApiResponse dmsGetList(@RequestBody Map<String, String> request)
      throws JSONException, IOException, UnirestException {
    FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();

    return dmsService.getListOfDoc(request);
  }
}
