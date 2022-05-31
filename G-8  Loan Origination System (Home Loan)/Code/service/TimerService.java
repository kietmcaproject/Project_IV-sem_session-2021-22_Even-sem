package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TimerService {
  private static final Logger logger = LoggerFactory.getLogger(TimerService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;

  @Scheduled(fixedRateString = "${schedule.fixrate.value}")
  public void opsPendingStatusUpdate() {
    //        thirteenMincron("Fi_UBLStatus","worklistUbl","FiPendingTimeStamp","Fi_UBL");
    thirteenMincron("FCUCoordinatorStatus", "worklist", "fcuWorklistEntry", "history");
    thirteenMincron("LegalCoordinatorStatus", "worklist", "legalWorklistEntry", "history");
    thirteenMincron("TechnicalCoordinatorStatus", "worklist", "technicalWorklistEntry", "history");
    thirteenMincron("FCUHeadStatus", "unclaim", "fcuWorklistEntry", "worklist");
    thirteenMincron("LegalHeadStatus", "unclaim", "legalWorklistEntry", "worklist");
    thirteenMincron("FCUSamplerStatus", "unclaim", "fcuWorklistEntry", "worklist");
    thirteenMincron("FCUManager", "unclaim", "fcuWorklistEntry", "worklist");
    thirteenMincron("InternalLegalManagerStatus", "unclaim", "legalWorklistEntry", "worklist");
    thirteenMincron("TechnicalHeadStatus", "unclaim", "technicalWorklistEntry", "worklist");

    //        thirteenMincron("RCU_UBLStatus","worklistUbl","RcuPendingTimeStamp","RCU_UBL");
    //
    // thirteenMincron("CREDIT_UBLStatus","sanctionCredit","CreditPendingTimeStamp","CREDIT_UBL");
    //        thirteenMincron("SALES_UBLStatus","Pending","SalesPendingTimeStamp","SALES_UBL");

  }

  public void thirteenMincron(
      String variableKey, String variableValue, String timeStampNew, String changeVariable2) {
    logger.info("<< inside timeStampNew Update ..>> ");
    List<CaseInstance> caseInstanceList = new ArrayList<>();
    CaseInstanceQuery caseInstanceQuery =
        cmmnRuntimeService
            .createCaseInstanceQuery()
            .includeCaseVariables()
            .variableValueEquals(variableKey, variableValue);
    caseInstanceList = caseInstanceQuery.orderByStartTime().desc().list();
    logger.info("caseInstanceList :-" + caseInstanceList.size());
    logger.info("variableKey :- {}", variableKey);
    logger.info("variableValue :- {}", variableValue);
    for (CaseInstance caseInstance : caseInstanceList) {
      String caseInstanceId = caseInstance.getId();
      Map<String, Object> processVariables = new HashMap<>();
      processVariables = cmmnRuntimeService.getVariables(caseInstanceId);
      String applicationId = processVariables.getOrDefault("applicationId", "").toString();
      String opsStatusValue = processVariables.getOrDefault(variableKey, "").toString();
      logger.info(
          "For applicationId {} the current ops Status is {}", applicationId, opsStatusValue);
      if (!opsStatusValue.equalsIgnoreCase(variableValue)) {
        logger.info("applicationId {} not in pending status", applicationId);
        continue;
      }

      String timeStamp = processVariables.getOrDefault(timeStampNew, "fail").toString();
      if (timeStamp.equals("fail")) {
        cmmnRuntimeService.setVariable(caseInstanceId, timeStampNew, new Date().getTime());
      }
      logger.info("time Stamp :-" + timeStamp);
      logger.info(
          "cas Id :-"
              + CommonHelperFunctions.getStringValue(
                  cmmnRuntimeService.getVariable(caseInstanceId, "caseInstanceId")));
      if (!timeStamp.isEmpty()
          && checkTimeStampExceedFifteenMins(
              CommonHelperFunctions.getStringValue(timeStamp), applicationId)) {
        logger.info("Changing Status for application ID " + applicationId);
        try {
          cmmnRuntimeService.setVariable(caseInstanceId, variableKey, changeVariable2);
          cmmnRuntimeService.setVariable(caseInstanceId, timeStampNew, new Date().getTime());
        } catch (Exception e) {

        }
      }
    }
    logger.info("<< exits timeStampNew Update ..>> ");
  }

  public static boolean checkTimeStampExceedFifteenMins(String timestamp, String applicationId) {
    try {
      long time = new Date().getTime();

      logger.info("Current time stamp" + time + "Exiting time stamp" + timestamp);
      logger.info("for applicationId: {},time Stamp: {} ", applicationId, timestamp);
      logger.info("for applicationId: {}, current time Stamp: {} ", applicationId, time);
      long pendingTimeStamp = Long.parseLong(timestamp);
      long diff = time - pendingTimeStamp;
      logger.info("for applicationId: {}, diff in time Stamp: {} ", applicationId, diff);
      logger.info("Difference for application {} is {} ", applicationId, diff);

      long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

      logger.info("time :- {}", minutes);

      if (minutes > 60) {
        logger.info("Returning true for application in scheduler: {}", applicationId);
        return true;
      }
      return false;
    } catch (Exception e) {
      logger.error(
          "error for checkTimeStampExceedFifteenMins for application id {} is {}",
          applicationId,
          e);
      return false;
    }
  }
}
