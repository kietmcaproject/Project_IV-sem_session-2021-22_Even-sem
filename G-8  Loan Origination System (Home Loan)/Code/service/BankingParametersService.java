package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("BankingParametersService")
public class BankingParametersService {
  private static final Logger logger = LoggerFactory.getLogger(BankingParametersService.class);
  private double totalEMI = 0d;
  private double totalNMI = 0d;
  private double totalABB = 0d;

  public DelegateExecution ratioCalculation(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();
    logger.info("--------------- applicantCount : {} ", applicantCount);

    customerSchemeMapping(execution);
    double emiNmiRatio = calculateEMItoNmiRatio(execution);
    double abbEmiRatio = calculateABBtoEMIRatio(execution);
    int numberOfOCR = countOutwardChequeReturns(execution);

    if (emiNmiRatio > 0.9) {
      execution.setVariable("rejectReasonCodeB_hl", "EMI/ NMI ratio below threshold");
      execution.setVariable("applicationStatus", "Rejected");
      execution.setVariable("caseStatus_hl", "Rejected");
    } else if (abbEmiRatio < 0.1) {
      String applicantCustomerScheme =
          CommonHelperFunctions.getStringValue(execution.getVariable("customerSchemeApp1_hl"));

      if (applicantCustomerScheme.equalsIgnoreCase("CAT B")
          || applicantCustomerScheme.equalsIgnoreCase("CAT C")) {
        execution.setVariable(" rejectReasonCodeB_hl", "ABB/ EMI ratio below threshold");
        execution.setVariable("applicationStatus", "Rejected");
        execution.setVariable("caseStatus_hl", "Rejected");

      } else if (applicantCustomerScheme.equalsIgnoreCase("CAT A")) {
        for (int i = 2; i <= applicantCount; i++) {
          String coApplicantCustomerScheme =
              CommonHelperFunctions.getStringValue(
                  execution.getVariable("customerSchemeApp" + i + "_hl"));
          if (coApplicantCustomerScheme.equalsIgnoreCase("CAT B")
              || coApplicantCustomerScheme.equalsIgnoreCase("CAT C")) {
            execution.setVariable("applicationStatus", "Refer to Credit");
            break;
          }
        }
      }
    } else if (numberOfOCR > 1) {
      execution.setVariable("rejectReasonCodeB_hl", "Outward Cheque Returns more than norms");
      execution.setVariable("applicationStatus", "Rejected");
      execution.setVariable("caseStatus_hl", "Rejected");
    }

    return execution;
  }

  private void customerSchemeMapping(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();
    logger.info("--------------- applicantCount : {} ", applicantCount);

    for (int i = 1; i <= applicantCount; i++) {
      String employmentType =
          CommonHelperFunctions.getStringValue(
              execution.getVariable("employmentType" + i + "_app"));
      if (employmentType == null || employmentType.equals("")) {
        execution.setVariable("customerSchemeApp" + i + "_hl", "NA");
      } else if (employmentType.equalsIgnoreCase("Class 1")
          || employmentType.equalsIgnoreCase("Class 2")) {
        execution.setVariable("customerSchemeApp" + i + "_hl", "CAT C");
      } else if (employmentType.equalsIgnoreCase("Class 3")) {
        execution.setVariable("customerSchemeApp" + i + "_hl", "CAT A");
      } else if (employmentType.equalsIgnoreCase("Class 4")) {
        execution.setVariable("customerSchemeApp" + i + "_hl", "CAT B");
      }
    }

    boolean isApplicantCatAorB;
    isApplicantCatAorB =
        execution.getVariable("customerSchemeApp1_hl").equals("CAT A")
            || execution.getVariable("customerSchemeApp1_hl").equals("CAT B");
    execution.setVariable("isApplicantCatAorB", isApplicantCatAorB);
  }

  public double calculateEMItoNmiRatio(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();
    logger.info("--------------- applicantCount : {} ", applicantCount);
    // Calculation of EMI for applicant and co-applicant
    double existingEMI = 0d;
    double proposedEMI = 0d;
    for (int i = 1; i <= applicantCount; i++) {
      existingEMI += Double.parseDouble(execution.getVariable("applicant" + i + "_EMI").toString());
    }
    double p = Double.parseDouble(execution.getVariable("loanAmount_hl").toString());
    double r = Double.parseDouble(execution.getVariable("expectedRoi_hl").toString());
    double n = Double.parseDouble(execution.getVariable("loanTenure_hl").toString());
    double rate = r / 1200;

    proposedEMI = (p * rate * Math.pow(1 + rate, n)) / (Math.pow(1 + rate, n) - 1);

    execution.setVariable("proposed_EMI", proposedEMI);

    totalEMI = existingEMI + proposedEMI;

    // Calculation NMI of applicant and co-applicant
    for (int i = 1; i <= applicantCount; i++) {
      totalNMI += Double.parseDouble(execution.getVariable("applicant" + i + "_NMI").toString());
    }

    double emiNmiRatio = totalEMI / totalNMI;

    execution.setVariable("totalExistingEMI_hl", existingEMI);
    execution.setVariable("totalEMI_hl", totalEMI);
    execution.setVariable("totalNMI_hl", totalNMI);
    if (emiNmiRatio > Double.MAX_VALUE) {
      emiNmiRatio = Double.MAX_VALUE;
    }
    execution.setVariable("emiNmiRatio_hl", emiNmiRatio);

    logger.info("-------------- existingEMI : {} ", existingEMI);
    logger.info("-------------- proposedEMI : {} ", proposedEMI);
    logger.info("-------------- totalEMI : {} ", totalEMI);
    logger.info("-------------- totalNMI : {} ", totalNMI);
    logger.info("-------------- emiNmiRatio : {} ", emiNmiRatio);

    return emiNmiRatio;
  }

  private double calculateABBtoEMIRatio(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();

    logger.info("--------------- applicantCount : {} ", applicantCount);

    for (int i = 1; i <= applicantCount; i++) {
      totalABB += Double.parseDouble(execution.getVariable("applicant" + i + "_ABB").toString());
    }

    double abbEmiRatio = totalABB / totalEMI;

    execution.setVariable("totalABB_hl", totalABB);
    execution.setVariable("abbEmiRatio_hl", abbEmiRatio);

    logger.info("-------------- totalABB : {} ", totalABB);
    logger.info("-------------- totalEMI : {} ", totalEMI);
    logger.info("-------------- abbEmiRatio : {} ", abbEmiRatio);

    return abbEmiRatio;
  }

  private int countOutwardChequeReturns(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();
    int ocrCount = 0;
    for (int i = 1; i <= applicantCount; i++) {
      String applicantType = execution.getVariable("applicant" + i + "ApplicantType_hl").toString();
      if (applicantType.equalsIgnoreCase("Primary Applicant")
          || applicantType.equalsIgnoreCase("Financial Co-Applicant")) {
        ocrCount += Integer.parseInt(execution.getVariable("applicant" + i + "_OCR").toString());
      }
    }

    execution.setVariable("ocrCount_hl", ocrCount);

    logger.info("-------------- ocrCount : {} ", ocrCount);

    return ocrCount;
  }
}
