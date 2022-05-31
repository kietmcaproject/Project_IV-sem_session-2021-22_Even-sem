package com.kuliza.workbench.controller;

import com.kuliza.workbench.service.EnachApiTest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
public class EnachApiController {
  @Autowired EnachApiTest enachApiTest;

  @RequestMapping(method = RequestMethod.POST, value = "/enachTest")
  public String enach(@RequestBody Map<String, Object> request)
      throws IOException, MessagingException, EmailException, JSONException, UnirestException {

    enachApiTest.testEnach(request);
    return "Success";
  }
}
