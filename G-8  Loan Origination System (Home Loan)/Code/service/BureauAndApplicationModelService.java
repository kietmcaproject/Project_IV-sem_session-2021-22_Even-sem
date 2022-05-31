package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("BureauAndApplicationModelService")
public class BureauAndApplicationModelService {
  private static final Logger logger =
      LoggerFactory.getLogger(BureauAndApplicationModelService.class);

  public DelegateExecution rejectReasonCodesMapping(DelegateExecution execution)
      throws JSONException {
    Double aDouble = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();

    // Bureau model
    for (int i = 1; i <= applicantCount; i++) {
      boolean isCibilScoreNegative =
          CommonHelperFunctions.getStringValue(execution.getVariable("isCibilScoreNegative"))
              .equalsIgnoreCase("yes");
      if (execution.getVariable("isApplicant" + i + "NF").toString().equalsIgnoreCase("no")
          && !isCibilScoreNegative) {
        String bureauModelStr =
            execution.getVariable("applicant" + i + "bureauModel8Cap").toString();
        logger.info(" ----------- bureauModelStr{} : {}", i, bureauModelStr);
        JSONArray bureauModel = new JSONArray(bureauModelStr);

        HashMap<String, Double> map = new HashMap<>();
        for (int j = 0; j < bureauModel.length(); j++) {
          if (!bureauModel
              .getJSONObject(j)
              .get("impactOfBM_hl")
              .toString()
              .equalsIgnoreCase("Ignore")) {
            String rrc =
                StringEscapeUtils.unescapeHtml4(
                    bureauModel.getJSONObject(j).get("rejectReasonCode_hl").toString());
            String rrcUnescaped = StringEscapeUtils.unescapeHtml4(rrc);
            double contribution =
                Double.parseDouble(bureauModel.getJSONObject(j).get("Contribution_hl").toString());
            map.put(rrcUnescaped, contribution);
          }
        }
        Map<String, Double> bureauModelCodes =
            map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        logger.info("---------------- bureauModelCodes : {}", bureauModelCodes);
        StringBuilder bureauModelRejectCodes = new StringBuilder();
        for (Map.Entry<String, Double> entry : bureauModelCodes.entrySet()) {
          bureauModelRejectCodes.append(entry.getKey());
          bureauModelRejectCodes.append(";");
        }
        bureauModelRejectCodes.deleteCharAt(bureauModelRejectCodes.length() - 1);
        String bureauRRC = new String(bureauModelRejectCodes);
        logger.info(
            "------------------ formatted bureauModelRejectCodes : {}", bureauModelRejectCodes);
        execution.setVariable("applicant" + i + "BureauModelRejectcodes", bureauModelRejectCodes);
        execution.setVariable("applicant1BMRC", bureauModelRejectCodes);
      }
    }

    // Application model
    for (int i = 1; i <= applicantCount; i++) {
      if (execution.getVariable("isApplicant" + i + "NF").toString().equalsIgnoreCase("no")) {
        String applicationModelStr =
            execution.getVariable("applicant" + i + "applicationModel1Cap").toString();
        JSONArray applicationModel = new JSONArray(applicationModelStr);

        HashMap<String, Double> map = new HashMap<>();
        for (int j = 0; j < applicationModel.length(); j++) {
          if (!applicationModel
              .getJSONObject(j)
              .get("impactofAM_hl")
              .toString()
              .equalsIgnoreCase("Ignore")) {
            String rrc =
                StringEscapeUtils.unescapeHtml4(
                    applicationModel.getJSONObject(j).get("rejectReasonCodeAm_hl").toString());
            String rrcUnescaped = StringEscapeUtils.unescapeHtml4(rrc);
            double contribution =
                Double.parseDouble(
                    applicationModel.getJSONObject(j).get("ContributionAm_hl").toString());
            map.put(rrcUnescaped, contribution);
          }
        }
        Map<String, Double> applicationModelCodes =
            map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        logger.info("---------------- applicationModelCodes : {}", applicationModelCodes);
        StringBuilder applicationModelRejectCodes = new StringBuilder();
        for (Map.Entry<String, Double> entry : applicationModelCodes.entrySet()) {
          applicationModelRejectCodes.append(entry.getKey());
          applicationModelRejectCodes.append(";");
        }
        applicationModelRejectCodes.deleteCharAt(applicationModelRejectCodes.length() - 1);

        logger.info(
            "------------------ formatted applicationModelRejectCodes : {}",
            applicationModelRejectCodes);
        execution.setVariable(
            "applicant" + i + "ApplicationModelRejectcodes", applicationModelRejectCodes);
      }
    }
    return execution;
  }
}
