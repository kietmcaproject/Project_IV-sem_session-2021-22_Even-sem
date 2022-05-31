package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service("NovelApiService")
public class NovelApiService {
  private static final Logger logger = LoggerFactory.getLogger(NovelApiService.class);

  @Autowired RuntimeService runtimeService;
  @Autowired IbHelper ibHelper;

  public DelegateExecution callNovelApis(DelegateExecution execution)
      throws JSONException, IOException, UnirestException, InterruptedException {
    logger.info("--------------------- in callNovelApis -----------------------");
    Double aDouble = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();
    for (int i = 0; i < applicantCount; i++) {
      try {
        int applicantNo = i + 1;
        logger.info("applicantNo :-" + applicantNo);
        try {
          logger.info(
              "applicantBStatementDetails_hl :- "
                  + execution.getVariable("applicant" + applicantNo + "BStatementDetails_hl"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        JSONArray applicantBStatementDetails_hl =
            CommonHelperFunctions.objectToJSONArray(
                execution
                    .getVariable("applicant" + applicantNo + "BStatementDetails_hl")
                    .toString());
        logger.info("applicantBStatementDetails_hl array :-" + applicantBStatementDetails_hl);
        JSONObject bankStatement = (JSONObject) applicantBStatementDetails_hl.get(0);
        String bankStatementUrlStr =
            StringEscapeUtils.unescapeHtml4(
                bankStatement.getString("applicant" + applicantNo + "6MBankStatement_hl"));
        logger.info("------------ bankStatementUrlStr : {}", bankStatementUrlStr);
        URL bankStatementUrl = new URL(bankStatementUrlStr);
        logger.info("------------ bankStatementUrl : {}", bankStatementUrl);
        downloadFileFromUrl(bankStatementUrl, applicantNo);
        logger.info("------------------downloadFileFromUrl method executed---------------------");
        String data = bankStatementUploadApi(bankStatementUrlStr, applicantNo).getData().toString();
        logger.info("data :- {}", data);
        logger.info("------------------- out uploadFile api ---------------------------------");
        Map<String, String> uploadResponse =
            CommonHelperFunctions.getStringStringHashMapFromJsonString(data);
        String docId = uploadResponse.get("docId");
        logger.info("uploadResponse :- {}", uploadResponse);
        logger.info("docId :- {}", docId);
        execution.setVariable("docId_" + applicantNo, docId);
        runtimeService.setVariable(execution.getProcessInstanceId(), "docId_" + applicantNo, docId);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return execution;
  }

  public ApiResponse bankStatementUploadApi(String bsUrl, int applicantNo) throws IOException {
    logger.info("--------------------- in bankStatementUploadApi ----------------------");
    URL bsUrl1 = new URL(bsUrl);
    String path = "/usr/local/tomcat/fileStorage/applicant" + applicantNo + ".pdf";
    logger.info("path:- {}", path);
    RestTemplate client = new RestTemplate();
    MultipartBodyBuilder apiUploadBuilder = new MultipartBodyBuilder();
    apiUploadBuilder.part("file", new FileSystemResource(path));
    RequestEntity apiUploadRequest =
        RequestEntity.post(URI.create(ibHelper.getUrl("cart-apis/bank-statement-upload-api")))
            .accept(MediaType.APPLICATION_JSON)
            .headers(
                h ->
                    h.set(
                        "Content-Type",
                        "multipart/form-data; boundary=----WebKitFormBoundarygn63zH1NOOwM9e8j"))
            .body(apiUploadBuilder.build());
    ResponseEntity responseEntity = client.exchange(apiUploadRequest, String.class);
    logger.info("apiUploadRequest :-  " + apiUploadRequest.getBody());
    logger.info("response of bankStatementUploadApi :- {} ", responseEntity.getBody());

    return new ApiResponse(HttpStatus.OK, "SUCCESS", responseEntity.getBody());
  }

  public void downloadFileFromUrl(URL bankStatementUrl, int applicantNo) throws IOException {
    logger.info("Bank statement url :- {}", bankStatementUrl);
    //    logger.info("Bank statement url extension :- {}",
    // FilenameUtils.getExtension(bankStatementUrl.getPath()));

    InputStream input;

    int count = 0;
    int maxTries = 3;
    while (true) {
      try {
        logger.info("-------- Try no : {}", count);
        input = bankStatementUrl.openStream();
        break;
      } catch (IOException e) {
        if (++count == maxTries) throw e;
      }
    }

    FileUtils.copyInputStreamToFile(
        input, new File("/usr/local/tomcat/fileStorage/applicant" + applicantNo + ".pdf"));
  }

  public DelegateExecution downloadFileApi(DelegateExecution execution)
      throws UnirestException, JSONException {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();

    logger.info("------------------------ in downloadFile api---------------------");
    for (int i = 1; i <= applicantCount; i++) {
      String docId = execution.getVariable("docId_" + i).toString();
      logger.info("-------- docId : {} ", docId);
      logger.info("------- docId : {}", docId);
      HttpResponse<String> response =
          Unirest.post(ibHelper.getUrl("cart-apis/download-report-data"))
              .header("accept", "application/json")
              .header(
                  "Content-Type",
                  "multipart/form-data; boundary=----WebKitFormBoundarygn63zH1NOOwM9e8j")
              .queryString("raw-text", docId)
              .asString();
      logger.info("-------- HttpResponse : {}", response);
      logger.info("-------- response of downloadFile NovelApi : {} ", response.getBody());
      String dataString = response.getBody();
      logger.info("-------- dataString : {}", dataString);

      JSONArray analysisData = null;
      try {
        JSONObject jsonObject = new JSONObject(dataString);
        //        logger.info("jsonObject :- {}", jsonObject);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        //        logger.info("jsonArray :- {}", jsonArray);
        JSONObject jsonData = (JSONObject) jsonArray.get(0);
        //        logger.info("jsonData :-{}", jsonData);
        analysisData = jsonData.getJSONArray("analysisData");
        logger.info("-------- analysisData :- {}", analysisData);
        variableMapping(execution, analysisData, i);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      execution.setVariable("novelApiResponse_" + i, response.getBody());
    }

    return execution;
  }

  public void variableMapping(DelegateExecution execution, JSONArray analysisDatas, int applicantNo)
      throws JSONException {
    logger.info("------------- IN : variableMapping method of NovelApiService --------------");

    Double emi_hl = 0d, nmi_hl = 0d, abb_hl = 0d;
    int ocr_hl = 0;
    for (int i = 0; i < analysisDatas.length(); i++) {
      JSONObject analysisData = analysisDatas.getJSONObject(i);
      if (analysisData.get("month").toString().equals("Grand Total")) {
        emi_hl = analysisData.getDouble("totalEMIAmount");
        abb_hl = analysisData.getDouble("averageEODBalance");
        ocr_hl = analysisData.getInt("noOfChequeBounceOutward");
      }
    }
    nmi_hl = calcLast3MonthNMIAvg(analysisDatas);

    //    execution.setVariable("emi_hl", emi_hl);
    //    execution.setVariable("nmi_hl", nmi_hl);
    //    execution.setVariable("abb_hl", abb_hl);
    //    execution.setVariable("ocr_hl", ocr_hl);

    execution.setVariable("applicant" + applicantNo + "_EMI", emi_hl);
    execution.setVariable("applicant" + applicantNo + "_NMI", nmi_hl);
    execution.setVariable("applicant" + applicantNo + "_ABB", abb_hl);
    execution.setVariable("applicant" + applicantNo + "_OCR", ocr_hl);

    logger.info("------------- OUT : variable mapping of Novel api response --------------");
  }

  public double calcLast3MonthNMIAvg(JSONArray analysisData) throws JSONException {
    // last 3 months average figure need to be taken
    double last3MonthNMI = 0d;
    for (int i = analysisData.length() - 2; (i >= 0) && (i > analysisData.length() - 5); i--) {
      last3MonthNMI +=
          analysisData.getJSONObject(i).getDouble("salaryAmount")
              + analysisData.getJSONObject(i).getDouble("otherIncomeAmount");
    }
    double avgLast3MonthNMI = last3MonthNMI / 3;
    logger.info("avgLast3MonthNMI :- {}", avgLast3MonthNMI);

    return avgLast3MonthNMI;
  }

  public ApiResponse uploadFile(String password, String bank, String name, MultipartFile file)
      throws IOException {
    logger.info("Multipart file:-" + file.toString());
    File requestFile = new File("src/main/resources/" + file.getOriginalFilename());
    try (OutputStream os = new FileOutputStream(requestFile)) {
      os.write(file.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    RestTemplate client = new RestTemplate();
    MultipartBodyBuilder apiUploadBuilder = new MultipartBodyBuilder();
    apiUploadBuilder.part("password", password);
    apiUploadBuilder.part("bank", bank);
    apiUploadBuilder.part("name", name);
    apiUploadBuilder.part(
        "file", new FileSystemResource("src/main/resources/" + file.getOriginalFilename()));
    RequestEntity apiUploadRequest =
        RequestEntity.post(URI.create(ibHelper.getUrl("cart-apis/bank-statement-upload-api")))
            .accept(MediaType.APPLICATION_JSON)
            .headers(
                h ->
                    h.set(
                        "Content-Type",
                        "multipart/form-data; boundary=----WebKitFormBoundarygn63zH1NOOwM9e8j"))
            .body(apiUploadBuilder.build());
    ResponseEntity responseEntity = client.exchange(apiUploadRequest, String.class);
    logger.info(client.exchange(apiUploadRequest, String.class).getBody());
    requestFile.delete();
    return new ApiResponse(HttpStatus.OK, "SUCCESS", responseEntity.getBody());
  }

  public void variableMappingTest(
      /*DelegateExecution execution, */ JSONArray downloadResponse, int applicantNo)
      throws JSONException {
    Double emi_hl = 0d, nmi_hl = 0d, abb_hl = 0d, ocr_hl = 0d;
    JSONObject jsonObject = downloadResponse.getJSONObject(applicantNo - 1);
    JSONArray analysisDatas = jsonObject.getJSONArray("analysisData");
    for (int i = 0; i < analysisDatas.length(); i++) {
      JSONObject analysisData = analysisDatas.getJSONObject(i);
      if (analysisData.get("month").toString().equals("Grand Total")) {
        emi_hl = analysisData.getDouble("totalEMIAmount");
        nmi_hl =
            analysisData.getDouble("salaryAmount") + analysisData.getDouble("otherIncomeAmount");
        abb_hl = analysisData.getDouble("averageEODBalance");
        ocr_hl = analysisData.getDouble("noOfChequeBounceOutward");
      }
    }
    logger.info("emi : " + emi_hl);
    logger.info("nmi : " + nmi_hl);
    logger.info("abb : " + abb_hl);
    logger.info("ocr : " + ocr_hl);
    //    execution.setVariable("emi_hl", emi_hl);
    //    execution.setVariable("nmi_hl", nmi_hl);
    //    execution.setVariable("abb_hl", abb_hl);
    //    execution.setVariable("ocr_hl", ocr_hl);
    //
    //    execution.setVariable("applicant"+ applicantNo +"_EMI", execution.getVariable("emi_hl"));
    //    execution.setVariable("applicant"+ applicantNo +"_NMI", execution.getVariable("nmi_hl"));
    //    execution.setVariable("applicant"+ applicantNo +"_ABB", execution.getVariable("abb_hl"));
    //    execution.setVariable("applicant"+ applicantNo +"_OCR", execution.getVariable("ocr_hl"));
  }

  public void callNovelApisTest(JSONObject request, int applicantNo)
      throws JSONException, IOException, UnirestException, InterruptedException {
    logger.info("--------------------- in callNovelApis -----------------------");
    //    int applicantNo =
    // Integer.parseInt(execution.getVariable("breLoopCount_hl").toString().trim());
    try {
      JSONArray applicantBStatementDetails_hl =
          (JSONArray) request.get("applicant" + applicantNo + "BStatementDetails_hl");
      logger.info("applicantBStatementDetails_hl :-" + applicantBStatementDetails_hl);
      JSONObject bankStatement = (JSONObject) applicantBStatementDetails_hl.get(0);
      String bankStatementUrl =
          bankStatement.getString("applicant" + applicantNo + "6MBankStatement_hl");
      logger.info("bankStatementUrl :-" + bankStatementUrl);
      downloadFileFromUrl(new URL(bankStatementUrl), applicantNo);
      logger.info("------------------downloadFileFromUrl method executed---------------------");
      String data = bankStatementUploadApi(bankStatementUrl, applicantNo).getData().toString();
      logger.info("------------------- out uploadFile api ---------------------------------");
      Map<String, String> uploadResponse =
          CommonHelperFunctions.getStringStringHashMapFromJsonString(data);
      String docId = uploadResponse.get("docId");
      //      Object downloadResponse = downloadFileApi(docId).getData();
      //      variableMapping(execution, CommonHelperFunctions.objectToJSONArray(downloadResponse),
      // applicantNo);
      logger.info(
          " ------------------ variable mapping of Novel api response done ---------------------");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  //  public static void main(String[] args) throws IOException, JSONException, UnirestException {
  //    //      String q =
  //    //
  // "https://drive.google.com/uc?export=download&id=1fE-4F-Tja3hR6vSDS-am_6ACOzWgEgzy";
  //    //      System.out.println(StringEscapeUtils.unescapeHtml4(q));
  //    String response =
  //        FileUtils.readFileToString(new File("/home/kuliza-523/newDownloadApiResponse.json"));
  //
  //    JSONObject jsonObject = new JSONObject(response);
  //    String jsonString = jsonObject.get("data").toString();
  //    JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
  //    System.out.println("jsonArray: " + jsonArray);
  //    JSONObject jsonData = (JSONObject) jsonArray.get(0);
  //    JSONArray analysisData = jsonData.getJSONArray("analysisData");
  //    System.out.println("analysisData: " + analysisData);
  //
  //    Double otherIncomeAmount =
  //        new JSONObject(analysisData.get(0).toString()).getDouble("otherIncomeAmount");
  //    System.out.println(otherIncomeAmount);
  //  }
}
