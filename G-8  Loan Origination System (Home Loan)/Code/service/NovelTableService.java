package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.CapriUtilService;
import java.util.Date;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.engine.RuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("NovelTableService")
public class NovelTableService {
  private static final Logger logger = LoggerFactory.getLogger(NovelTableService.class);

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Autowired CapriUtilService capriUtilService;

  @Autowired RuntimeService runtimeService;

  public DelegatePlanItemInstance populate(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    logger.info("inside NovelTableService - populate method");
    String caseId = planItemInstance.getCaseInstanceId();
    Double aDouble =
        Double.parseDouble(cmmnRuntimeService.getVariable(caseId, "applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();
    Map<String, Object> variables = planItemInstance.getVariables();

    JSONArray novelStatementTable = new JSONArray();
    for (int i = 1; i <= applicantCount; i++) {
      boolean hasNovel = cmmnRuntimeService.hasVariable(caseId, "novelApiResponse_" + i);
      logger.info("hasNovel variable for applicant {}: {}", i, hasNovel);
      String novelApiResponseStr = variables.get("novelApiResponse_" + i).toString();
      logger.info("novelApiResponseStr : {}", novelApiResponseStr);
      JSONObject novelApiResponse = new JSONObject(novelApiResponseStr);
      if (hasNovel && novelApiResponse.length() != 0) {
        logger.info("novelApiResponse for applicant {}: {}", i, novelApiResponse);
        JSONArray emiArray =
            novelApiResponse.getJSONArray("data").getJSONObject(0).getJSONArray("emi");
        logger.info("novel api emiArray : {}", emiArray);
        for (int j = 0; j < emiArray.length(); j++) {
          JSONArray emiTransactions = emiArray.getJSONObject(j).getJSONArray("transactions");
          for (int k = 0; k < emiTransactions.length(); k++) {
            JSONObject row = new JSONObject();
            JSONObject transaction = emiTransactions.getJSONObject(k);
            row.put("applicant" + i + "TypeNovel_hl", transaction.get("type"));
            row.put("applicant" + i + "PaymentCatNovel_hl", transaction.get("paymentCategory"));
            Date date =
                capriUtilService.unixTimestampToDate(transaction.get("transactionDate").toString());
            String transactionDateTime =
                CommonHelperFunctions.getDateInFormat(date, "dd/MM/yyyy hh:mm:ss");
            row.put("applicant" + i + "DateNovel_hl", transactionDateTime);
            row.put("applicant" + i + "EmiAmountNovel_hl", transaction.get("amount"));
            row.put("applicant" + i + "DNarrationNovel_hl", transaction.get("narration"));
            novelStatementTable.put(row);
          }
        }
      }
      logger.info("novelStatementTable values for applicant {} : {}", i, novelStatementTable);
      cmmnRuntimeService.setVariable(
          caseId, "applicant" + i + "NovelStatementTable_hl", novelStatementTable.toString());
    }
    logger.info("exiting NovelTableService - populate method");
    return planItemInstance;
  }
}
