package com.kuliza.workbench.service;

import com.kuliza.workbench.util.MasterHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.EmailException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
  @Autowired private JavaMailSender javaMailSender;
  @Autowired TemplateEngine templateEngine;
  @Autowired MasterHelper masterHelper;

  @Value("${link.to.open.process}")
  private String linkToOpenProcess;

  public void emailContent(Map<String, String> request)
      throws IOException, MessagingException, EmailException, JSONException {
    String toEmail = request.get("to").toString();
    System.out.println(toEmail);
    legalEmail(toEmail);
  }

  public void sendEmail(String toEmail)
      throws IOException, MessagingException, EmailException, JSONException {
    //    SimpleMailMessage message = new SimpleMailMessage();
    //    //    message.setFrom("noreply.technical@capriglobal.in");
    //    message.setFrom("gu028@capriglobal.in");
    //    message.setTo(toEmail);
    //
    //    message.setText(body);
    //    message.setSubject(subject);
    //    System.out.println("printing...");
    //    System.out.println(message);
    //    System.out.println("printed....");
    //    javaMailSender.send(message);
    //    //    logger.info("Mail sent successfully........");
    //    System.out.println("Mail sent successfully........");
    HashMap<String, Object> mailVar = new HashMap<String, Object>();

    String losId = "ABHLGZB000000234";
    logger.info("losId :- {} ", losId);

    String branchName = "Preet Vihar";
    logger.info("branchName :- {} ", branchName);

    int applicantCount = 6;
    logger.info("applicantCount :- {} ", applicantCount);
    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    String initiateDiscreetProfileCheck = "no";
    String fcuLink =
        linkToOpenProcess
            .concat("ExternalFCUVendorProcess")
            .concat("&applicationId=")
            .concat(losId);
    logger.info("fcu link :- {}", fcuLink);

    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < applicantCount; i++) {

      JSONObject jsonObject = new JSONObject();

      try {
        jsonObject.put("aadhaarStatus", "Samled");
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        jsonObject.put("panStatus", "Samped");
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        jsonObject.put("bankStatementStatus", "Samled");
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        jsonObject.put("salarySlipStatus", "Sampled");
      } catch (JSONException e) {
        e.printStackTrace();
      }

      jsonArray.put(jsonObject);
    }
    logger.info("jsonArray1 :- {} ", jsonArray);

    List<JSONObject> variables = new ArrayList<JSONObject>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = (JSONObject) jsonArray.get(0);
      try {
        if (jsonObject.getString("aadhaarStatus").equals("Sampled")) {
          JSONObject values = new JSONObject();
          values.put("docName", "aadhaar.pdf");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          if (i == 0) {
            values.put("applicantType", "PrimaryApplicant");
          } else {
            values.put("applicantType", "Co-Applicant");
          }
          variables.add(values);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        if (jsonObject.getString("panStatus").equals("Sampled")) {
          JSONObject values = new JSONObject();
          values.put("docName", "pan.pdf");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          if (i == 0) {
            values.put("applicantType", "PrimaryApplicant");
          } else {
            values.put("applicantType", "Co-Applicant");
          }
          variables.add(values);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        if (jsonObject.getString("bankStatementStatus").equals("Sampled")) {
          JSONObject values = new JSONObject();
          values.put("docName", "bankStatement.pdf");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          if (i == 0) {
            values.put("applicantType", "PrimaryApplicant");
          } else {
            values.put("applicantType", "Co-Applicant");
          }
          variables.add(values);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        if (jsonObject.getString("salarySlipStatus").equals("Sampled")) {
          JSONObject values = new JSONObject();
          values.put("docName", "salarySlip.pdf");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          if (i == 0) {
            values.put("applicantType", "PrimaryApplicant");
          } else {
            values.put("applicantType", "Co-Applicant");
          }
          variables.add(values);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    //    putAll.put("array", variables);
    mailVar.put("values", variables);
    mailVar.put("fcuLink", fcuLink);

    logger.info("Mail Variables :- {} ", mailVar);

    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("fcu_email_template", context);

    //    Properties props = System.getProperties();
    //    props.put("mail.smtp.host", "smtp.office365.com");
    //    props.put("mail.smtp.port", "587");
    //    props.put("mail.smtp.ssl.enable", "true");
    //    props.put("mail.smtp.auth", "true");
    //
    //    Session session =
    //        Session.getInstance(
    //            props,
    //            new Authenticator() {
    //              @Override
    //              protected PasswordAuthentication getPasswordAuthentication() {
    //                return new PasswordAuthentication("gu028@capriglobal.in", "Delhi$2022");
    //              }
    //            });
    //
    //    session.setDebug(true);
    //
    //    MimeMessage message = new MimeMessage(session);
    //    message.setFrom("noreply.technical@capriglobal.in");
    //    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
    //    message.setSubject("Application for legal evaluation from Capri Global");
    //    message.setText(body);
    //    Transport.send(message);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(toEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Documents shared from Capri Global");
    helper.setText(body, true);

    // String file = "/home/kuliza-407/Desktop/implementation_engine_los/applicationDetails.pdf";

    // PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

    // Document doc = new Document(pdfDoc);
    //    for (int i = 0; i < applicantCount; i++) {
    //      Table table = new Table(4);
    //      table.setHorizontalAlignment(HorizontalAlignment.LEFT);
    //      table.setMargin(12f);
    //      if (i == 0) {
    //        table.addCell(new Cell(0, 4).add(new Paragraph("Applicant Details").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Applicant Id").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("101")));
    //        table.addCell(new Cell().add(new Paragraph("Name").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Mayank")));
    //        table.addCell(new Cell().add(new Paragraph("E-mail ID").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("mayank@algo.com")));
    //        table.addCell(new Cell().add(new Paragraph("Designation").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Engineer")));
    //        table.addCell(new Cell().add(new Paragraph("Marital Status").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Unmarried")));
    //        table.addCell(new Cell().add(new Paragraph("Mobile Number").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("9956746373")));
    //        table.addCell(new Cell().add(new Paragraph("Gender").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("male")));
    //        table.addCell(new Cell().add(new Paragraph("Lat/Long Address").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("N?72, Connaught Circus, New Delhi")));
    //        table.addCell(new Cell().add(new Paragraph("Latitude").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("28.6319781")));
    //        table.addCell(new Cell().add(new Paragraph("Longitude").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("77.2197395")));
    //        table.addCell(new Cell().add(new Paragraph("Applicant Type").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Primary Applicant")));
    //        table.addCell(new Cell().add(new Paragraph("Nature of Applicant's Job").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Skilled")));
    //        table.addCell(new Cell().add(new Paragraph("Relationship with Applicant").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Self")));
    //        table.addCell(new Cell().add(new Paragraph("Number of Dependants").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("1")));
    //        table.addCell(new Cell().add(new Paragraph("Education Qualification").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Post-graduate/ masters’ degree")));
    //        table.addCell(new Cell().add(new Paragraph("UCIC").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("12")));
    //        doc.add(table);
    //      } else {
    //        table.addCell(new Cell(0, 4).add(new Paragraph("Co-Applicant Details").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Co-Applicant Id").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("103")));
    //        table.addCell(new Cell().add(new Paragraph("Name").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Ranjani")));
    //        table.addCell(new Cell().add(new Paragraph("E-mail ID").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("ranjani1@algo.com")));
    //        table.addCell(new Cell().add(new Paragraph("Designation").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Lead")));
    //        table.addCell(new Cell().add(new Paragraph("Mobile Number").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("7756473847")));
    //        table.addCell(new Cell().add(new Paragraph("Gender").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Female")));
    //        table.addCell(new Cell().add(new Paragraph("Co-Applicant Type").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Non-Financial")));
    //        table.addCell(new Cell().add(new Paragraph("Nature of Applicant's Job").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Skilled")));
    //        table.addCell(new Cell().add(new Paragraph("Relationship with Applicant").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Spouse")));
    //        table.addCell(new Cell().add(new Paragraph("Number of Dependants").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("2")));
    //        table.addCell(new Cell().add(new Paragraph("Education Qualification").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("Post-graduate/ masters’ degree")));
    //        table.addCell(new Cell().add(new Paragraph("UCIC").setBold()));
    //        table.addCell(new Cell().add(new Paragraph("13")));
    //        doc.add(table);
    //      }
    //    }
    //    doc.close();
    //    helper.addAttachment(
    //        "Application details",
    //        new
    // File("/home/kuliza-407/Desktop/implementation_engine_los/applicationDetails.pdf"));

    javaMailSender.send(mail);
    System.out.println("Mail sent successfully........");
  }

  public void legalEmail(String toEmail) throws MessagingException, JSONException {
    HashMap<String, Object> mailVar = new HashMap<>();

    int applicantCount = 2;
    String dealNo = "APLNGZB678965";
    String productName = "Home Loan";
    String propertyAddress = "Gorakhpur, 273413";
    String initiationDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    String branchName = "Preet Vihar";
    String mobile = "9989989878";
    String legalLink =
        linkToOpenProcess.concat("ExternalLawyerProcess").concat("&applicationId=").concat(dealNo);
    String primaryApplicant = "Kshitiz";
    List<String> applicantNames = new ArrayList<>();
    applicantNames.add("Akash");
    applicantNames.add("Mayank");
    applicantNames.add("Nikhil");
    applicantNames.add("Suresh");
    applicantNames.add("Rakesh");

    String delimiter = ", ";
    StringJoiner joiner = new StringJoiner(delimiter);
    applicantNames.forEach(item -> joiner.add(item));
    mailVar.put("dealNo", dealNo);
    mailVar.put("productName", productName);
    mailVar.put("propertyAddress", propertyAddress);
    mailVar.put("initiationDate", initiationDate);
    mailVar.put("branchName", branchName);
    mailVar.put("mobile", mobile);
    mailVar.put("primaryApplicant", primaryApplicant);
    mailVar.put("coApplicantName", joiner);
    mailVar.put("legalLink", legalLink);

    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("legal_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(toEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Application for legal evaluation from Capri Global");
    helper.setText(body, true);
    Properties prop = System.getProperties();
    printProperties(prop);

    javaMailSender.send(mail);
    System.out.println("Mail sent successfully........");
  }

  private void printProperties(Properties prop) {
    for (Object key : prop.keySet()) {
      System.out.println(key + ": " + prop.getProperty(key.toString()));
    }
  }
}
