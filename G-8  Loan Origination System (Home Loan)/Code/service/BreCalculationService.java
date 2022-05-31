package com.kuliza.workbench.service;

import com.kuliza.lending.authorization.config.iam.IAMService;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.Map;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("BreCalculationService")
public class BreCalculationService {
  private static final Logger logger = LoggerFactory.getLogger(BreCalculationService.class);
  @Autowired private RuntimeService runtimeService;

  private static final int NINE_TIMES_NINE = 999999999;

  @Value("${account.type.map.master.url}")
  private String accountTypeMapMasterUrl;

  @Autowired public IAMService iamService;

  public DelegateExecution breRecursiveInput(DelegateExecution execution) throws JSONException {
    logger.info("value :----- {}", execution.getVariable("breLoopCount_hl"));
    execution.setVariable("ownContribution_hl", execution.getVariable("ownContribution_hl"));
    execution.setVariable("Foir_hl", execution.getVariable("Foir_hl"));
    execution.setVariable("salaryBand_hl", execution.getVariable("salaryBand_hl"));

    // AM Parameters
    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {

      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant1currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant1workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant1Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant1CirScore_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant2currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant2workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant2Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant2CirScore_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant3currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant3workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant3Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant3CirScore_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant4currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant4workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant4Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant4CirScore_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant5currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant5workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant5Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant5CirScore_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      execution.setVariable(
          "currentresidenceinYears_hl",
          execution.getVariable("applicant6currentresidenceinYears_hl"));
      execution.setVariable(
          "workExperience_hl", execution.getVariable("applicant6workExperience_hl"));
      execution.setVariable("Dependents_hl", execution.getVariable("applicant6Dependents_hl"));
      execution.setVariable("cibilScore_hl", execution.getVariable("applicant6CirScore_hl"));
    }

    // BM Parameters
    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant1MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant1LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant160PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant1CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant1BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant1OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant1MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant1MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant1closedcdloansCount_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant2MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant2LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant260PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant2CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant2BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant2OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant2MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant2MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant2closedcdloansCount_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant3MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant3LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant360PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant3CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant3BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant3OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant3MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant3MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant3closedcdloansCount_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant4MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant4LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant460PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant4CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant4BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant4OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant4MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant4MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant4closedcdloansCount_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant5MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant5LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant560PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant5CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant5BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant5OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant5MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant5MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant5closedcdloansCount_hl"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      execution.setVariable(
          "MissedPayment_hl", execution.getVariable("applicant6MissedPayment_hl"));
      execution.setVariable(
          "LoanToUnsecLoanRatio_hl", execution.getVariable("applicant6LoanToUnsecLoanRatio_hl"));
      execution.setVariable(
          "sixtyPlusDpdInL3y_hl", execution.getVariable("applicant660PlusDpdInL3y_hl"));
      execution.setVariable(
          "CountClosedLoans_hl", execution.getVariable("applicant6CountClosedLoans_hl"));
      execution.setVariable(
          "BureauHistory_hl", execution.getVariable("applicant6BureauHistory_hl"));
      execution.setVariable(
          "OutStandingLoan_hl", execution.getVariable("applicant6OutStandingLoan_hl"));
      execution.setVariable("MaxDpdAllL3m_hl", execution.getVariable("applicant6MaxDpdAllL3m_hl"));
      execution.setVariable(
          "MaxDpdAutoL12m_hl", execution.getVariable("applicant6MaxDpdAutoL12m_hl"));
      execution.setVariable(
          "closedcdloansCount_hl", execution.getVariable("applicant6closedcdloansCount_hl"));
    }

    Double aDouble = Double.parseDouble(execution.getVariable("breLoopCount_hl").toString());
    int applicantNo = aDouble.intValue();

    execution.setVariable(
        "ApplicantType_hl", execution.getVariable("applicant" + applicantNo + "ApplicantType_hl"));

    execution.setVariable(
        "isCibilScoreNegative",
        execution.getVariable("isApplicant" + applicantNo + "CibilScoreNegative"));
    return execution;
  }

  public DelegateExecution breRecursiveOutput(DelegateExecution execution) throws JSONException {

    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {
      execution.setVariable(
          "applicant1applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant1finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant1finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant1bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant1applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      execution.setVariable(
          "applicant2applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant2finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant2finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant2bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant2applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      execution.setVariable(
          "applicant3applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant3finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant3finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant3bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant3applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      execution.setVariable(
          "applicant4applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant4finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant4finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant4bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant4applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      execution.setVariable(
          "applicant5applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant5finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant5finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant5bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant5applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      execution.setVariable(
          "applicant6applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant6finalOutcomeBRE1Output", execution.getVariable("finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant6finalOutcomeBRE1Considered",
          execution.getVariable("finalOutcomeBRE1Considered"));
      execution.setVariable("applicant6bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant6applicationScore", execution.getVariable("applicationScore"));
    }

    String isCibilScoreNegative =
        CommonHelperFunctions.getStringValue(execution.getVariable("isCibilScoreNegative"));
    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant1bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant1lastOutcomeBRE1Output",
          execution.getVariable("applicant1finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant1lastOutcomeBRE1Considered",
          execution.getVariable("applicant1finalOutcomeBRE1Considered"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant2bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant2lastOutcomeBRE1Output",
          execution.getVariable("applicant2finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant2lastOutcomeBRE1Considered",
          execution.getVariable("applicant2finalOutcomeBRE1Considered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant3bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant3lastOutcomeBRE1Output",
          execution.getVariable("applicant3finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant3lastOutcomeBRE1Considered",
          execution.getVariable("applicant3finalOutcomeBRE1Considered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant4bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant4lastOutcomeBRE1Output",
          execution.getVariable("applicant4finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant4LatestOutcomeBRE1Considered",
          execution.getVariable("applicant4finalOutcomeBRE1Considered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant5bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant5lastOutcomeBRE1Output",
          execution.getVariable("applicant5finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant5LatestOutcomeBRE1Considered",
          execution.getVariable("applicant5finalOutcomeBRE1Considered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant6bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant6lastOutcomeBRE1Output",
          execution.getVariable("applicant6finalOutcomeBRE1Output"));
      execution.setVariable(
          "applicant6LatestOutcomeBRE1Considered",
          execution.getVariable("applicant6finalOutcomeBRE1Considered"));
    }

    execution.setVariable("bureauModel8Cap", "");
    execution.setVariable("applicationModel1Cap", "");
    return execution;
  }

  public DelegateExecution breRecursiveOutputRerun(DelegateExecution execution)
      throws JSONException {

    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {
      execution.setVariable(
          "applicant1applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant1finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant1finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant1bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant1applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      execution.setVariable(
          "applicant2applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant2finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant2finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant2bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant2applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      execution.setVariable(
          "applicant3applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant3finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant3finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant3bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant3applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      execution.setVariable(
          "applicant4applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant4finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant4finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant4bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant4applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      execution.setVariable(
          "applicant5applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant5finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant5finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant5bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant5applicationScore", execution.getVariable("applicationScore"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      execution.setVariable(
          "applicant6applicationModel1Cap",
          execution.getVariable("applicationModel1Cap").toString());
      execution.setVariable(
          "applicant6finalOutcomeBRE1RerunOutput",
          execution.getVariable("finalOutcomeBRE1RerunOutputNew"));
      execution.setVariable(
          "applicant6finalOutcomeBRE1RerunConsidered",
          execution.getVariable("finalOutcomeBRE1RerunConsidered"));
      execution.setVariable("applicant6bureauScore", execution.getVariable("bureauScore"));
      execution.setVariable(
          "applicant6applicationScore", execution.getVariable("applicationScore"));
    }
    String isCibilScoreNegative =
        CommonHelperFunctions.getStringValue(execution.getVariable("isCibilScoreNegative"));
    if (execution.getVariable("breLoopCount_hl").toString().equals("1")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant1bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant1lastOutcomeBRE1Output",
          execution.getVariable("applicant1finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant1LatestOutcomeBRE1Considered",
          execution.getVariable("applicant1finalOutcomeBRE1RerunConsidered"));
    } else if (execution.getVariable("breLoopCount_hl").toString().equals("2.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant2bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant2lastOutcomeBRE1Output",
          execution.getVariable("applicant2finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant2LatestOutcomeBRE1Considered",
          execution.getVariable("applicant2finalOutcomeBRE1RerunConsidered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("3.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant3bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant3lastOutcomeBRE1Output",
          execution.getVariable("applicant3finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant3LatestOutcomeBRE1Considered",
          execution.getVariable("applicant3finalOutcomeBRE1RerunConsidered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("4.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant4bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant4lastOutcomeBRE1Output",
          execution.getVariable("applicant4finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant4LatestOutcomeBRE1Considered",
          execution.getVariable("applicant4finalOutcomeBRE1RerunConsidered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("5.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant5bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant5lastOutcomeBRE1Output",
          execution.getVariable("applicant5finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant5LatestOutcomeBRE1Considered",
          execution.getVariable("applicant5finalOutcomeBRE1RerunConsidered"));

    } else if (execution.getVariable("breLoopCount_hl").toString().equals("6.0")) {
      if (isCibilScoreNegative.equalsIgnoreCase("no")) {
        execution.setVariable(
            "applicant6bureauModel8Cap", execution.getVariable("bureauModel8Cap").toString());
      }
      execution.setVariable(
          "applicant6lastOutcomeBRE1Output",
          execution.getVariable("applicant6finalOutcomeBRE1RerunOutput"));
      execution.setVariable(
          "applicant6LatestOutcomeBRE1Considered",
          execution.getVariable("applicant6finalOutcomeBRE1RerunConsidered"));
    }

    execution.setVariable("bureauModel8Cap", "");
    execution.setVariable("applicationModel1Cap", "");
    return execution;
  }

  public DelegateExecution breCalculation(DelegateExecution execution) throws JSONException {
    Map<String, Object> variables = execution.getVariables();
    JSONArray breApplicantValue = new JSONArray();
    JSONArray breBureauModelValue = new JSONArray();
    String isCibilScoreNegative =
        CommonHelperFunctions.getStringValue(execution.getVariable("isCibilScoreNegative"));
    if (isCibilScoreNegative.equalsIgnoreCase("no")) {
      try {
        breBureauModelValue =
            CommonHelperFunctions.objectToJSONArray(variables.get("bureauModel8Cap"));
        logger.info("bureauModel8Cap :- {}", variables.get("bureauModel8Cap").toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    breApplicantValue =
        CommonHelperFunctions.objectToJSONArray(variables.get("applicationModel1Cap"));
    logger.info("applicationModel1Cap :- {}", variables.get("applicationModel1Cap").toString());

    double applicationS1 = 0;
    for (int i = 0; i < breApplicantValue.length(); i++) {
      logger.info("breApplicantValue.get(i) :- {}  i :- {}", breApplicantValue.get(i), i);
      JSONObject object = new JSONObject(breApplicantValue.get(i).toString());

      logger.info("object:- {}", object);
      Double applicationX1 = Double.parseDouble(object.get("WoeAm_hl").toString());
      logger.info("applicationX1:- {}", applicationX1);

      Double applicationZ1 = Double.parseDouble(object.get("MulitiplyingFactorAm_hl").toString());
      logger.info("applicationZ1:- {}", applicationZ1);
      applicationS1 += (applicationX1 * applicationZ1);
    }
    logger.info("applicationS1:- {}", applicationS1);

    final double e = 2.718;
    final double constantA = -2.78;

    double applicationS2 = applicationS1 + constantA;
    logger.info("applicationS2:- {}", applicationS2);

    double applicationS3 = Math.pow(e, applicationS2);
    logger.info("applicationS3:- {}", applicationS3);

    double applicationS4 = (applicationS3) / (1 + applicationS3);
    logger.info("applicationS4:- {}", applicationS4);

    if (applicationS4 >= 0 && applicationS4 < 0.0267) {
      execution.setVariable("pdRangeAM", "R1");
    } else if (applicationS4 >= 0.0267 && applicationS4 < 0.0403) {
      execution.setVariable("pdRangeAM", "R2");
    } else if (applicationS4 >= 0.0403 && applicationS4 < 0.059) {
      execution.setVariable("pdRangeAM", "R3");
    } else if (applicationS4 >= 0.059 && applicationS4 < 0.0844) {
      execution.setVariable("pdRangeAM", "R4");
    } else if (applicationS4 >= 0.0844) {
      execution.setVariable("pdRangeAM", "R5");
    } else {
      execution.setVariable("pdRangeAM", "Invalid Result");
    }

    // BM Model PD Calculation: ......
    if (isCibilScoreNegative.equalsIgnoreCase("no")) {
      double bureauS1 = 0;

      for (int i = 0; i < breBureauModelValue.length(); i++) {
        logger.info("breBureauModelValue :- {}  - i {}", breBureauModelValue.get(i), i);
        JSONObject object = new JSONObject(breBureauModelValue.get(i).toString());

        Double bureauX1 = Double.parseDouble(object.get("Woe_hl").toString());
        logger.info("bureauX1:- {}", bureauX1);

        Double bureauZ1 = Double.parseDouble(object.get("MulitiplyingFactor_hl").toString());

        logger.info("bureauZ1:- {}", bureauZ1);
        bureauS1 += (bureauX1 * bureauZ1);
      }
      ;
      logger.info("bureauS1:- {}", bureauS1);

      final double constantB = -2.64;

      double bureauS2 = bureauS1 + constantB;
      logger.info("bureauS2:- {}", bureauS2);

      double bureauS3 = Math.pow(e, bureauS2);
      logger.info("bureauS3:- {}", bureauS3);

      double bureauS4 = (bureauS3) / (1 + bureauS3);
      logger.info("bureauS4:- {}", bureauS4);

      if (bureauS4 >= 0 && bureauS4 < 0.0237) {
        execution.setVariable("pdRangeBM", "R1");
      } else if (bureauS4 >= 0.0237 && bureauS4 < 0.036) {
        execution.setVariable("pdRangeBM", "R2");
      } else if (bureauS4 >= 0.036 && bureauS4 < 0.0548) {
        execution.setVariable("pdRangeBM", "R3");
      } else if (bureauS4 >= 0.0548 && bureauS4 < 0.0778) {
        execution.setVariable("pdRangeBM", "R4");
      } else if (bureauS4 >= 0.0778) {
        execution.setVariable("pdRangeBM", "R5");
      } else {
        execution.setVariable("pdRangeBM", "Invalid Result");
      }
    }

    return execution;
  }

  public DelegateExecution scoreCalculation(DelegateExecution execution) throws JSONException {
    Map<String, Object> variables = execution.getVariables();
    JSONArray breApplicantValue = new JSONArray();
    JSONArray breBureauModelValue = new JSONArray();
    String isCibilScoreNegative =
        CommonHelperFunctions.getStringValue(execution.getVariable("isCibilScoreNegative"));
    if (isCibilScoreNegative.equalsIgnoreCase("no")) {
      breBureauModelValue =
          CommonHelperFunctions.objectToJSONArray(variables.get("bureauModel8Cap"));
      logger.info("bureauModel8Cap :- {}", variables.get("bureauModel8Cap").toString());
    }
    breApplicantValue =
        CommonHelperFunctions.objectToJSONArray(variables.get("applicationModel1Cap"));
    logger.info("applicationModel1Cap :- {}", variables.get("applicationModel1Cap").toString());

    // AM score calculation.......
    double applicationScore2 = 0;

    for (int i = 0; i < breApplicantValue.length(); i++) {
      JSONObject object = new JSONObject(breApplicantValue.get(i).toString());
      Double applicationY = Double.parseDouble(object.get("ContributionAm_hl").toString());
      logger.info("applicationY:- {}", applicationY);
      applicationScore2 += applicationY;
    }
    logger.info("applicationScore2:- {}", applicationScore2);

    final double constantScoreA = 56.2;
    double applicationScore3 = applicationScore2 + constantScoreA;
    logger.info("applicationScore3:- {}", applicationScore3);

    if (applicationScore3 < 0) {
      execution.setVariable("applicationScore", 0);
    } else if (applicationScore3 > 100) {
      execution.setVariable("applicationScore", 100);
    } else {
      execution.setVariable("applicationScore", applicationScore3);
    }

    // BM score calculation.......

    if (isCibilScoreNegative.equalsIgnoreCase("no")) {
      double bureauScore2 = 0;

      for (int i = 0; i < breBureauModelValue.length(); i++) {
        JSONObject object = new JSONObject(breBureauModelValue.get(i).toString());
        Double bureauY = Double.parseDouble(object.get("Contribution_hl").toString());
        logger.info("bureauY:- {}", bureauY);
        bureauScore2 += bureauY;
      }
      logger.info("bureauScore2:- {}", bureauScore2);

      final double constantScoreB = 52.6181;
      double bureauScore3 = bureauScore2 + constantScoreB;
      logger.info("bureauScore3:- {}", bureauScore3);

      if (bureauScore3 < 0) {
        execution.setVariable("bureauScore", 0);
      } else if (bureauScore3 > 100) {
        execution.setVariable("bureauScore", 100);
      } else {
        execution.setVariable("bureauScore", bureauScore3);
      }
    }

    return execution;
  }

  public DelegateExecution nonFinancialBreColor(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();

    String applicationStatus = "", creditStatus = "", bre1Color_hl = "";
    String status = "", fcuStatus = "", technicalStatus = "", legalStatus = "";
    boolean isRed = false;
    String mainApplicantColor =
        execution.getVariable("applicant1finalOutcomeBRE1Considered").toString();
    for (int i = 1; i <= applicantCount; i++) {
      String applicantType =
          CommonHelperFunctions.getStringValue(
              execution.getVariable("applicant" + i + "ApplicantType_hl"));
      if (!applicantType.equalsIgnoreCase("Non-Financial Co-Applicant")) {
        String applicantColor =
            CommonHelperFunctions.getStringValue(
                execution.getVariable("applicant" + i + "finalOutcomeBRE1Considered"));
        if (applicantColor.equalsIgnoreCase("Red")) {
          execution.setVariable("applicationStatus", "(R) - Rejected (Credit evaluation)");
          execution.setVariable("creditStatus", "(R0) - Rejected by Credit model");
          execution.setVariable("bre1Color_hl", applicantColor);
          execution.setVariable("caseStatus_hl", "Rejected");
          status = "(R) - Rejected (Credit evaluation)";
          isRed = true;
          break;
        } else {
          applicationStatus = "(U) - Application under review";
          bre1Color_hl = mainApplicantColor;
          fcuStatus = "Initiated";
          technicalStatus = "Initiated";
          legalStatus = "Initiated";
          status = "(U) - Application under review";
        }
      }
    }
    if (!isRed) {
      execution.setVariable("applicationStatus", applicationStatus);
      execution.setVariable("bre1Color_hl", bre1Color_hl);
      execution.setVariable("fcuStatus", fcuStatus);
      execution.setVariable("technicalStatus", technicalStatus);
      execution.setVariable("legalStatus", legalStatus);
    }
    if (mainApplicantColor.equalsIgnoreCase("Amber")) {
      execution.setVariable("creditStatus", "(A0) - Credit assessment initiated");
    } else if (mainApplicantColor.equalsIgnoreCase("Green")) {
      execution.setVariable("creditStatus", "(G0) - Credit assessment initiated");
    }

    if (status.equalsIgnoreCase("(R) - Rejected (Credit evaluation)")) {
      execution.setVariable("bre1RejectTimestamp", java.time.LocalDateTime.now().toString());
      execution.setVariable("consumerAppStatus_hl", "Rejected");
    }

    return execution;
  }

  public DelegateExecution nonFinancialBreColorRerun(DelegateExecution execution) {
    Double doubleValue = Double.parseDouble(execution.getVariable("applicantCount_hl").toString());
    int applicantCount = doubleValue.intValue();
    boolean isRed = false;
    for (int i = 1; i <= applicantCount; i++) {
      String applicantType =
          CommonHelperFunctions.getStringValue(
              execution.getVariable("applicant" + i + "ApplicantType_hl"));
      if (!applicantType.equalsIgnoreCase("Non-Financial Co-Applicant")) {
        String applicantColor =
            CommonHelperFunctions.getStringValue(
                execution.getVariable("applicant" + i + "finalOutcomeBRE1RerunConsidered"));
        if (applicantColor.equalsIgnoreCase("Red")) {
          execution.setVariable("bre1RerunColor_hl", applicantColor);
          isRed = true;
          break;
        }
      }
    }
    if (!isRed) {
      execution.setVariable(
          "bre1RerunColor_hl", execution.getVariable("applicant1finalOutcomeBRE1RerunConsidered"));
    }

    return execution;
  }

  public DelegateExecution bre1StatusServiceRerun(DelegateExecution execution) {
    String bre1Color = execution.getVariable("bre1Color_hl").toString();
    String bre1RerunColor = execution.getVariable("bre1RerunColor_hl").toString();

    if (bre1Color.equalsIgnoreCase("Amber")) {
      if (bre1RerunColor.equalsIgnoreCase("Red")) {
        execution.setVariable("applicationStatus", "(R) - Rejected (Credit evaluation)");
        execution.setVariable("creditStatus", "(AR) - Rejected by Credit model post PD");
        execution.setVariable("caseStatus_hl", "Rejected");
        execution.setVariable("fcuStatus", "Dropped (Rejected by Credit)");
        execution.setVariable("technicalStatus", "Dropped (Rejected by Credit)");
        execution.setVariable("legalStatus", "Dropped (Rejected by Credit)");

      } else if (bre1RerunColor.equalsIgnoreCase("Amber")) {
        execution.setVariable("creditStatus", "(AA) - Credit decision pending post PD");

      } else if (bre1RerunColor.equalsIgnoreCase("Green")) {
        int loanAmount = Integer.parseInt(execution.getVariable("loanAmount_hl").toString());
        if (loanAmount <= 1500000) {
          execution.setVariable("creditStatus", "(AG) - Approved by Credit model post PD");
        } else {
          execution.setVariable("creditStatus", "(AG) - Credit approval pending post PD");
        }
      }
    } else if (bre1Color.equalsIgnoreCase("Green")) {
      if (bre1RerunColor.equalsIgnoreCase("Red")) {
        execution.setVariable("applicationStatus", "(R) - Rejected (Credit evaluation)");
        execution.setVariable("creditStatus", "(GR) - Rejected by Credit model post PD");
        execution.setVariable("caseStatus_hl", "Rejected");
        execution.setVariable("fcuStatus", "Dropped (Rejected by Credit)");
        execution.setVariable("technicalStatus", "Dropped (Rejected by Credit)");
        execution.setVariable("legalStatus", "Dropped (Rejected by Credit)");
        execution.setVariable("bre1RerunColor_hl", "Red");

      } else if (bre1RerunColor.equalsIgnoreCase("Amber")) {
        execution.setVariable("creditStatus", "(GA) - Credit decision pending post PD");

      } else if (bre1RerunColor.equalsIgnoreCase("Green")) {
        int loanAmount = Integer.parseInt(execution.getVariable("loanAmount_hl").toString());
        if (loanAmount <= 1500000) {
          execution.setVariable("creditStatus", "(GG) - Approved by Credit model post PD");

        } else {
          execution.setVariable("creditStatus", "(GG) - Credit approval pending post PD");
        }
      }
    }
    return execution;
  }

  //  public JSONArray ENQFlatSheetCreationTest() throws JSONException, UnirestException,
  // IOException {
  //
  //    JSONArray ENQ_Flat = new JSONArray();
  //    String creditReportStr =
  //        FileUtils.readFileToString(new File("/home/kuliza-523/Downloads/Credit report.json"));
  //
  //    //    Double aDouble =
  //    //   Double.parseDouble(execution.getVariable("breLoopCount_hl").toString());
  //    int applicantNo = 1;
  //    logger.info(" creditReportStr: " + creditReportStr);
  //    JSONObject creditReport = new JSONObject(creditReportStr);
  //    JSONObject creditReportJsonObj = creditReport.getJSONObject("CreditReport");
  //    JSONArray enquiry = creditReportJsonObj.getJSONArray("Enquiry");
  //
  //    logger.info(" enquiry :" + enquiry);
  //
  //    for (int i = 0; i < enquiry.length(); i++) {
  //
  //      if (enquiry.getJSONObject(i).get("DateOfEnquiryFields") == null
  //          || Integer.parseInt(enquiry.getJSONObject(i).get("EnquiryAmount").toString())
  //              > NINE_TIMES_NINE) {
  //        continue;
  //      }
  //
  //      JSONObject row = new JSONObject();
  //      row.put(
  //          "acct_num",
  //          creditReportJsonObj.getJSONObject("Header").get("ReferenceNumber").toString());
  //      row.put("dateofenquiry", enquiry.getJSONObject(i).get("DateOfEnquiryFields").toString());
  //      row.put("enquirypurpose", enquiry.getJSONObject(i).get("EnquiryPurpose").toString());
  //      row.put("enquiryamount", enquiry.getJSONObject(i).get("EnquiryAmount").toString());
  //
  //      ENQ_Flat.put(row);
  //    }
  //
  //    //    String token =
  //    //        "Bearer
  //    //
  // eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3QmJpejR4a3VlM2ZpZlllMnhtMmEtVWo5RXYxejNGbFQzcjNVUUQ5QjBNIn0.eyJleHAiOjE2NDU4MTgwNTIsImlhdCI6MTY0NTgxNjI1MiwiYXV0aF90aW1lIjoxNjQ1ODE2MjUyLCJqdGkiOiI5ZDg3MTQzNy1hYWEwLTQwNjktYTRhOS01MTczZTUzNjkyMDQiLCJpc3MiOiJodHRwczovL2xvcy5jYXByaWdsb2JhbC5kZXYvYXV0aC9yZWFsbXMva3VsaXphX3JlYWxtIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6Ijc1OGI2MDk5LWMxYjUtNGE0YS1iMzQ4LTkyNWUyMmQ1NzI1YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImxlbmRpbmciLCJub25jZSI6InhkX09xRTIwY1JKMXNFWXBnM2lQa0hQb3kzR0hCTmFUM0dwTkpjUGZTRGMiLCJzZXNzaW9uX3N0YXRlIjoiYjhkODczZmEtODhmMy00MTRlLWI1MzQtM2ZhOGI4MTA5NDNmIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJtYXN0ZXJzX2FkbWluIiwiYWNjZXNzLWlkbSIsIm9mZmxpbmVfYWNjZXNzIiwiYWNjZXNzLXRhc2siLCJhY2Nlc3MtbW9kZWxlciIsInVtYV9hdXRob3JpemF0aW9uIiwiYWNjZXNzLWFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJsZW5kaW5nIjp7InJvbGVzIjpbIm1hc3RlcnNfYWRtaW4iXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGFkZHJlc3MgZW1haWwgcGhvbmUgbWljcm9wcm9maWxlLWp3dCBvZmZsaW5lX2FjY2VzcyBwcm9maWxlIiwidXBuIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoic3R1ZGlvIGFkbWluIiwiZ3JvdXBzIjpbIm1hc3RlcnNfYWRtaW4iLCJhY2Nlc3MtaWRtIiwib2ZmbGluZV9hY2Nlc3MiLCJhY2Nlc3MtdGFzayIsImFjY2Vzcy1tb2RlbGVyIiwidW1hX2F1dGhvcml6YXRpb24iLCJhY2Nlc3MtYWRtaW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20iLCJnaXZlbl9uYW1lIjoic3R1ZGlvIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoic3R1ZGlvX2FkbWluQGt1bGl6YS5jb20ifQ.DAe4Ro_M0kvqIUDzwf19Y8IQ2MERSgBINSumTAJIi-QgjhdCyl9db2m3EDhrH441oQ8MDyv7lRJKd6RFms3ajWqt4-CMhuFjO9epsYVTLqjIyBzbWENyeI1aQZjH1H61JJ_rHCSoFjHxk2Chi5tpyhAFbhOKU2lFwOeYV4VX8s0TTdhVGwaSEQ12gizrYNVwhgeH1mAnwuJF4-31VF04MBGmJZb37vBJ9VH7IATG4WHn3XdkTX4yCJcHhfsKVURLE4g_vrv_MqQQXewuRlvINlGG2JmjTfD-ipu7_WYjV0qOuttxt_gRhrcrBrGz8dAINtnyuNZ6MG9OUx3bFoNEEQ";
  //    String token = iamService.getAdminAccessToken();
  //    logger.info("Token: " + token);
  //
  //    HttpResponse<String> response =
  //        Unirest.get(accountTypeMapMasterUrl).header("Authorization", "Bearer " +
  // token).asString();
  //    logger.info("account type map master response :- {}", response.getBody());
  //
  //    JSONObject data = new JSONObject(response.getBody());
  //    logger.info("data :- {}", data);
  //    JSONArray acctTypeMap = data.getJSONArray("data");
  //    logger.info("acctTypeMap : {}", acctTypeMap);
  //
  //    for (int i = 0; i < ENQ_Flat.length(); i++) {
  //      String enquirypurpose = ENQ_Flat.getJSONObject(i).get("enquirypurpose").toString();
  //      for (int j = 0; j < acctTypeMap.length(); j++) {
  //        if (enquirypurpose.equalsIgnoreCase(
  //            acctTypeMap.getJSONObject(j).get("acct_type").toString())) {
  //          ENQ_Flat.getJSONObject(i)
  //              .put(
  //                  "description_category1",
  //                  acctTypeMap.getJSONObject(j).get("description_category1").toString());
  //          ENQ_Flat.getJSONObject(i)
  //              .put(
  //                  "description_category3",
  //                  acctTypeMap.getJSONObject(j).get("description_category3").toString());
  //          ENQ_Flat.getJSONObject(i)
  //              .put(
  //                  "description_category4",
  //                  acctTypeMap.getJSONObject(j).get("description_category4").toString());
  //        }
  //      }
  //    }
  //
  //    return ENQ_Flat;
  //  }

  //    public static void main(String[] args) {
  //
  //      final double applicationScore2 = 25.5;
  //      final double constantScoreA = 156.2;
  //      double applicationScore3 = applicationScore2 + constantScoreA;
  //      logger.info("applicationScore3:- {}", applicationScore3);
  //    double applicationScore;
  //      if (applicationScore3 < 0) {
  //        applicationScore = 0;
  //      } else if (applicationScore3 > 100) {
  //       applicationScore = 100;
  //      } else {
  //        applicationScore = applicationScore3;
  //      }
  //      System.out.println(applicationScore);

  /** ******** To test the CVFlatSheetCreation uncomment below lines ******* */

  //          String creditReportStr = FileUtils.readFileToString(
  //            new File("/home/kuliza-523/Downloads/Credit report.json"));
  //          JlSONObject creditReport = new JSONObject(creditReportStr);
  //          CVFlatMapping(creditReport);
  //

  //  }

}
