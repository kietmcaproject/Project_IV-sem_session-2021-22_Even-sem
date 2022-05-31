package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.repository.EPFOApiDetailsRepository;
import com.kuliza.workbench.service.EmpVerificationApiService;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import javax.mail.MessagingException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("emp-verification")
public class EmpVerificationApiController {

  @Autowired EmpVerificationApiService empVerificationApiService;

  @Autowired EPFOApiDetailsRepository epfoApiDetailsRepository;

  @RequestMapping(method = RequestMethod.POST, value = "/send-details")
  public ApiResponse receiveDetails(@RequestBody Map<String, Object> request)
      throws JSONException, MessagingException {
    return empVerificationApiService.receiveEmpDetailsFromPragatiApp(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/epfo-check")
  public ResponseEntity<Object> epfoCheckApi(@RequestBody Map<String, Object> request)
      throws JSONException, UnirestException {
    return empVerificationApiService.epfoCheck(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/email-auth")
  public ResponseEntity<Object> emailAuthApi(@RequestBody Map<String, Object> request)
      throws JSONException, UnirestException {
    return empVerificationApiService.emailAuthLinkBased(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/kscan-epf-profile")
  public ResponseEntity<Object> kscanEpfProfileApi(@RequestBody Map<String, Object> request)
      throws JSONException, UnirestException {
    return empVerificationApiService.kscanEpfProfile(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/form26as-check")
  public ResponseEntity<Object> form26asApi(@RequestBody Map<String, Object> request)
      throws JSONException, UnirestException {
    return empVerificationApiService.form26asCheck(request);
  }
}
