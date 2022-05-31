package com.kuliza.workbench.service;

import com.kuliza.workbench.model.EmailTemplate;
import com.kuliza.workbench.repository.EmailTemplateRepository;
import com.kuliza.workbench.util.MasterHelper;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
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

@Service("EmailServiceClass")
public class EmailServiceClass {

  @Autowired EmailTemplateRepository emailTemplateRepository;
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired JavaMailSender javaMailSender;
  @Autowired TemplateEngine templateEngine;
  @Autowired MasterHelper masterHelper;

  @Value("${link.to.open.process}")
  private String linkToOpenProcess;

  private static final Logger logger = LoggerFactory.getLogger(EmailServiceClass.class);

  public DelegatePlanItemInstance fcuEmail(
      DelegatePlanItemInstance planItemInstance, String toEmail)
      throws MessagingException, JSONException, FileNotFoundException {

    String caseInstanceId = planItemInstance.getCaseInstanceId();
    Map<String, Object> allVariablesInJson = cmmnRuntimeService.getVariables(caseInstanceId);
    logger.info("All Variables :- {} ", allVariablesInJson);
    logger.info(
        "......................................................................................");
    //    JSONObject allVariablesInJson = new JSONObject(new Gson().toJson(allVariables));
    //    logger.info("allVariablesInJson :- {} ", allVariablesInJson);

    HashMap<String, Object> mailVar = new HashMap<>();
    String losId = planItemInstance.getVariable("los_id").toString();
    logger.info("losId :- {} ", losId);

    String branchName = planItemInstance.getVariable("loanBranch_hl").toString();
    logger.info("branchName :- {} ", branchName);

    int applicantCount =
        Integer.parseInt(planItemInstance.getVariable("applicantCount_hl").toString());
    logger.info("applicantCount :- {} ", applicantCount);
    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

    String initiateDiscreetProfileCheck =
        planItemInstance.getVariable("InitiateDiscreetProfileCheck").toString();

    String fcuLink =
        linkToOpenProcess
            .concat("ExternalFCUVendorProcess")
            .concat("&applicationId=")
            .concat(losId);
    logger.info("fcu link :- {}", fcuLink);
    String aadhaarStatus = "";
    String panStatus = "";
    String bankStatementStatus = "";
    String salarySlipStatus = "";

    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < applicantCount; i++) {

      JSONObject jsonObject = new JSONObject();

      try {
        String applicantAadhaarDetails =
            allVariablesInJson.get("applicant" + (i + 1) + "AadharDetails_hl").toString();
        JSONArray applicantAadhaarDetails_hl = new JSONArray(applicantAadhaarDetails);
        JSONObject aadhaarObject = (JSONObject) applicantAadhaarDetails_hl.get(i);
        aadhaarStatus = aadhaarObject.getString("applicant" + (i + 1) + "documentStatusAadhaar");
        jsonObject.put("aadhaarStatus", aadhaarStatus);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        String applicantPanDetails =
            allVariablesInJson.get("applicant" + (i + 1) + "PanDetails_hl").toString();
        JSONArray applicantPanDetails_hl = new JSONArray(applicantPanDetails);
        JSONObject panObject = (JSONObject) applicantPanDetails_hl.get(i);
        panStatus = panObject.getString("applicant" + (i + 1) + "documentStatusPan");
        jsonObject.put("panStatus", panStatus);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        String applicantBStatementDetails =
            allVariablesInJson.get("applicant" + (i + 1) + "BStatementDetails_hl").toString();
        JSONArray applicantBStatementDetails_hl = new JSONArray(applicantBStatementDetails);
        JSONObject bankObject = (JSONObject) applicantBStatementDetails_hl.get(i);
        bankStatementStatus =
            bankObject.getString("applicant" + (i + 1) + "documentStatusBankStatement");
        jsonObject.put("bankStatementStatus", bankStatementStatus);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      try {
        //        String applicantSalarySlipDetails =
        //            allVariablesInJson.get("applicant" + (i + 1) +
        // "SalarySlipDetails_hl").toString();
        //        JSONArray applicantSalarySlipDetails_hl = new
        // JSONArray(applicantSalarySlipDetails);
        //        JSONObject salarySlipObject = (JSONObject) applicantSalarySlipDetails_hl.get(i);
        //        salarySlipStatus =
        //            salarySlipObject.getString("applicant" + (i + 1) +
        // "documentStatusSalarySlip");
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
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
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
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
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
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("remarks", "Verify Profile & Banking");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
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
          values.put("applicationNo", losId);
          values.put("branchName", branchName);
          values.put("date", currentDate);
          values.put("docName", "salarySlip.pdf");
          values.put("remarks", "Verify Profile & Banking");
          values.put("initiateDiscreetProfileCheck", initiateDiscreetProfileCheck);
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
      logger.info(" Variables :- {} ", variables);
    }
    mailVar.put("values", variables);
    mailVar.put("fcuLink", fcuLink);

    logger.info("Mail Variables :- {} ", mailVar);
    Context context = new Context();
    context.setVariables(mailVar);
    MimeMessage mail = javaMailSender.createMimeMessage();
    String body = templateEngine.process("fcu_email_template", context);
    MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    helper.setTo(toEmail);
    helper.setFrom("noreply.technical@capriglobal.in");
    helper.setSubject("Documents shared from Capri Global");
    helper.setText(body, true);

    //        String file =
    // "/home/kuliza-407/Desktop/implementation_engine_los/applicationDetails.pdf";
    //
    //        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));
    //
    //        Document doc = new Document(pdfDoc);
    //        for (int i = 0; i < applicantCount; i++) {
    //          Table table = new Table(4);
    //          table.setHorizontalAlignment(HorizontalAlignment.LEFT);
    //          table.setMargin(12f);
    //          if (i == 0) {
    //            table.addCell(
    //                    new Cell(0, 4)
    //                            .add(
    //                                    new Paragraph("Applicant Details")
    //                                            .setBold()
    //
    // .setHorizontalAlignment(HorizontalAlignment.CENTER)));
    //            table.addCell(new Cell().add(new Paragraph("Applicant Id").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant1_id", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Name").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Name_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("E-mail ID").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Email_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Designation").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Designation_hl",
    // null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Marital Status").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1MaritalStatus_hl",
    // null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Mobile Number").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1MobileNumber_hl",
    // null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Gender").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Gender_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Lat/Long Address").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Location_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Latitude").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Latitude_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Longitude").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Longitude_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Applicant Type").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1ApplicantType_hl",
    // null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Nature of Applicant's
    // Job").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1JobNature_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Relationship with
    // Applicant").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1Relation_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Number of Dependants").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1DependentsCount_hl",
    //     null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Education Qualification").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1EducationQualification_hl",
    //     null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("UCIC").setBold()));
    //            table.addCell(new Cell().add(new
    //     Paragraph(allVariablesInJson.getOrDefault("applicant1UcicId_hl", null).toString())));
    //            doc.add(table);
    //          } else {
    //            table.addCell(
    //                    new Cell(0, 4)
    //                            .add(
    //                                    new Paragraph("Co-Applicant Details")
    //                                            .setBold()
    //
    // .setHorizontalAlignment(HorizontalAlignment.CENTER)));
    //            table.addCell(new Cell().add(new Paragraph("Co-Applicant Id").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant" + (i + 1) + "_id", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Name").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //     + (i + 1) + "Name_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("E-mail ID").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //     + (i + 1) + "Email_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Designation").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //     + (i + 1) + "Designation_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Mobile Number").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //     + (i + 1) + "MobileNumber_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Gender").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //     + (i + 1) + "Gender_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Co-Applicant Type").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "ApplicantType_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Nature of Applicant's
    // Job").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "JobNature_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Relationship with
    // Applicant").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "Relation_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Number of Dependants").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "DependentsCount_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("Education Qualification").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "EducationQualification_hl", null).toString())));
    //            table.addCell(new Cell().add(new Paragraph("UCIC").setBold()));
    //            table.addCell(new Cell().add(new
    // Paragraph(allVariablesInJson.getOrDefault("applicant"
    //                    + (i + 1) + "UcicId_hl", null).toString())));
    //            doc.add(table);
    //          }
    //        }
    //            doc.close();
    //        helper.addAttachment(
    //                "Application details",
    //                new
    //     File("/home/kuliza-407/Desktop/implementation_engine_los/applicationDetails.pdf"));

    javaMailSender.send(mail);
    System.out.println("Mail sent successfully........");

    return planItemInstance;
  }

  public DelegatePlanItemInstance legalEmail(
      DelegatePlanItemInstance planItemInstance, String toEmail)
      throws MessagingException, JSONException, FileNotFoundException {
    Map<String, Object> allVariables = planItemInstance.getVariables();
    logger.info("All Variables :- {} ", allVariables);
    HashMap<String, Object> mailVar = new HashMap<>();
    String losId = planItemInstance.getVariable("los_id").toString();
    logger.info("losId :- {} ", losId);
    int applicantCount =
        Integer.parseInt(planItemInstance.getVariable("applicantCount_hl").toString());
    String dealNo = allVariables.getOrDefault("loanId_hl", "").toString();
    String segmentOfApplicantDropdown =
        allVariables.getOrDefault("segmentOfApplicantDropdown", "").toString();
    String productName = "Home Loan";
    String propertyAddress = allVariables.getOrDefault("propertyAddress_hl", "").toString();
    String initiationDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    String branchName = allVariables.getOrDefault("loanBranch_hl", "").toString();
    JSONObject masterObject = masterHelper.getRequestFromMaster("capri-hrmis");
    JSONArray masterData = masterObject.getJSONArray("data");
    String mobile = "";
    String legalLink =
        linkToOpenProcess.concat("ExternalLawyerProcess").concat("&applicationId=").concat(losId);
    for (int i = 0; i < masterData.length(); i++) {
      JSONObject data = masterData.getJSONObject(i);
      if (data.getString("Name as per HRMS").equalsIgnoreCase("Internal Legal Manager 1")) {
        mobile = data.get("Mobile").toString();
      }
    }

    String primaryApplicant = planItemInstance.getVariable("applicant1Name_hl").toString();
    List<String> applicantNames = new ArrayList<>();
    for (int i = 1; i < applicantCount; i++) {
      String coApplicantName =
          planItemInstance.getVariable("applicant" + (i + 1) + "Name_hl").toString();
      applicantNames.add(coApplicantName);
    }
    String delimiter = ", ";
    StringJoiner joiner = new StringJoiner(delimiter);
    applicantNames.forEach(item -> joiner.add(item));
    mailVar.put("dealNo", dealNo);
    mailVar.put("segmentOfApplicantDropdown", segmentOfApplicantDropdown);
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

    javaMailSender.send(mail);
    System.out.println("Mail sent successfully........");
    return planItemInstance;
  }

  public String setEmailData(Map<String, Object> request) {
    EmailTemplate emailTemplate = new EmailTemplate();
    emailTemplate.setTemplateId(request.get("templateId").toString());
    emailTemplate.setSubject(request.get("subject").toString());
    emailTemplate.setBody(request.get("body").toString());
    emailTemplateRepository.save(emailTemplate);

    return emailTemplate.toString();
  }

  //  public static void main(String[] args) throws JSONException, FileNotFoundException {
  //    String file = "/home/kuliza-407/Desktop/implementation_engine_los/addingTableToPDF.pdf";
  //
  //    // Step-1 Creating a PdfDocument object
  //    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));
  //
  //    // Step-2 Creating a Document object
  //    Document doc = new Document(pdfDoc);
  //
  //    // Step-3 Creating a table
  //    Table table = new Table(2);
  //
  //    // Step-4 Adding cells to the table
  //    table.addCell(new Cell().add(new Paragraph("Name")));
  //    table.addCell(new Cell().add(new Paragraph("Raju")));
  //    table.addCell(new Cell().add(new Paragraph("Id")));
  //    table.addCell(new Cell().add(new Paragraph("1001")));
  //    table.addCell(new Cell().add(new Paragraph("Designation")));
  //    table.addCell(new Cell().add(new Paragraph("Programmer")));
  //
  //    // Step-6 Adding Table to document
  //    doc.add(table);
  //
  //    // Step-7 Closing the document
  //    doc.close();
  //    System.out.println("Table created successfully..");
  //  }
  //      PDDocument document = new PDDocument();
  //
  //      PDPage page = new PDPage();
  //      document.addPage(page);
  //
  //      PDPageContentStream contentStream = new PDPageContentStream(document, page);
  //
  //      contentStream.setFont(PDType1Font.COURIER, 12);
  //      contentStream.beginText();
  //      contentStream.showText("Hello World");
  //      contentStream.endText();
  //      contentStream.close();
  //
  //      document.save("pdfBoxHelloWorld.pdf");
  //      document.close();
  //    }
  //    JSONArray jsonArray = new JSONArray();
  //    for (int i = 0; i < 5; i++){
  //      int c = 5, d = 3;
  //      JSONObject jsonObject = new JSONObject();
  //      if (c == 5) {
  //        jsonObject.put("a", 5);
  //        jsonObject.put("a", 7);
  //      }
  //      if (d ==3) {
  //        jsonObject.put("a", 5);
  //        jsonObject.put("a", 9);
  //        jsonObject.put("a", 70);
  //        jsonObject.put("a", 90);
  //      }
  //      jsonArray.put(jsonObject);
  //    }
  //    System.out.println(jsonArray);
  //  }
}
