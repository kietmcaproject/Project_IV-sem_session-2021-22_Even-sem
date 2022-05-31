package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.model.PragatiAppDetails;
import com.kuliza.workbench.repository.PragatiAppDetailsRepository;
import com.kuliza.workbench.service.DigioService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DigioController {
  private static final Logger logger = LoggerFactory.getLogger(DigioController.class);

  @Autowired DigioService digioService;
  @Autowired PragatiAppDetailsRepository pragatiAppDetailsRepository;

  @Autowired RuntimeService runtimeService;

  @Autowired TaskService taskService;

  @PostMapping(value = "/fileUpload")
  public ApiResponse uploadAgreement(MultipartFile uploadFile) throws IOException {
    ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, "file not valid", null);

    if (uploadFile.isEmpty()) return response;
    response = digioService.digioFileToPdf(uploadFile);
    return response;
  }

  @PostMapping(value = "/product/saveVariables")
  public ApiResponse saveVariables(@RequestBody Map<String, Object> request) throws IOException {
    ApiResponse response = new ApiResponse(HttpStatus.OK, "not valid", null);
    String processInstanceId = request.get("processInstanceId").toString();
    try {
      logger.info("Inside try block...................");
      PragatiAppDetails pragatiAppDetails =
          pragatiAppDetailsRepository.findByKulizaAppId(
              request.get("lasid").toString()); // lasid is shared with frontend
      logger.info(
          "Pragati App Details Process Instance Id......:- {}",
          pragatiAppDetails.getProcessInstanceId());
      String caseInstanceId =
          runtimeService
              .getVariable(pragatiAppDetails.getProcessInstanceId(), "caseInstanceId")
              .toString();
      logger.info("Case Instance Id..............:- {}", caseInstanceId);
      runtimeService.setVariable(processInstanceId, "oldCaseInstanceId", caseInstanceId);
      List<Task> allTasks =
          taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
      taskService.complete(allTasks.get(0).getId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }

  @PostMapping(value = "/createDocFromTemplate")
  public ApiResponse docFromTemplate() throws IOException, JSONException {
    return digioService.generateDocFromTemplates();
  }

  @PostMapping(value = "/createDocFromSingleTemplate")
  public ApiResponse docFromSingleTemplate() throws IOException, JSONException {
    return digioService.generateDocFromSingleTemplate();
  }
}
