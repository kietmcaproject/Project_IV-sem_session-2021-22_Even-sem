package com.kuliza.workbench.controller;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.service.EStampService;
import com.kuliza.workbench.service.FileDownloadService;
import com.kuliza.workbench.util.CapriUtilService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileDownloadController {

  @Autowired FileDownloadService fileDownloadService;
  //  @Autowired AllocationLogicService allocationLogicService;
  //  @Autowired NameSimilarityService nameSimilarityService;
  //  @Autowired PennyDropApiService pennyDropApiService;
  @Autowired EStampService eStampService;
  @Autowired CapriUtilService capriUtilService;

  @RequestMapping(method = RequestMethod.POST, value = "/downloadFile")
  public ResponseEntity<Object> downloadFile(@RequestParam String docId) throws UnirestException {

    //    uploadPdfService.uploadFile("", "", "", );
    return CommonHelperFunctions.buildResponseEntity(fileDownloadService.download(docId));
  }

  //  @RequestMapping(method = RequestMethod.POST, value = "/downloadFileAsExcel")
  //  public ApiResponse sampleController(@RequestBody Map<String, Object> request)
  //      throws UnirestException, JSONException {
  //    String name1 = request.get("name1").toString();
  //    String name2 = request.get("name2").toString();
  //
  //    return nameSimilarityService.nameSimilarityApi(name1, name2);
  //  }

  //
  //  @RequestMapping(method = RequestMethod.POST, value = "/downloadFileAsExcel")
  //  public void pennyDrop() throws UnirestException {
  //    pennyDropApiService.callPennyDrop();
  //  }

  //  @RequestMapping(method = RequestMethod.POST, value = "/downloadFileAsExcel")
  //  public void estampApi() throws UnirestException, JSONException, IOException {
  //    eStampService.createSignatureRequestTest();
  //  }

  @RequestMapping(method = RequestMethod.POST, value = "/downloadFileAsExcel")
  public void sampleController(@RequestParam("file") MultipartFile file)
      throws UnirestException, JSONException, IOException {
    //    capriUtilService.multipartFileToBase64(file);
  }
}
