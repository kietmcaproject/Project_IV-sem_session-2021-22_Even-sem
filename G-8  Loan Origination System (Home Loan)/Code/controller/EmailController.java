package com.kuliza.workbench.controller;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.workbench.service.BankDetailsService;
import com.kuliza.workbench.service.EmailService;
import com.kuliza.workbench.service.EmailServiceClass;
import java.io.IOException;
import java.util.Map;
import javax.mail.MessagingException;
import org.apache.commons.mail.EmailException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
  @Autowired private EmailService emailService;
  @Autowired private EmailServiceClass emailServiceClass;

  @Autowired private BankDetailsService bankDetailsService;

  @RequestMapping(method = RequestMethod.POST, value = "/email")
  public String sendEmail(@RequestBody Map<String, String> request)
      throws IOException, MessagingException, EmailException, JSONException {

    emailService.emailContent(request);
    return "Success";
  }

  @RequestMapping(method = RequestMethod.POST, value = "/setEmailTemplate")
  public String setEmail(@RequestBody Map<String, Object> request) {
    return emailServiceClass.setEmailData(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/email-bank-details")
  public ApiResponse sendEmailForBankDetails(@RequestBody Map<String, Object> request)
      throws MessagingException {
    return bankDetailsService.sendMailForDetailCaptureTest(request);
  }
}
