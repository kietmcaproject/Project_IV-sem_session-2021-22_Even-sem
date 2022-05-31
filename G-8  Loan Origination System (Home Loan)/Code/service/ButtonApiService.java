package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ButtonApiService {
  private static final Logger logger = LoggerFactory.getLogger(ButtonApiService.class);

  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Autowired EligibilityCriteriaService eligibilityCriteriaService;

  public void enableRecalculate(Map<String, Object> request) {
    logger.info("------------- inside enableRecalculate api ----------------- ");
    logger.info("------ request variables : {}", request);
    String caseInstanceId = request.get("caseInstanceId").toString();
    logger.info("------ caseInstanceId : {}", caseInstanceId);

    cmmnRuntimeService.setVariables(caseInstanceId, request);

    String sanctionToHighAuth =
        CommonHelperFunctions.getStringValue(request.get("sanctionToHighAuthDropdown"));
    logger.info("---- sanctionToHighAuth : {}", sanctionToHighAuth);

    int loanAmount =
        Integer.parseInt(
            cmmnRuntimeService.getVariable(caseInstanceId, "loanAmount_hl").toString());
    if (loanAmount <= 3000000) {
      String revisedLtvLessThirty_hlac =
          cmmnRuntimeService.getVariable(caseInstanceId, "revisedLtvLessThirty_hlac").toString();
      logger.info("------ revisedLtvLessThirty_hlac : {}", revisedLtvLessThirty_hlac);
      cmmnRuntimeService.setVariable(
          caseInstanceId, "RevisedLtvMain_hl", revisedLtvLessThirty_hlac);
    } else {
      String revisedLtvMoreThirty_hl =
          cmmnRuntimeService.getVariable(caseInstanceId, "revisedLtvMoreThirty_hl").toString();
      logger.info("------ revisedLtvMoreThirty_hl : {}", revisedLtvMoreThirty_hl);
      cmmnRuntimeService.setVariable(caseInstanceId, "RevisedLtvMain_hl", revisedLtvMoreThirty_hl);
    }

    int foirMain =
        Integer.parseInt(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseInstanceId, "foirMain_hl")));
    int revisedFoirMain =
        Integer.parseInt(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseInstanceId, "revisedFoirMain_hl")));
    logger.info("--------- foirMain : {} , revisedFoirMain : {}", foirMain, revisedFoirMain);

    int ltvMain =
        Integer.parseInt(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseInstanceId, "ltvMain_hl")));
    int revisedLtvMain =
        Integer.parseInt(
            CommonHelperFunctions.getStringValue(
                cmmnRuntimeService.getVariable(caseInstanceId, "RevisedLtvMain_hl")));
    logger.info("-------- ltvMain : {} , revisedLtvMain : {}", ltvMain, revisedLtvMain);

    cmmnRuntimeService.setVariable(caseInstanceId, "changeinFoirLtv", "no");
    String foirChangeFlag = "";
    String ltvChangeFlag = "";

    String existingRoleSanction =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "existingRoleSanction"));

    String finalApproveAuthFOIR = "";
    int foirDiff = Math.abs(foirMain - revisedFoirMain);
    logger.info("------- foirDiff : {}", foirDiff);
    int ltvDiff = Math.abs(ltvMain - revisedLtvMain);
    logger.info("------- ltvDiff : {}", ltvDiff);

    if (foirDiff > 0) {
      foirChangeFlag = "yes";
    }
    if (ltvDiff > 0) {
      ltvChangeFlag = "yes";
    }

    logger.info("---- foirChangeFlag : {},  ltvChangeFlag : {}", foirChangeFlag, ltvChangeFlag);

    if (foirChangeFlag.equals("yes") || ltvChangeFlag.equals("yes")) {
      eligibilityCriteriaService.finalEligibleLoanAmountForAPI(
          caseInstanceId, revisedLtvMain, revisedFoirMain);
    } else {
      cmmnRuntimeService.setVariable(caseInstanceId, "revisedLoanAmount_hl", "");
      cmmnRuntimeService.setVariable(caseInstanceId, "revisedamountpayableToBuyer_hl", "");
      cmmnRuntimeService.setVariable(caseInstanceId, "revisedamountpayableToSeller_hl", "");
    }

    if (foirChangeFlag.equals("yes") || ltvChangeFlag.equals("yes")) {
      cmmnRuntimeService.setVariable(caseInstanceId, "changeinFoirLtv", "yes");
      if (sanctionToHighAuth.equals("") || sanctionToHighAuth.equalsIgnoreCase("No")) {
        cmmnRuntimeService.setVariable(caseInstanceId, "sanctionToHighAuthDropdown", "Yes");
      }
    }

    //    if (true) {
    //      cmmnRuntimeService.setVariable(caseInstanceId, "sanctionToHighAuthDropdown", "Yes");
    //    }

    logger.info(
        "---- sanctionToHighAuthDropdown : {}",
        cmmnRuntimeService.getVariable(caseInstanceId, "sanctionToHighAuthDropdown").toString());

    cmmnRuntimeService.setVariable(caseInstanceId, "foirChangeFlag", foirChangeFlag);
    cmmnRuntimeService.setVariable(caseInstanceId, "ltvChangeFlag", ltvChangeFlag);

    if (foirDiff <= 0) {
      finalApproveAuthFOIR = existingRoleSanction;
    } else if (foirDiff <= 2) {
      finalApproveAuthFOIR = "CML2";
    } else if (foirDiff <= 5) {
      finalApproveAuthFOIR = "CML3";
    } else if (foirDiff <= 10) {
      finalApproveAuthFOIR = "NCM";
    }
    cmmnRuntimeService.setVariable(caseInstanceId, "finalApproveAuthFOIR", finalApproveAuthFOIR);

    String finalApproveAuthLTV = "";

    if (ltvDiff <= 0) {
      finalApproveAuthLTV = existingRoleSanction;
    } else if (ltvDiff <= 2) {
      finalApproveAuthLTV = "CML2";
    } else if (ltvDiff <= 5) {
      finalApproveAuthLTV = "CML3";
    } else if (ltvDiff <= 10) {
      finalApproveAuthLTV = "NCM";
    }
    cmmnRuntimeService.setVariable(caseInstanceId, "finalApproveAuthLTV", finalApproveAuthLTV);
    logger.info(
        "----- finalApproveAuthFOIR : {},    finalApproveAuthLTV : {}",
        finalApproveAuthFOIR,
        finalApproveAuthLTV);
    int higherRoleOrdinalIndex =
        (Math.max(
            Roles.valueOf(finalApproveAuthFOIR).ordinal(),
            Roles.valueOf(finalApproveAuthLTV).ordinal()));
    String finalApprovingAuthSanction = String.valueOf(Roles.values()[higherRoleOrdinalIndex]);
    logger.info("------ finalApprovingAuthSanction : {}", finalApprovingAuthSanction);
    cmmnRuntimeService.setVariable(
        caseInstanceId, "finalApprovingAuthSanction", finalApprovingAuthSanction);
    cmmnRuntimeService.setVariable(caseInstanceId, "sanctionRecalculateStatus", "Done");
    int recalculateCardSaveCount =
        Integer.parseInt(
            cmmnRuntimeService.getVariable(caseInstanceId, "recalculateCardSaveCount").toString());
    cmmnRuntimeService.setVariable(
        caseInstanceId, "recalculateCardSaveCount", recalculateCardSaveCount + 1);
  }

  public String changeRoleStatus(Map<String, Object> request) {
    logger.info("------------- in roleStatusChange button api ----------------- ");
    String caseInstanceId = request.get("caseInstanceId").toString();
    logger.info("------ caseInstanceId : {}", caseInstanceId);

    String existingRoleSanction =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "existingRoleSanction"));
    String finalApprovingAuthSanction =
        String.valueOf(
            cmmnRuntimeService.getVariable(caseInstanceId, "finalApprovingAuthSanction"));

    if (Roles.valueOf(finalApprovingAuthSanction).ordinal()
        < Roles.valueOf(existingRoleSanction).ordinal()) {
      finalApprovingAuthSanction = existingRoleSanction;
      cmmnRuntimeService.setVariable(
          caseInstanceId, "finalApprovingAuthSanction", finalApprovingAuthSanction);
    }
    if (Roles.valueOf(finalApprovingAuthSanction).ordinal()
        == Roles.valueOf(existingRoleSanction).ordinal()) {
      cmmnRuntimeService.setVariable(caseInstanceId, "enableApproveSanction", "yes");
    }

    if (Roles.valueOf(finalApprovingAuthSanction).ordinal()
        > Roles.valueOf(existingRoleSanction).ordinal()) {
      cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status", "history");
      int existingOrdinal = Roles.valueOf(existingRoleSanction).ordinal();
      existingRoleSanction = String.valueOf(Roles.values()[existingOrdinal + 1]);
      cmmnRuntimeService.setVariable(caseInstanceId, "existingRoleSanction", existingRoleSanction);
      cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Sanction", "yes");
      logger.info("--------- existingRoleSanction after increment : {}", existingRoleSanction);
    }

    return "success";
  }

  public ApiResponse directlyChangeRoleStatus(Map<String, Object> request) {
    String caseInstanceId = request.get("caseInstanceId").toString();
    String existingRoleSanction =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "existingRoleSanction"));

    cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status", "history");
    int existingOrdinal = Roles.valueOf(existingRoleSanction).ordinal();
    existingRoleSanction = String.valueOf(Roles.values()[existingOrdinal + 1]);
    cmmnRuntimeService.setVariable(caseInstanceId, "existingRoleSanction", existingRoleSanction);
    cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Sanction", "yes");

    return new ApiResponse(200, "ok", "ok");
  }

  public ApiResponse finalApproval(Map<String, Object> request) {
    logger.info("-------- inside final approval api ---------------");

    String caseInstanceId = request.get("caseInstanceId").toString();
    String existingRoleSanction =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "existingRoleSanction"));
    cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status", "history");
    cmmnRuntimeService.setVariable(
        caseInstanceId, "applicationStatus", "Sanctioned by Credit, Sales approval pending");
    cmmnRuntimeService.setVariable(caseInstanceId, "BranchManagerStatus", "sanction");

    return new ApiResponse(200, "Success", "Ok");
  }

  public ApiResponse finalRejection(Map<String, Object> request) {
    logger.info("-------- inside final rejection api ---------------");
    String caseInstanceId = request.get("caseInstanceId").toString();
    String existingRoleSanction =
        String.valueOf(cmmnRuntimeService.getVariable(caseInstanceId, "existingRoleSanction"));

    logger.info("existingRoleSanction : {}", existingRoleSanction);
    cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status", "reject");
    return new ApiResponse(200, "Success", "Ok");
  }

  enum Roles {
    CML1,
    CML2,
    CML3,
    NCM
  }

  //  public static void roleStatusChangeTest() {
  //    logger.info("------------- in roleStatusChange button api ----------------- ");
  ////    String caseInstanceId = request.get("caseInstanceId").toString();
  ////    logger.info("------ caseInstanceId : {}", caseInstanceId);
  //
  //    String finalApprovingAuthSanction = "NCM";
  //    String existingRoleSanction = "CML3";
  //
  //    if (Roles.valueOf(finalApprovingAuthSanction).ordinal() <
  // Roles.valueOf(existingRoleSanction).ordinal()) {
  //      finalApprovingAuthSanction = existingRoleSanction;
  //      System.out.println("finalApprovingAuth : "+ finalApprovingAuthSanction);
  ////      cmmnRuntimeService.setVariable(caseInstanceId, "finalApprovingAuthSanction",
  // finalApprovingAuthSanction);
  //    }
  //    if (Roles.valueOf(finalApprovingAuthSanction).ordinal() ==
  // Roles.valueOf(existingRoleSanction).ordinal()) {
  //      System.out.println("application status : Sanctioned by Credit, Sales approval ");
  ////      cmmnRuntimeService.setVariable(caseInstanceId, "applicationStatus", "Sanctioned by
  // Credit, Sales approval pending");
  //    }
  //
  //    int existingOrdinal = Roles.valueOf(existingRoleSanction).ordinal();
  //    if (Roles.valueOf(finalApprovingAuthSanction).ordinal() >
  // Roles.valueOf(existingRoleSanction).ordinal()) {
  //      String existingStatus = existingRoleSanction + "Status";
  //      System.out.println("existingStatus : "+ existingStatus +" = history");
  ////      cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status",
  // "history");
  //      existingRoleSanction = String.valueOf(Roles.values()[existingOrdinal + 1]);
  ////            cmmnRuntimeService.setVariable(caseInstanceId, "existingRoleSanction",
  // existingRoleSanction );
  //
  //      existingStatus = existingRoleSanction + "Status";
  //
  //      System.out.println("existingStatus : "+ existingStatus +" = sanction");
  ////      cmmnRuntimeService.setVariable(caseInstanceId, existingRoleSanction + "Status",
  // "sanction");
  //    }
  //  }

  //  public static void main(String[] args) {
  //    String finalApproveAuthFOIR = "CML3";
  //    String finalApproveAuthLTV = "CML2";
  //    int maxOrdinalIndex = (Math.max(Roles.valueOf(finalApproveAuthFOIR).ordinal(),
  // Roles.valueOf(finalApproveAuthLTV).ordinal()));
  //    String finalApprovingAuthSanction = String.valueOf(Roles.values()[maxOrdinalIndex]);
  //    System.out.println(finalApprovingAuthSanction);
  //
  ////    roleStatusChangeTest();
  //  }

}
