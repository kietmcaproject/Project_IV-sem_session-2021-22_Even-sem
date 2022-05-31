package com.kuliza.workbench.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CapriUtilService {
  private static final Logger logger = LoggerFactory.getLogger(CapriUtilService.class);

  public String getRandomAlphanumericKey(int size) {
    String AlphaNumericString =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
    StringBuilder sb = new StringBuilder(size);
    for (int i = 0; i < size; i++) {
      int index = (int) (AlphaNumericString.length() * Math.random());
      sb.append(AlphaNumericString.charAt(index));
    }
    return sb.toString();
  }

  public String getCurrentDate(String format) {
    SimpleDateFormat myFormat = new SimpleDateFormat(format);
    String currentDate = myFormat.format(new Date());
    return currentDate;
  }

  public LocalTime getCurrentTime() {
    return java.time.LocalTime.now();
  }

  public String encodeBase64String(String IN_FILE) throws IOException {
    byte[] inFileBytes = Files.readAllBytes(Paths.get(IN_FILE));
    //    byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(inFileBytes);
    String encodedString = Base64.getEncoder().encodeToString(inFileBytes);
    logger.info("encoded: {}", encodedString);
    return encodedString;
  }

  public String fileToBase64(MultipartFile file) throws IOException {
    byte[] fileContent = file.getBytes();
    String encodedStr = Base64.getEncoder().encodeToString(fileContent);
    return encodedStr;
  }

  public void decodeBase64(byte[] encoded, String OUT_FILE) throws IOException {
    byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);

    FileOutputStream fos = new FileOutputStream(OUT_FILE);
    fos.write(decoded);
    fos.flush();
    fos.close();
  }

  public String getCurrentAssessmentYear() {
    // calendar object will provide current date instance
    Calendar calendar = Calendar.getInstance();

    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);
    logger.info("day : " + day + " month : " + month + " year : " + year);
    String assessmentYear = "";

    // month index start form 0 (e.g; Jan is 0, Feb is 1 and so no...)
    if (month == 0 || month == 1 || month == 2) {
      int currentYear = calendar.get(Calendar.YEAR);
      String currentYearStr = String.valueOf(currentYear);
      logger.info("currentYearStr : " + currentYearStr);
      int nextYear = (calendar.get(Calendar.YEAR) % 100) + 1;
      String nextYearStr = String.valueOf(nextYear);
      assessmentYear = currentYearStr.concat("-").concat(nextYearStr);
    } else {
      int currentYear = calendar.get(Calendar.YEAR) + 1;
      String currentYearStr = String.valueOf(currentYear);
      logger.info("currentYearStr : " + currentYearStr);
      int nextYear = (calendar.get(Calendar.YEAR) % 100) + 2;
      String nextYearStr = String.valueOf(nextYear);
      assessmentYear = currentYearStr.concat("-").concat(nextYearStr);
    }
    logger.info("assessmentYear : " + assessmentYear);
    return assessmentYear;
  }

  public List<String> getCurrentQuarterMonths() {
    Calendar myCal = new GregorianCalendar();
    myCal.add(Calendar.MONTH, 1);
    logger.info("current date for quarter : {}", myCal.getTime());
    int quarter = (myCal.get(Calendar.MONTH) / 3) + 1;
    logger.info("quarter : {}", quarter);
    List<String> quarterMonths = new ArrayList<>();
    switch (quarter) {
      case 1:
        quarterMonths.add("01");
        quarterMonths.add("02");
        quarterMonths.add("03");
        break;
      case 2:
        quarterMonths.add("04");
        quarterMonths.add("05");
        quarterMonths.add("06");
        break;
      case 3:
        quarterMonths.add("07");
        quarterMonths.add("08");
        quarterMonths.add("09");
        break;
      case 4:
        quarterMonths.add("10");
        quarterMonths.add("11");
        quarterMonths.add("12");
        break;
    }
    logger.info("quarter months : {}", quarterMonths);
    return quarterMonths;
  }

  public Date unixTimestampToDate(String unixTimestamp) {
    Date date = new Date(Long.parseLong(unixTimestamp));
    return date;
  }

  public long dateToUnixTimestamp(Date date) {
    long unixTimeStamp = date.getTime();
    return unixTimeStamp;
  }
}
