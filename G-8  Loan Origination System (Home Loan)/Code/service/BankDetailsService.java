package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service("BankDetailsService")
public class BankDetailsService {
  private static final Logger logger = LoggerFactory.getLogger(BankDetailsService.class);

  @Autowired RuntimeService runtimeService;

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Autowired JavaMailSender javaMailSender;

  @Autowired TemplateEngine templateEngine;

  @Value("${link.to.open.process}")
  private String linkToOpenProcess;

  public DelegateExecution sendMailForDetailCapture(DelegateExecution execution)
      throws MessagingException {
    logger.info("inside BankDetailsService - capture method");
    String loanId = execution.getVariable("loanId_hl").toString();
    String applicationId = "AP" + loanId.substring(2);
    String bankDetailsFormLink =
        linkToOpenProcess
            .concat("postSanctioningCapri")
            .concat("&applicationId=")
            .concat(applicationId);

    logger.info("bankDetailsFormLink : {}", bankDetailsFormLink);

    String primaryApplicant = execution.getVariable("applicant1Name_hl").toString();
    String primaryApplicantEmail = execution.getVariable("applicant1Email_hl").toString();

    // mail sending with java mail sender
    HashMap<String, Object> mailVar = new HashMap<>();
    mailVar.put("detailsFormLink", bankDetailsFormLink);
    mailVar.put("primaryApplicant", primaryApplicant);

    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("bank_details_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(primaryApplicantEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Application for legal evaluation from Capri Global");
    helper.setText(body, true);

    javaMailSender.send(mail);
    logger.info("Mail sent successfully for CAPTURING BANK DETAILS ........");

    return execution;
  }

  public DelegatePlanItemInstance setLinkVariable(DelegatePlanItemInstance planItemInstance) {
    logger.info("inside BankDetailsService - set link variable method");
    String loanId = planItemInstance.getVariable("loanId_hl").toString();
    String caseInstanceId = planItemInstance.getCaseInstanceId();
    logger.info("case instance id : {}", caseInstanceId);
    String applicationId = "AP" + loanId.substring(2);
    String bankDetailsFormLink =
        linkToOpenProcess
            .concat("bankDetailsModelCapri")
            .concat("&applicationId=")
            .concat(applicationId);

    logger.info("bankDetailsFormLink : {}", bankDetailsFormLink);

    cmmnRuntimeService.setVariable(caseInstanceId, "sellerBankDetailsLink_hl", bankDetailsFormLink);

    return planItemInstance;
  }

  public ApiResponse sendMailForDetailCaptureTest(Map<String, Object> request)
      throws MessagingException {
    // !!! THIS METHOD IS FOR ONLY TESTING PURPOSE
    logger.info("inside BankDetailsService - capture method");
    //    String loanId = execution.getVariable("loanId_hl").toString();
    String loanId = request.get("loanId_hl").toString();
    String oldLosId = "AP" + loanId.substring(2);
    String bankDetailsFormLink =
        "https://los.capriglobal.dev/external?jounreyName=postSanctioningCapri&applicationId="
            .concat(oldLosId);

    logger.info("bankDetailsFormLink : {}", bankDetailsFormLink);
    String primaryApplicant = request.get("applicant1Name_hl").toString();
    String primaryApplicantEmail = request.get("applicant1Email_hl").toString();

    HashMap<String, Object> mailVar = new HashMap<>();
    mailVar.put("detailsFormLink", bankDetailsFormLink);
    mailVar.put("primaryApplicant", primaryApplicant);

    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("bank_details_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(primaryApplicantEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Provide bank details for Home Loan from Capri Global");
    helper.setText(body, true);

    javaMailSender.send(mail);
    System.out.println("Mail sent successfully for BANK DETAILS ........");

    return new ApiResponse(200, "Ok");
  }
}
