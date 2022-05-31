package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("EligibilityCriteriaService")
public class EligibilityCriteriaService {

  private static final Logger logger = LoggerFactory.getLogger(EligibilityCriteriaService.class);

  private static final int THIRTY_LACS = 3000000;
  private static final int SEVENTY_FIVE_LACS = 7500000;

  private static final DecimalFormat decimalFormat = new DecimalFormat("#");

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  public DelegateExecution calculate(DelegateExecution execution) {
    // For non BT Loans
    logger.info("--------- EligibilityCriteriaService - calculate method -------------");
    double propertyValueTechApp =
        Double.parseDouble(String.valueOf(execution.getVariable("totalValuation")));
    double hl = Double.parseDouble(String.valueOf(execution.getVariable("loanAmount_hl")));
    String endUseOfLoan = String.valueOf(execution.getVariable("endUseOfLoanString_hl"));
    String salariedUnderSAHAJ = String.valueOf(execution.getVariable("sahajLoan"));
    int loanToValuePercent = 0;

    logger.info("---- propertyValueTechApp : {}", propertyValueTechApp);
    logger.info("---- homeLoan amount : {}", hl);
    logger.info("---- end use of loan : {}", endUseOfLoan);
    logger.info("---- salariedUnderSahaj : {}", salariedUnderSAHAJ);

    // ltv table

    if (endUseOfLoan.equalsIgnoreCase("Self-construction")
        || endUseOfLoan.equalsIgnoreCase("HL Refinance")) {
      if (hl <= THIRTY_LACS) {
        if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
          loanToValuePercent = 80;
        } else {
          loanToValuePercent = 75;
        }
      } else {
        if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
          loanToValuePercent = 77;
        } else {
          loanToValuePercent = 75;
        }
      }
    } else {
      if (endUseOfLoan.equalsIgnoreCase("Purchase of House for Self-usage")
          || endUseOfLoan.equalsIgnoreCase("Purchase of House for investment")) {
        if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
          loanToValuePercent = 60;
        } else {
          //  'na' means this case will not arise
        }
      }

      if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
        if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
          loanToValuePercent = 75;
        } else {
          loanToValuePercent = 75;
        }
      }

      if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
        if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
          loanToValuePercent = 70;
        } else {
          loanToValuePercent = 70;
        }
      }
    }
    logger.info("---- ltv : {}", loanToValuePercent);

    Double aDouble = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();
    double monthlyCombinedIncome = 0d;
    int foir = 0;
    for (int i = 1; i <= applicantCount; i++) {
      monthlyCombinedIncome +=
          Double.parseDouble(String.valueOf(execution.getVariable("totalEligibleIncome" + i)));
    }
    logger.info("---- monthlyCombinedIncome : {}", monthlyCombinedIncome);

    if (monthlyCombinedIncome <= 25000) {
      foir = 45;
    } else if (monthlyCombinedIncome <= 50000) {
      foir = 50;
    } else {
      foir = 55;
    }
    logger.info("---- foir : {}", foir);

    execution.setVariable("ltvMain_hl", loanToValuePercent);
    execution.setVariable("foirMain_hl", foir);

    finalEligibleLoanAmountForBPMN(execution, loanToValuePercent, foir);

    return execution;
  }

  public void finalEligibleLoanAmountForBPMN(DelegateExecution execution, int ltv, int foir) {
    logger.info(
        "--------- EligibilityCriteriaService - finalEligibleLoanAmountForBPMN method -------------");

    double A = 0d;
    double B = 0d;
    double C = 0d;
    double D = 0d;

    // Calculating A
    double propertyValueTechApp =
        Double.parseDouble(String.valueOf(execution.getVariable("totalValuation")));

    A = (propertyValueTechApp * ltv) / 100;
    logger.info("---- A : {}", A);

    // Calculating B = Eligible loan amount as per monthly income (Lmi)

    Double aDouble = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();
    double monthlyCombinedIncome = 0d;
    for (int i = 1; i <= applicantCount; i++) {
      monthlyCombinedIncome +=
          Double.parseDouble(String.valueOf(execution.getVariable("totalEligibleIncome" + i)));
    }
    logger.info("---- monthlyCombinedIncome : {}", monthlyCombinedIncome);
    Double currentObligations =
        Double.parseDouble(execution.getVariable("monthlyObligation_EMI").toString());
    logger.info("---- currentObligations : {}", currentObligations);

    double a = ((monthlyCombinedIncome * foir) / 100) - currentObligations;
    logger.info("---- a : {}", a);

    double roi = Double.parseDouble(String.valueOf(execution.getVariable("roiFD_hl")));
    int tenureInMonths = Integer.parseInt(String.valueOf(execution.getVariable("loanTenure_hl")));
    logger.info("---- roi : {} , tenureInMonths : {}", roi, tenureInMonths);

    roi = roi / 100;
    double b = a * (Math.pow(1 + (roi / 12), tenureInMonths) - 1);
    double c = b / (Math.pow(1 + (roi / 12), tenureInMonths));

    B = c / (roi / 12);
    B = Double.parseDouble(decimalFormat.format(B));

    logger.info("---- b : {}", b);
    logger.info("---- c : {}", c);
    logger.info("---- B : {}", B);

    // Calculating C

    double registryAmount =
        Double.parseDouble(execution.getVariable("registryAmount_hl").toString());
    C = 1.4 * registryAmount;

    logger.info("---- registryAmount : {}", registryAmount);
    logger.info("---- C : {}", C);
    // Calculating D
    D = Double.parseDouble(String.valueOf(execution.getVariable("loanAmount_hl")));
    logger.info("---- D : {}", D);

    double X = Math.min(Math.min(A, B), Math.min(C, D));
    logger.info("---- X (Loan as per lower of A, B, C, or D) : {}", X);

    double technicalValuation =
        Double.parseDouble(String.valueOf(execution.getVariable("totalValuation")));

    int percentMultiplier = 0;

    // new table for getting %  multiplier
    String customerScheme =
        CommonHelperFunctions.getStringValue(execution.getVariable("customerSchemeApp1_hl"));
    double loanAmount = Double.parseDouble(String.valueOf(execution.getVariable("loanAmount_hl")));
    String endUseOfLoan = String.valueOf(execution.getVariable("endUseOfLoanString_hl"));
    String salariedUnderSAHAJ = String.valueOf(execution.getVariable("sahajLoan"));

    if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
      if (customerScheme.equalsIgnoreCase("CAT A") || customerScheme.equalsIgnoreCase("CAT B")) {
        if (endUseOfLoan.equalsIgnoreCase("Self-construction")
            || endUseOfLoan.equalsIgnoreCase("HL Refinance")) {
          if (loanAmount <= THIRTY_LACS) {
            percentMultiplier = 85;
          }
          if (loanAmount > THIRTY_LACS && loanAmount <= SEVENTY_FIVE_LACS) {
            percentMultiplier = 77;
          }
          if (loanAmount > SEVENTY_FIVE_LACS) {
            percentMultiplier = 75;
          }
        } else {
          if (endUseOfLoan.equalsIgnoreCase("Purchase of House for Self-usage")
              || endUseOfLoan.equalsIgnoreCase("Purchase of House for investment")) {
            percentMultiplier = 60;
          }
          if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
            percentMultiplier = 80;
          }
          if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
            percentMultiplier = 75;
          }
        }
      }
    } else {
      if (customerScheme.equalsIgnoreCase("CAT C")) {
        if (endUseOfLoan.equalsIgnoreCase("Self-construction")
            || endUseOfLoan.equalsIgnoreCase("HL Refinance")) {
          if (loanAmount <= THIRTY_LACS) {
            percentMultiplier = 80;
          }
          if (loanAmount > THIRTY_LACS && loanAmount <= SEVENTY_FIVE_LACS) {
            percentMultiplier = 75;
          }
          if (loanAmount > SEVENTY_FIVE_LACS) {
            // NA = Not Applicable
          }
        } else {
          if (endUseOfLoan.equalsIgnoreCase("Purchase of House for Self-usage")
              || endUseOfLoan.equalsIgnoreCase("Purchase of House for investment")) {
            // NA = Not Applicable
          }
          if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
            percentMultiplier = 80;
          }
          if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
            percentMultiplier = 75;
          }
        }
      }
    }
    logger.info("---- technicalValuation : {}", technicalValuation);
    logger.info("---- percentMultiplier : {}", percentMultiplier);

    double Xmax = ((technicalValuation * percentMultiplier) / 100);

    logger.info("---- Xmax : {}", Xmax);

    if (endUseOfLoan.equalsIgnoreCase("HL Refinance")
        || endUseOfLoan.equalsIgnoreCase("Self-construction")) {
      Xmax = Math.min(Xmax, 2500000);
      logger.info(
          "---- in case of refinance, self-construction, or renovation Xmax is below 25 lacs : {}",
          Xmax);
    } else {
      Xmax = Math.min(Xmax, 5000000);
      logger.info(
          "---- in case other than refinance, self-construction, or renovation Xmax is below 50 lacs : {}",
          Xmax);
    }
    Xmax = BigDecimal.valueOf(Xmax).setScale(2, RoundingMode.HALF_UP).doubleValue();

    logger.info("---- Xmax (rounded to 2 decimal) : {}", Xmax);

    double Xf = Math.min(X, Xmax);
    logger.info("---- Xf (Final eligible loan amount): {}", Xf);
    execution.setVariable("eligibleLoanAmount1_hl", Xf);

    // calculation of Amount Payable to Seller and Amount Payable to Buyer
    double amountPayableToSeller = 0d;
    double amountPayableToBuyer = 0d;
    if (registryAmount > Xf) {
      amountPayableToSeller = Xf;
      amountPayableToBuyer = 0;
    } else if (registryAmount <= Xf) {
      amountPayableToSeller = (registryAmount * 80.00) / 100.00;
      amountPayableToBuyer = Xf - amountPayableToSeller;
    }
    logger.info("---- amountPayableToSeller : {}", amountPayableToSeller);
    logger.info("---- amountPayableToBuyer : {}", amountPayableToBuyer);

    if (endUseOfLoan.equalsIgnoreCase("HL Refinance")
        || endUseOfLoan.equalsIgnoreCase("Self-construction")) {
      amountPayableToSeller = Xf;
      amountPayableToBuyer = 0;
      logger.info(
          "In case of refinance, self-construction, and renovation, 100% of the loan amount (Xf ) shall be paid to the applicant \n"
              + "---- amountPayableToSeller : {}",
          amountPayableToSeller);
      logger.info("---- amountPayableToBuyer : {}", amountPayableToBuyer);
    }

    double OBT = loanAmount;
    // TODO: as per cibil report find disbursement date and application login
    Calendar calendar = Calendar.getInstance();
    Date loanDisbursementDate = calendar.getTime();
    String enquiryDate =
        CommonHelperFunctions.getStringValue(execution.getVariable("enquiryDate_hl"));
    LocalDateTime localDateTime = LocalDateTime.parse(enquiryDate);

    Calendar calendar1 = Calendar.getInstance();
    calendar1.clear();
    calendar1.set(
        localDateTime.getYear(),
        localDateTime.getMonthValue() - 1,
        localDateTime.getDayOfMonth(),
        localDateTime.getHour(),
        localDateTime.getMinute(),
        localDateTime.getSecond());

    Date applicationLoginDate = calendar1.getTime();
    long timeDiff = Math.abs(loanDisbursementDate.getTime() - applicationLoginDate.getTime());
    long diffMonths = (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS)) / 30;
    logger.info("---- OBT : {}", OBT);
    logger.info(
        "---- difference between original disbursement date and application login : {}",
        diffMonths);

    // calculating Xbf
    double Xbf = 0d;
    if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
      if (X < OBT) {
        Xbf = 0;
      } else if (X == OBT) {
        Xbf = X;
      } else if (X > OBT) {
        if (diffMonths <= 12) {
          Xbf = Math.min((OBT + (OBT * 10) / 100.0), X);
        } else {
          Xbf = Math.min((OBT + (OBT * 20) / 100.0), X);
        }
      }
      logger.info("---- Xbf : {}", Xbf);

      if (X < OBT) {
        amountPayableToSeller = 0;
        amountPayableToBuyer = 0;
      } else if (X == OBT) {
        amountPayableToSeller = OBT;
        amountPayableToBuyer = 0;
      } else if (X > OBT) {
        if (diffMonths <= 12) {
          amountPayableToSeller = OBT;
          amountPayableToBuyer = Math.min(((OBT * 10) / 100.0), X - OBT);
        } else {
          amountPayableToSeller = OBT;
          amountPayableToBuyer = Math.min(((OBT * 20) / 100.0), X - OBT);
        }
      }
      logger.info("---- amountPayableToSeller : {}", amountPayableToSeller);
      logger.info("---- amountPayableToBuyer : {}", amountPayableToBuyer);
    }

    execution.setVariable("amountpayableToBuyer_hl", amountPayableToBuyer);
    execution.setVariable("amountpayableToSeller_hl", amountPayableToSeller);
  }

  public void finalEligibleLoanAmountForAPI(String caseInstanceId, int ltv, int foir) {
    logger.info(
        "--------- EligibilityCriteriaService - finalEligibleLoanAmountForAPI method -------------");
    double A = 0d;
    double B = 0d;
    double C = 0d;
    double D = 0d;

    // Calculating A
    double propertyValueTechApp =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "totalValuation")));

    A = (propertyValueTechApp * ltv) / 100;
    logger.info("---- A : {}", A);

    // Calculating B = Eligible loan amount as per monthly income (Lmi)

    Double aDouble =
        Double.parseDouble(
            cmmnRuntimeService.getVariable(caseInstanceId, "applicantCount_hl").toString());
    int applicantCount = aDouble.intValue();
    double monthlyCombinedIncome = 0d;
    for (int i = 1; i <= applicantCount; i++) {
      monthlyCombinedIncome +=
          Double.parseDouble(
              String.valueOf(
                  cmmnRuntimeService.getVariable(caseInstanceId, "totalEligibleIncome" + i)));
    }
    logger.info("---- monthlyCombinedIncome : {}", monthlyCombinedIncome);
    Double currentObligations =
        Double.parseDouble(
            cmmnRuntimeService.getVariable(caseInstanceId, "monthlyObligation_EMI").toString());
    logger.info("---- currentObligations : {}", currentObligations);

    double a = ((monthlyCombinedIncome * foir) / 100) - currentObligations;
    logger.info("---- a : {}", a);

    double roi =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "roiFD_hl")));
    int tenureInMonths =
        Integer.parseInt(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "loanTenure_hl")));
    logger.info("---- roi : {} , tenureInMonths : {}", roi, tenureInMonths);

    roi = roi / 100;
    double b = a * (Math.pow(1 + (roi / 12), tenureInMonths) - 1);
    double c = b / (Math.pow(1 + (roi / 12), tenureInMonths));

    B = c / (roi / 12);
    B = Double.parseDouble(decimalFormat.format(B));

    logger.info("---- b : {}", b);
    logger.info("---- c : {}", c);
    logger.info("---- B : {}", B);

    // Calculating C

    double registryAmount =
        Double.parseDouble(
            cmmnRuntimeService.getVariable(caseInstanceId, "registryAmount_hl").toString());
    C = 1.4 * registryAmount;

    logger.info("---- registryAmount : {}", registryAmount);
    logger.info("---- C : {}", C);
    // Calculating D
    D =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "loanAmount_hl")));
    logger.info("---- D : {}", D);

    double X = Math.min(Math.min(A, B), Math.min(C, D));
    X = BigDecimal.valueOf(X).setScale(2, RoundingMode.HALF_UP).doubleValue();

    logger.info("---- X (Loan as per lower of A, B, C, or D) : {}", X);

    double technicalValuation =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "totalValuation")));
    int percentMultiplier = 0;

    // new table for getting %  multiplier
    String customerScheme =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "customerSchemeApp1_hl"));
    double loanAmount =
        Double.parseDouble(
            String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "loanAmount_hl")));
    String endUseOfLoan =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "endUseOfLoanString_hl"));
    String salariedUnderSAHAJ =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "sahajLoan"));

    if (salariedUnderSAHAJ.equalsIgnoreCase("no")) {
      if (customerScheme.equalsIgnoreCase("CAT A") || customerScheme.equalsIgnoreCase("CAT B")) {
        if (endUseOfLoan.equalsIgnoreCase("Self-construction")
            || endUseOfLoan.equalsIgnoreCase("HL Refinance")) {
          if (loanAmount <= THIRTY_LACS) {
            percentMultiplier = 85;
          }
          if (loanAmount > THIRTY_LACS && loanAmount <= SEVENTY_FIVE_LACS) {
            percentMultiplier = 77;
          }
          if (loanAmount > SEVENTY_FIVE_LACS) {
            percentMultiplier = 75;
          }
        } else {
          if (endUseOfLoan.equalsIgnoreCase("Purchase of House for Self-usage")
              || endUseOfLoan.equalsIgnoreCase("Purchase of House for investment")) {
            percentMultiplier = 60;
          }
          if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
            percentMultiplier = 80;
          }
          if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
            percentMultiplier = 75;
          }
        }
      }
    } else {
      if (customerScheme.equalsIgnoreCase("CAT C")) {
        if (endUseOfLoan.equalsIgnoreCase("Self-construction")
            || endUseOfLoan.equalsIgnoreCase("HL Refinance")) {
          if (loanAmount <= THIRTY_LACS) {
            percentMultiplier = 80;
          }
          if (loanAmount > THIRTY_LACS && loanAmount <= SEVENTY_FIVE_LACS) {
            percentMultiplier = 75;
          }
          if (loanAmount > SEVENTY_FIVE_LACS) {
            // NA = Not Applicable
          }
        } else {
          if (endUseOfLoan.equalsIgnoreCase("Purchase of House for Self-usage")
              || endUseOfLoan.equalsIgnoreCase("Purchase of House for investment")) {
            // NA = Not Applicable
          }
          if (endUseOfLoan.equalsIgnoreCase("Plot purchase & Construction")) {
            percentMultiplier = 80;
          }
          if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
            percentMultiplier = 75;
          }
        }
      }
    }
    logger.info("---- technicalValuation : {}", technicalValuation);
    logger.info("---- percentMultiplier : {}", percentMultiplier);

    double Xmax = ((technicalValuation * percentMultiplier) / 100);

    logger.info("---- Xmax : {}", Xmax);

    if (endUseOfLoan.equalsIgnoreCase("HL Refinance")
        || endUseOfLoan.equalsIgnoreCase("Self-construction")) {
      Xmax = Math.min(Xmax, 2500000);
      logger.info(
          "---- in case of refinance, self-construction, or renovation Xmax is below 25 lacs : {}",
          Xmax);
    } else {
      Xmax = Math.min(Xmax, 5000000);
      logger.info(
          "---- in case other than refinance, self-construction, or renovation Xmax is below 50 lacs : {}",
          Xmax);
    }
    Xmax = BigDecimal.valueOf(Xmax).setScale(2, RoundingMode.HALF_UP).doubleValue();

    logger.info("---- Xmax (rounded to 2 decimal) : {}", Xmax);

    double Xf = Math.min(X, Xmax);
    logger.info("---- Xf (Final eligible loan amount): {}", Xf);

    // checking for ltv table breach
    double ltvCurr = (Xf / technicalValuation) * 100;
    logger.info("ltv current value : {}", ltvCurr);
    double maxLtv;
    if (technicalValuation <= THIRTY_LACS) {
      maxLtv = 90;
    } else if (technicalValuation <= SEVENTY_FIVE_LACS) {
      maxLtv = 80;
    } else {
      maxLtv = 75;
    }
    logger.info("maxLtv value : {}", maxLtv);

    if (ltvCurr > maxLtv) {
      cmmnRuntimeService.setVariable(caseInstanceId, "isLtvTableBreached_hl", true);
    } else {
      cmmnRuntimeService.setVariable(caseInstanceId, "isLtvTableBreached_hl", false);
    }

    cmmnRuntimeService.setVariable(caseInstanceId, "revisedLoanAmount_hl", Xf);

    // calculation of Amount Payable to Seller and Amount Payable to Buyer
    double amountPayableToSeller = 0d;
    double amountPayableToBuyer = 0d;
    if (registryAmount > Xf) {
      amountPayableToSeller = Xf;
      amountPayableToBuyer = 0.00;
    } else if (registryAmount <= Xf) {
      amountPayableToSeller = (registryAmount * 80.00) / 100.00;
      amountPayableToBuyer = Xf - amountPayableToSeller;
    }
    logger.info("---- amountPayableToSeller : {}", amountPayableToSeller);
    logger.info("---- amountPayableToBuyer : {}", amountPayableToBuyer);

    if (endUseOfLoan.equalsIgnoreCase("HL Refinance")
        || endUseOfLoan.equalsIgnoreCase("Self-construction")) {
      amountPayableToSeller = Xf;
      amountPayableToBuyer = 0.00;
      logger.info(
          "In case of refinance, self-construction, and renovation, 100% of the loan amount (Xf ) shall be paid to the applicant \n---- amountPayableToSeller : {} , ---- amountPayableToBuyer : {}",
          amountPayableToSeller, amountPayableToBuyer);
    }

    double OBT = loanAmount;
    // TODO: as per cibil report find disbursement date and application login
    Calendar calendar = Calendar.getInstance();
    Date loanDisbursementDate = calendar.getTime();
    String enquiryDate =
        CommonHelperFunctions.getStringValue(
            cmmnRuntimeService.getVariable(caseInstanceId, "enquiryDate_hl"));
    LocalDateTime localDateTime = LocalDateTime.parse(enquiryDate);

    Calendar calendar1 = Calendar.getInstance();
    calendar1.clear();
    calendar1.set(
        localDateTime.getYear(),
        localDateTime.getMonthValue() - 1,
        localDateTime.getDayOfMonth(),
        localDateTime.getHour(),
        localDateTime.getMinute(),
        localDateTime.getSecond());

    Date applicationLoginDate = calendar1.getTime();
    long timeDiff = Math.abs(loanDisbursementDate.getTime() - applicationLoginDate.getTime());
    long diffMonths = (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS)) / 30;
    logger.info("---- OBT : {}", OBT);
    logger.info(
        "---- difference between original disbursement date and application login : {}",
        diffMonths);

    // calculating Xbf
    double Xbf = 0d;
    if (endUseOfLoan.equalsIgnoreCase("Balance Transfer")) {
      if (X < OBT) {
        Xbf = 0;
      } else if (X == OBT) {
        Xbf = X;
      } else if (X > OBT) {
        if (diffMonths <= 12) {
          Xbf = Math.min((OBT + (OBT * 10) / 100.00), X);
        } else {
          Xbf = Math.min((OBT + (OBT * 20) / 100.00), X);
        }
      }
      logger.info("---- Xbf : {}", Xbf);

      if (X < OBT) {
        amountPayableToSeller = 0;
        amountPayableToBuyer = 0;
      } else if (X == OBT) {
        amountPayableToSeller = OBT;
        amountPayableToBuyer = 0;
      } else if (X > OBT) {
        if (diffMonths <= 12) {
          amountPayableToSeller = OBT;
          amountPayableToBuyer = Math.min(((OBT * 10) / 100.00), X - OBT);
        } else {
          amountPayableToSeller = OBT;
          amountPayableToBuyer = Math.min(((OBT * 20) / 100.00), X - OBT);
        }
      }
      logger.info("---- amountPayableToSeller : {}", amountPayableToSeller);
      logger.info("---- amountPayableToBuyer : {}", amountPayableToBuyer);
    }

    cmmnRuntimeService.setVariable(
        caseInstanceId, "revisedamountpayableToBuyer_hl", amountPayableToBuyer);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "revisedamountpayableToSeller_hl", amountPayableToSeller);
  }

  //    public static void main(String[] args) {
  //      double B = 0d;
  //      double monthlyCombinedIncome = 160000.00;
  //      double currentObligations = 44000.00;
  //      int foir = 55;
  //      double a = ((monthlyCombinedIncome * foir) / 100) - currentObligations;
  //      logger.info("---- a : {}", a);
  //
  //      double roi = 6.90;
  //      int tenureInMonths = 240;
  //      logger.info("---- roi : {} , tenureInMonths : {}", roi, tenureInMonths);
  //
  //      double temp = Math.pow(1 + (roi / 1200), tenureInMonths);
  //      logger.info("---- temp : {}", temp);
  //      BigDecimal bd = new BigDecimal(temp).setScale(2, RoundingMode.HALF_UP);
  //      temp = bd.doubleValue();
  //      logger.info("---- temp : {}", temp);
  //
  //      double b = a * (Math.pow(1 + (roi / 1200), tenureInMonths) - 1);
  //      double c = b / (Math.pow(1 + (roi / 1200), tenureInMonths));
  //
  //      B = c / (roi / 1200);
  //      bd = new BigDecimal(B).setScale(2, RoundingMode.HALF_UP);
  //      B = bd.doubleValue();
  //      logger.info("---- b : {}", b);
  //      logger.info("---- c : {}", c);
  //      logger.info("---- B : {}", B);
  //    }
  //  public static void main(String[] args) {
  //    double B = 3.228622125497287E7;
  //    double dfB = Double.parseDouble(String.format("%.2f", B));
  //    String bd = new BigDecimal(B).toPlainString();
  //    DecimalFormat df = new DecimalFormat("#");
  //    df.setMaximumFractionDigits(2);
  //    System.out.println("decimal format : " + df.format(B));
  //
  //    System.out.println(new BigDecimal(B).toPlainString());
  //    System.out.println("dfB : " + dfB);
  //    double truncatedB = BigDecimal.valueOf(B).setScale(2, RoundingMode.HALF_UP).doubleValue();
  //    System.out.println(truncatedB);
  //  }
}
