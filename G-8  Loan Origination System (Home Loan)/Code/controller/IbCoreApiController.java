package com.kuliza.workbench.controller;

import static com.kuliza.workbench.util.WorkbenchConstants.CUSTOM_API;
import static com.kuliza.workbench.util.WorkbenchConstants.IB_API_ENDPOINT;

import com.kuliza.workbench.service.IbCoreApiService;
import com.kuliza.workbench.util.WorkbenchConstants;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(IB_API_ENDPOINT)
@RestController
public class IbCoreApiController {

  @Autowired private IbCoreApiService ibCoreApiService;

  private String IB_HOST;
  private String IB_HOST_END;

  public String getIB_HOST() {
    return IB_HOST;
  }

  @Value(value = "${ib.host}")
  public void setIB_HOST(String IB_HOST) {
    WorkbenchConstants.IB_HOST = IB_HOST;
  }

  public String getIB_HOST_END() {
    return IB_HOST_END;
  }

  @Value(value = "${ib.host.end}")
  public void setIB_HOST_END(String IB_HOST_END) {
    WorkbenchConstants.IB_HOST_END = IB_HOST_END;
  }

  @RequestMapping(method = RequestMethod.POST, value = CUSTOM_API)
  public ResponseEntity<Object> getIBApisResponse(
      @RequestParam(required = false) String slugName,
      @RequestParam(required = false) String apiName,
      @RequestBody Map<String, Object> request)
      throws UnirestException, JSONException {
    return ibCoreApiService.customApi(slugName, apiName, request);
  }
}
