package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("FeeAndChargeService")
public class FeeAndChargeService {
  private static final Logger logger = LoggerFactory.getLogger(FeeAndChargeService.class);

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public DelegatePlanItemInstance calculate(DelegatePlanItemInstance planItemInstance)
      throws JSONException {
    logger.info("inside fee and charge service - evaluate method");
    String caseId = planItemInstance.getCaseInstanceId();

    double gst = 18.0;
    JSONArray feeDetailsTable = new JSONArray();
    JSONObject row1 = new JSONObject();
    row1.put("chargeDescription", "PF");
    row1.put("chargeAmount", 2000);
    row1.put("gstApplicable", gst);
    row1.put("chargeWithGST", 2000 + (2000 * gst) / 100);
    feeDetailsTable.put(row1);

    double loanAmount =
        Double.parseDouble(cmmnRuntimeService.getVariable(caseId, "loanAmount_hl").toString());
    int cersaiCharge = loanAmount <= 500000 ? 50 : 100;
    JSONObject row2 = new JSONObject();
    row2.put("chargeDescription", "CERSAI Charges");
    row2.put("chargeAmount", cersaiCharge);
    row2.put("gstApplicable", gst);
    row2.put("chargeWithGST", cersaiCharge + ((cersaiCharge * gst) / 100));
    feeDetailsTable.put(row2);

    JSONObject row3 = new JSONObject();
    row3.put("chargeDescription", "Legal Opinion Charges");
    row3.put("chargeAmount", 2500);
    row3.put("gstApplicable", gst);
    row3.put("chargeWithGST", 2500 + ((2500 * gst) / 100));
    feeDetailsTable.put(row3);

    JSONObject row4 = new JSONObject();
    row4.put("chargeDescription", "Valuation Charges");
    row4.put("chargeAmount", 2000);
    row4.put("gstApplicable", gst);
    row4.put("chargeWithGST", 2000 + ((2000 * gst) / 100));
    feeDetailsTable.put(row4);

    JSONObject row5 = new JSONObject();
    row5.put("chargeDescription", "RCU Charges");
    row5.put("chargeAmount", 750);
    row5.put("gstApplicable", gst);
    row5.put("chargeWithGST", 750 + ((750 * gst) / 100));
    feeDetailsTable.put(row5);

    JSONObject row6 = new JSONObject();
    row6.put("chargeDescription", "ROC lien updation charges will be charged if Applicable");
    row6.put("chargeAmount", 0);
    row6.put("gstApplicable", gst);
    row6.put("chargeWithGST", 0);
    feeDetailsTable.put(row6);

    // TODO: Utkarsh to provide variable
    double imdCharges =
        Double.parseDouble(
            CommonHelperFunctions.getStringValueOrDefault(
                cmmnRuntimeService.getVariable(caseId, "imdCharges_hl"), "0"));
    JSONObject row7 = new JSONObject();
    row7.put("chargeDescription", "IMD Charges");
    row7.put("chargeAmount", 0);
    row7.put("gstApplicable", gst);
    row7.put("chargeWithGST", 0);
    feeDetailsTable.put(row7);

    String endUseOfLoan =
        String.valueOf(cmmnRuntimeService.getVariable(caseId, "endUseOfLoanString_hl"));
    if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
      JSONObject row8 = new JSONObject();
      row8.put("chargeDescription", "Legal Handling Charges");
      row8.put("chargeAmount", 1500);
      row8.put("gstApplicable", gst);
      row8.put("chargeWithGST", 1500 + ((1500 * gst) / 100));
      feeDetailsTable.put(row8);
    }

    String salariedUnderSAHAJ = String.valueOf(cmmnRuntimeService.getVariable(caseId, "sahajLoan"));
    if (salariedUnderSAHAJ.equalsIgnoreCase("yes")) {
      double docHandlingAndAdminCharge = (loanAmount * 2) / 100;
      JSONObject row9 = new JSONObject();
      row9.put("chargeDescription", "Document Handling and Administrative Charges");
      row9.put("chargeAmount", docHandlingAndAdminCharge);
      row9.put("gstApplicable", gst);
      row9.put(
          "chargeWithGST", docHandlingAndAdminCharge + ((docHandlingAndAdminCharge * gst) / 100));
      feeDetailsTable.put(row9);

    } else {
      boolean isApplicantCatAorB =
          Boolean.parseBoolean(
              cmmnRuntimeService.getVariable(caseId, "isApplicantCatAorB").toString());
      if (isApplicantCatAorB) {
        double docHandlingAndAdminCharge = loanAmount / 100;
        JSONObject row9 = new JSONObject();
        row9.put("chargeDescription", "Document Handling and Administrative Charges");
        row9.put("chargeAmount", docHandlingAndAdminCharge);
        row9.put("gstApplicable", gst);
        row9.put(
            "chargeWithGST", docHandlingAndAdminCharge + ((docHandlingAndAdminCharge * gst) / 100));
        feeDetailsTable.put(row9);

      } else {
        double docHandlingAndAdminCharge = (loanAmount * 1.5) / 100;
        JSONObject row9 = new JSONObject();
        row9.put("chargeDescription", "Document Handling and Administrative Charges");
        row9.put("chargeAmount", docHandlingAndAdminCharge);
        row9.put("gstApplicable", gst);
        row9.put(
            "chargeWithGST", docHandlingAndAdminCharge + ((docHandlingAndAdminCharge * gst) / 100));
        feeDetailsTable.put(row9);
      }
    }

    logger.info("feeDetailsEditable AFTER: {}", feeDetailsTable);
    cmmnRuntimeService.setVariable(caseId, "feeDetailsEditable", feeDetailsTable.toString());
    return planItemInstance;
  }
}
