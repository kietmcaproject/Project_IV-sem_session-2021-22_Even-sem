package com.kuliza.workbench.service;

import com.google.gson.Gson;
import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.CapriUtilService;
import com.kuliza.workbench.util.IbHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DmsService {
  private static final Logger logger = LoggerFactory.getLogger(DmsService.class);
  @Autowired CapriUtilService capriUtilService;
  @Autowired IbHelper ibHelper;
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public Map<String, Object> uploadDoc(MultipartFile doc, Map<String, String> request)
      throws JSONException, IOException, UnirestException {
    String base64 = capriUtilService.fileToBase64(doc);
    String applicationNumber =
        CommonHelperFunctions.getStringValue(request.get("applicationNumber"));
    String loanAccountNumber = "LN" + applicationNumber.substring(2);
    String customerNumber = CommonHelperFunctions.getStringValue(request.get("customerNumber"));
    String appCustNo = applicationNumber + "-" + customerNumber;
    String product = "Home Loan";
    String customerName = CommonHelperFunctions.getStringValue(request.get("customerName"));
    String pan = CommonHelperFunctions.getStringValue(request.get("pan"));
    String customerType = CommonHelperFunctions.getStringValue(request.get("customerType"));
    String customerCategory = CommonHelperFunctions.getStringValue(request.get("customerCategory"));
    String branchWithCode = CommonHelperFunctions.getStringValue(request.get("branchWithCode"));
    String thirdPartyFileName =
        CommonHelperFunctions.getStringValue(request.get("third_party_file_name"));
    String docSetName = CommonHelperFunctions.getStringValue(request.get("docSetName"));

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("applicationNumber", applicationNumber);
    jsonObject.put("loanAccountNumber", loanAccountNumber);
    jsonObject.put("customerNumber", customerNumber);
    jsonObject.put("appCustNo", appCustNo);
    jsonObject.put("product", product);
    jsonObject.put("customerName", customerName);
    jsonObject.put("pan", pan);
    jsonObject.put("customerType", customerType);
    jsonObject.put("customerCategory", customerCategory);
    jsonObject.put("branchWithCode", branchWithCode);
    jsonObject.put("uploadedDocuments", uploadedDocArray(base64, thirdPartyFileName, docSetName));
    JSONArray jsonArray = new JSONArray();
    jsonArray.put(jsonObject);
    JSONObject payload = uploadPayload(jsonArray);
    logger.info("payload :- {}", payload);

    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("dms-s3/upload-doc-as-content-against-an-index-master-value"))
            .header("Content-Type", "application/json")
            .body(payload)
            .asString();
    logger.info("Response :- {}", response.getBody());

    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    JSONObject object = new JSONObject(new Gson().toJson(res));
    String primaryDocId =
        object
            .getJSONArray("response")
            .getJSONObject(0)
            .getJSONObject("uploadedDocuments")
            .getJSONArray("document_details")
            .getJSONObject(0)
            .getString("primary_doc_id");
    String doc_preview_uri =
        object
            .getJSONArray("response")
            .getJSONObject(0)
            .getJSONObject("uploadedDocuments")
            .getJSONArray("document_details")
            .getJSONObject(0)
            .getString("doc_preview_uri");
    String doc_download_uri =
        object
            .getJSONArray("response")
            .getJSONObject(0)
            .getJSONObject("uploadedDocuments")
            .getJSONArray("document_details")
            .getJSONObject(0)
            .getString("doc_download_uri");
    Map<String, Object> map = new HashMap<>();
    map.put("url", doc_download_uri);
    map.put("timeout", "");
    return map;
  }

  public ApiResponse getListOfDoc(Map<String, String> request)
      throws JSONException, UnirestException {
    String caseInstanceId = CommonHelperFunctions.getStringValue(request.get("caseInstanceId"));
    String applicationNumber = cmmnRuntimeService.getVariable(caseInstanceId, "los_id").toString();
    String customerNumber =
        cmmnRuntimeService.getVariable(caseInstanceId, "applicationId_hl").toString();
    String appCustNo = applicationNumber + "-" + customerNumber;
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("appCustNo", appCustNo);
    JSONArray jsonArray = new JSONArray();
    jsonArray.put(jsonObject);
    JSONObject payload = listOfDocPayload(jsonArray);
    logger.info("payload :- {}", payload);
    HttpResponse<String> response =
        Unirest.post(ibHelper.getUrl("dms-s3/get-list-of-docs-against-an-index-master-value"))
            .header("Content-Type", "application/json")
            .body(payload)
            .asString();
    logger.info("Response :- {}", response.getBody());

    Map<String, Object> res = CommonHelperFunctions.getHashMapFromJsonString(response.getBody());
    return new ApiResponse(HttpStatus.OK, "Success", res);
  }

  private JSONObject listOfDocPayload(JSONArray jsonArray) throws JSONException {
    JSONObject payload = new JSONObject();
    payload.put("masterDetails", jsonArray);
    return payload;
  }

  private JSONArray uploadedDocArray(String base64, String thirdPartyFileName, String docSetName)
      throws JSONException {
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("docSetName", docSetName);
    jsonObject.put("third_party_file_content", base64);
    jsonObject.put("third_party_file_name", thirdPartyFileName);
    jsonArray.put(jsonObject);
    return jsonArray;
  }

  private JSONObject uploadPayload(JSONArray jsonArray) throws JSONException {

    JSONObject payload = new JSONObject();
    payload.put("masterDetails", jsonArray);
    return payload;
  }
}
