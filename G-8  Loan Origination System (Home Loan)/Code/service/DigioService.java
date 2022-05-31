package com.kuliza.workbench.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.model.DigioFileInfo;
import com.kuliza.workbench.model.DocPayload;
import com.kuliza.workbench.model.DocSigner;
import com.kuliza.workbench.model.DocTemplate;
import com.kuliza.workbench.repository.DigioFileRepository;
import com.kuliza.workbench.util.IbHelper;
import com.kuliza.workbench.util.WorkbenchConstants;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("DigioService")
public class DigioService {

  @Autowired DigioFileRepository digioFileRepository;

  @Autowired IbHelper ibHelper;

  private static final Logger logger = LoggerFactory.getLogger(DigioService.class);

  public ApiResponse digioFileToPdf(MultipartFile uploadFile) {

    try {
      File file = new File(WorkbenchConstants.DIGIOPATH + uploadFile.getOriginalFilename());
      FileUtils.writeByteArrayToFile(file, uploadFile.getBytes());
      String hashGenerationUrl =
          "https://los.capriglobal.dev/ib/api/external-integration/rest/digio-apis/create-template/kuliza/jFhZQTHsYWSBujq_3K7fPh3rdU7m9dHDivVGXQxHInueW_clWwoSHfwSmFhveMZN/?env=capri&isFile=false&raw=false&version=1.0";
      HttpEntity entity =
          MultipartEntityBuilder.create().addPart("file", new FileBody(file)).build();
      HttpPost requestPost = new HttpPost(hashGenerationUrl);
      requestPost.setEntity(entity);
      HttpClient client = HttpClientBuilder.create().build();
      HttpResponse response = client.execute(requestPost);
      HttpEntity entity1 = response.getEntity();
      String responseString = EntityUtils.toString(entity1, "UTF-8");
      if (response.getStatusLine().getStatusCode() == 200) {
        HashMap<String, Object> template =
            new ObjectMapper().readValue(responseString, HashMap.class);
        String digioVariables =
            new Gson().fromJson(responseString, JsonObject.class).get("variables").toString();
        DigioFileInfo fileModel =
            new DigioFileInfo(
                uploadFile.getOriginalFilename(), template.get("id").toString(), digioVariables);
        digioFileRepository.save(fileModel);
        return new ApiResponse(HttpStatus.SC_OK, "Success message");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ApiResponse(HttpStatus.SC_BAD_REQUEST, "fail to upload a file");
  }

  public ApiResponse generateDocFromSingleTemplate() throws JSONException {
    DocTemplate docTemplate = fillDRFFormatTemplate("TMP220517142352517A8WGVY1DHUN8S7");
    String templateKey = docTemplate.getTemplate_key();
    Map<String, Object> templateVariables = docTemplate.getTemplate_values();
    templateVariables.put("url#id", templateKey);
    templateVariables.put("qp#file_type", "PDF");
    JSONObject payload = new JSONObject(new Gson().toJson(templateVariables));
    JSONObject response = new JSONObject();
    try {
      String url = ibHelper.getUrl("digio-apis/generating-document-from-single-template");
      logger.info("generateDocFromSingleTemplate api request : {}", payload);
      com.mashape.unirest.http.HttpResponse<String> httpResponse =
          Unirest.post(url).header("Content-Type", "application/json").body(payload).asString();

      response = new JSONObject(httpResponse.getBody());
      //      logger.info("generateDocFromSingleTemplate api response : {}", response);

      int status = Integer.parseInt(response.getString("status"));
      if (status == 200) {
        return new ApiResponse(org.springframework.http.HttpStatus.OK, "success");
      }

    } catch (UnirestException e) {
      logger.info("Error in hitting generateDocFromSingleTemplate");
      e.printStackTrace();
    }

    return new ApiResponse(org.springframework.http.HttpStatus.BAD_REQUEST, "failed to create doc");
  }

  public ApiResponse generateDocFromTemplates() throws JSONException {
    String json = createPayload();
    logger.info("json : {}", json);
    JSONObject payload = new JSONObject(json);

    JSONObject response = null;
    try {
      String url = ibHelper.getUrl("digio-apis/generating-a-document-from-templates-and-esigning");
      logger.info("generateDocFromTemplates api request : {}", payload);
      com.mashape.unirest.http.HttpResponse<String> httpResponse =
          Unirest.post(url).header("Content-Type", "application/json").body(payload).asString();
      response = new JSONObject(httpResponse.getBody());
      logger.info("generateDocFromTemplates api response : {}", response);
    } catch (UnirestException e) {
      logger.info("Error in hitting generateDocFromTemplates");
      e.printStackTrace();
    }
    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.toString());
    int status = (int) res.get("status");
    if (status != 200) {
      return new ApiResponse(status, "error", res);
    }
    return new ApiResponse(org.springframework.http.HttpStatus.OK, "success", res);
  }

  private String createPayload() {
    DocPayload docPayload = new DocPayload();
    List<DocTemplate> templates = new ArrayList<>();
    DocTemplate template = fillEndUseLetterTemplate("TMP220513170810791AWJUMGA2JLKEYZ");
    DocTemplate template1 = fillDRFFormatTemplate("TMP220517142352517A8WGVY1DHUN8S7");
    DocTemplate template2 = fillAppFormTemplate("TMP220530104401312YH4JFSXILKJZTB");

    templates.add(template);

    List<DocSigner> signers = new ArrayList<>();
    DocSigner docSigner =
        new DocSigner("anupam.kushwaha@kuliza.com", "Anupam K", "Loan Agreement", "aadhaar");
    signers.add(docSigner);

    //    List<DocEstampRequest> estampRequest = new ArrayList<>();
    //    Map<String, Object> tags = new HashMap<>();
    //    tags.put("MH-100-MH_100", 1);
    //    DocEstampRequest docEstampRequest =
    //        new DocEstampRequest(tags, "ALL", "This is dummy content", "ALL");
    //    estampRequest.add(docEstampRequest);

    docPayload.setTemplates(templates);
    docPayload.setSigners(signers);
    //    docPayload.setEstamp_request(estampRequest);
    docPayload.setExpire_in_days(10);
    docPayload.setDisplay_on_page("all");
    docPayload.setSend_sign_link(true);
    docPayload.setNotify_signers(true);
    logger.info("docPayload : {}", docPayload);
    Gson gson = new GsonBuilder().create();

    return gson.toJson(docPayload);
  }

  private DocTemplate fillEndUseLetterTemplate(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Applicant_No", "APHLGZBKZ000001467");
    templateValues.put("Applicant_Nam", "Mayank");
    templateValues.put("Facility_Name", "option_1");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillDRFFormatTemplate(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Date", "");
    templateValues.put("Place", "");
    templateValues.put("Application No", "");
    templateValues.put("Name of applicant", "Anupam Kushwaha");
    templateValues.put("Subject", "");
    templateValues.put("sanction letter date", "");
    templateValues.put("Favouring 1", "");
    templateValues.put("Bank name & Ac no 1", "");
    templateValues.put("Amount 1", "");
    templateValues.put("Paise 1", "");

    templateValues.put("Favouring 2", "");
    templateValues.put("Bank name & Ac no 2", "");
    templateValues.put("Amount 2", "");
    templateValues.put("Paise 2", "");

    templateValues.put("Favouring 3", "");
    templateValues.put("Bank name & Ac no 3", "");
    templateValues.put("Amount 3", "");
    templateValues.put("Paise 3", "");

    templateValues.put("Favouring 4", "");
    templateValues.put("Bank name & Ac no 4", "");
    templateValues.put("Amount 4", "");
    templateValues.put("Paise 4", "");

    templateValues.put("Favouring 5", "");
    templateValues.put("Bank name & Ac no 5", "");
    templateValues.put("Amount 5", "");
    templateValues.put("Paise 5", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillNACH(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("UMRN", "");
    templateValues.put("Date", "");
    templateValues.put("Sponsor Bank Code", "");
    templateValues.put("Utility Code", "");
    templateValues.put("Bank A/C number", "");
    templateValues.put("with bank", "");
    templateValues.put("Reference 1", "");
    templateValues.put("Reference2", "");
    templateValues.put("Phone no", "");
    templateValues.put("IFSC", "");
    templateValues.put("MICR", "");
    templateValues.put("Amount of Rupees", "");
    templateValues.put("Rs", "");
    templateValues.put("Frequency", ""); // Mnthy, Qtly, H-Yrly, Yrly, option_5
    templateValues.put("Rs", "");

    templateValues.put("Email ID", "");
    templateValues.put("From", "");
    templateValues.put("To", "");
    templateValues.put("Or", "");
    templateValues.put("Debit Type", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillAppFormTemplate(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Gross Income", "");
    templateValues.put("Other Income", "");
    templateValues.put("Immovable", "");
    templateValues.put("Securities", "");
    templateValues.put("Other Income", "");
    templateValues.put("Bank Balance", "");
    // total
    templateValues.put("field_name_136", "");
    templateValues.put("Bank Name", "");
    templateValues.put("Account No", "");
    templateValues.put("Branch", "");
    templateValues.put("Overdraft limit", "");
    templateValues.put("No of Years", "");
    templateValues.put("AC/Type", "");
    templateValues.put("field_name_175", ""); // option_1, option_2 option_3
    templateValues.put("field_name_173", ""); // option_1, option_2 option_3

    templateValues.put("Name", "");
    templateValues.put("Date of birth", "");
    templateValues.put("Image", "");
    templateValues.put(
        "Education",
        "under graduate"); // under graduate, graduate, post-graduate, professional, others
    templateValues.put("No of dependents", "children"); // children, parents, others
    templateValues.put("Mailing address", "current"); // current, office
    templateValues.put("If overdraft,Limit", "option_1"); // option_1, option_2, option_3, option_4
    templateValues.put(
        "constitution", "Individual"); // Individual, Proprietor, Partnership, Pvt ltd, other
    templateValues.put("Category", "OBC"); // SC, ST, OBC, General, Minority, other
    templateValues.put("field_name_64", ""); // option_1, option_2
    templateValues.put("Mothers Maiden Name", "");
    templateValues.put("Aadhar No", "");
    templateValues.put("Voter ID", "");
    templateValues.put("Pan no", "");
    templateValues.put("Driving Licence", "");
    templateValues.put("DL Valid Upto", "");
    templateValues.put("Passport No", "");
    templateValues.put("Passport Valid Upto", "");
    templateValues.put("Residence Address line 1", "");
    templateValues.put("Residence Address line 2", "");
    templateValues.put("pin code", "");
    templateValues.put("city", "");
    templateValues.put("State", "");
    templateValues.put("landline no", "");
    templateValues.put("Mobile", "");
    templateValues.put("Residence Accommodation", "Own"); // Own, Family, Rented, Employer
    templateValues.put("Residing since", "");
    templateValues.put("Rent Per month", "");
    templateValues.put("occupation", ""); // business, professional, salaried, retired, housewife
    templateValues.put("Employer name", "");
    templateValues.put("Employer Address", "");
    templateValues.put("Employer Address line2", "");
    templateValues.put("City", "");
    templateValues.put("State 2", "");
    templateValues.put("Landline 2", "");
    templateValues.put("Website", "");
    templateValues.put("Working For", ""); // Central, PSU, MNC, Partnership, Others
    templateValues.put("Driving License 2", "");
    templateValues.put("Department", "");
    templateValues.put("Emp Id", "");
    templateValues.put("Employed Since", "");
    templateValues.put("Total work exp", "");
    templateValues.put("Prev Org", "");
    templateValues.put("If professional", ""); // Doctor, CA/CWA/CS, Architect, Lawyer, Others
    templateValues.put("Others 1", "");
    templateValues.put(
        "If Bussiness", ""); // Manufacturer, Retailed/Trader, Wholesaler, Importer/Exporter, Others
    templateValues.put("Others 2", "");
    templateValues.put("Office Accomodation", ""); // Own, Family, Rented,
    templateValues.put("Operating Since", "");
    templateValues.put("Rent per month", "");
    templateValues.put("Gross annual income", "");
    templateValues.put("Immovable", "");
    templateValues.put("Securities", "");
    templateValues.put("other income 3", "");
    templateValues.put("Bank balance 2", "");
    templateValues.put("Total 1", "");

    templateValues.put("Name of bank", "");
    templateValues.put("Account no 2", "");
    templateValues.put("Branch", "");
    templateValues.put("If overdraft, Limit", "");
    templateValues.put("No of years", "");
    templateValues.put("Ac Type", ""); // Current, Saving, ODCC

    // PAGE 2

    templateValues.put("Application No", "");
    templateValues.put("Date of application", "");
    templateValues.put("Loan Amount", "");
    templateValues.put("Existing Customer", ""); // Yes, No
    templateValues.put("Tenture", "");
    templateValues.put("Resident Indian", ""); // Resident Indian
    templateValues.put("Balance Transfer", ""); // option_1
    templateValues.put("FI Name", "");
    templateValues.put("Home Loan", ""); // Purchase, Construction, Composite, Rennovation
    templateValues.put("Home Equity", ""); // Working Capital, Assest Purchase, others

    templateValues.put("Name 2", ""); // Mr, Mrs, Ms
    templateValues.put("Name field", "");
    templateValues.put("Education 1", "");
    templateValues.put("", "");
    templateValues.put("", "");
    templateValues.put("", "");
    templateValues.put("", "");
    templateValues.put("", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fill11DeclarationCumUndertaking(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Application No", "");
    templateValues.put("Name", "");
    templateValues.put("Property Address", "");
    templateValues.put("Address", "");
    templateValues.put("Name2", "");
    templateValues.put("Ro", "");
    templateValues.put("Property From", "");
    templateValues.put("Date", "");
    templateValues.put("Date2", "");
    templateValues.put("Day of ", "");
    templateValues.put("Year", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fill12DeclarationCumUndertaking(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Executed At", "");
    templateValues.put("on this", "");
    templateValues.put("day of", "");
    templateValues.put("year", "");
    templateValues.put("Name", "");
    templateValues.put("Ro", "");
    templateValues.put("Loan Amount", "");
    templateValues.put("Record", "");
    templateValues.put("Registration No", "");
    templateValues.put("Book number", "");
    templateValues.put("Electricity connection no", "");
    templateValues.put("Date", "");
    templateValues.put("Day of 1", "");
    templateValues.put("Year1", "");
    templateValues.put("Witness", "");
    templateValues.put("Address", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillLOC(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Date", "");
    templateValues.put("Place", "");
    templateValues.put("Name", "");
    templateValues.put("Rs Amount", "");
    templateValues.put("Rupees", "");
    templateValues.put("Rupees2", "");
    templateValues.put("Dated", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillVernacularTemplate(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("field_name_11", "");
    templateValues.put("on this", "");
    templateValues.put("day of", "");
    templateValues.put("year", "");
    templateValues.put("Name", "");
    templateValues.put("Age", "");
    templateValues.put("R/O", "");
    templateValues.put("", "");
    templateValues.put("", "");
    templateValues.put("", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillPlotPurchaseUndertaking(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Application No", "");
    templateValues.put("Name of applicant", "");
    templateValues.put("Property Address", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillDPN(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Date", "");
    templateValues.put("Place", "");
    templateValues.put("Name", "");
    templateValues.put("Sum of rs", "");
    templateValues.put("Rupees in words 2", "");
    templateValues.put("rate of interest", "");
    templateValues.put("margin", "");
    templateValues.put("Rs", "");
    templateValues.put("Rupees in words", "");
    templateValues.put("signed by the above name", "");
    templateValues.put("Image", "");

    template.setTemplate_values(templateValues);
    return template;
  }

  private DocTemplate fillCLSSPmay(String templateId) {
    DocTemplate template = new DocTemplate();
    template.setTemplate_key(templateId);
    Map<String, Object> templateValues = new HashMap<>();

    templateValues.put("Name", "");
    templateValues.put("age1", "");
    templateValues.put("son of", "");
    templateValues.put("Resident1", "");
    templateValues.put("Resident2", "");
    templateValues.put("Age2", "");
    templateValues.put("Year", "");
    templateValues.put("son of", "");
    templateValues.put("resident of", "");
    templateValues.put("application form no", "");
    templateValues.put("category", ""); // EWS, LIG, MIG-1, MIG2
    templateValues.put("property", "");

    template.setTemplate_values(templateValues);
    return template;
  }
}
