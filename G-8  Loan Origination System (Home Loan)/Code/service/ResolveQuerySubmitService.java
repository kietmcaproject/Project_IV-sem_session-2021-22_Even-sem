package com.kuliza.workbench.service;

import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.workbench.util.CapriUtilService;
import java.util.Map;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResolveQuerySubmitService {
  private static final Logger logger = LoggerFactory.getLogger(ResolveQuerySubmitService.class);
  @Autowired CmmnRuntimeService cmmnRuntimeService;
  @Autowired CapriUtilService capriUtilService;

  public void checkResolutionTimestamp(String caseInstanceId, String currentRole)
      throws JSONException {
    logger.info("Inside checkResolutionTimestamp........................");

    Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
    logger.info("variables Inside checkResolutionTimestamp :- {}", variables);

    JSONArray jsonArray =
        CommonHelperFunctions.objectToJSONArray(
            cmmnRuntimeService.getVariable(caseInstanceId, "listOfQueries_hl"));
    logger.info("listOfQueries_hl........... :- {}", jsonArray);

    JSONObject object = jsonArray.getJSONObject(jsonArray.length() - 1);
    //    String queryResolutionDocuments = null;
    String resolutionTimeStamp = "";
    try {
      resolutionTimeStamp = object.get("queryResolutionTimeStamp_hl").toString();
    } catch (Exception e) {
    }
    logger.info("Resolution Query Time Stamp :- {}", resolutionTimeStamp);
    //    queryResolutionDocuments =
    //        CommonHelperFunctions.getStringValue(object.get("queryResolutionDocuments_hl"));
    String queryResolutionRemarks = "";
    try {
      queryResolutionRemarks = object.get("queryResolutionRemarks_hl").toString();
    } catch (Exception e) {
    }
    logger.info("queryResolutionRemarks_hl :- {}", queryResolutionRemarks);

    for (int i = 0; i < jsonArray.length(); i++) {
      logger.info("inside for...............:-{}", i);
      if (i == (jsonArray.length() - 1)) {
        logger.info("inside 1st if...............:-{}", jsonArray.length());
        if ((resolutionTimeStamp == null) || (resolutionTimeStamp.equalsIgnoreCase(""))) {
          logger.info("inside 2nd if...............");
          if (queryResolutionRemarks != null || !queryResolutionRemarks.equalsIgnoreCase("")) {
            logger.info("inside 3rd if...............");
            resolutionTimeStamp = capriUtilService.getCurrentDate("dd-MM-yyyy");
            logger.info("query time stamp variable changed...............");
            cmmnRuntimeService.setVariable(
                caseInstanceId, "queryResolutionTimeStamp_hl", resolutionTimeStamp);
          }
        }
      }
    }
    logger.info("Query Time Stamp after updating :- {}", resolutionTimeStamp);
    cmmnRuntimeService.setVariable(caseInstanceId, currentRole + "Status", "history");
    cmmnRuntimeService.setVariable(caseInstanceId, "queryMode_hl", "off");
  }

  //  public static void main(String[] args) throws JSONException {

  //        long unix_seconds = 1612981800000;
  //// convert seconds to milliseconds
  //        Date date = new Date(unix_seconds*1000L);
  //// format of the date
  //        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
  //        jdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
  //        String java_date = jdf.format(date);
  //        System.out.println("\n"+java_date+"\n");
  //        System.out.println(Instant.ofEpochMilli( Long.parseLong( "1612981800000" ) )
  //                .toString());
  //        String date = LocalDateTime.ofInstant(
  //                Instant.ofEpochMilli(Long.valueOf("1612981800000")), ZoneId.systemDefault()
  //        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  //    Date date = new Date(Long.parseLong("1475166540000"));
  //    long date1 = date.getTime();
  //
  //    System.out.println(date);
  //    System.out.println(date1);
}
