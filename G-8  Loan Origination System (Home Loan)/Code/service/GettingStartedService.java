package com.kuliza.workbench.service;

import com.kuliza.lending.common.pojo.ApiResponse;
import com.kuliza.lending.common.utils.CommonHelperFunctions;
import com.kuliza.lending.common.utils.Constants;
import com.kuliza.workbench.model.StarterModel;
import com.kuliza.workbench.pojo.GettingStartedPojo;
import com.kuliza.workbench.repository.StarterRepository;
import com.kuliza.workbench.util.WorkbenchHelper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GettingStartedService {

  @Autowired private StarterRepository starterRepository;

  private static final Logger logger = LoggerFactory.getLogger(GettingStartedService.class);

  public ApiResponse getGlobalProp(String name) {
    List<StarterModel> props = new ArrayList<>();
    try {
      if (WorkbenchHelper.isNullOrEmpty(name)) {
        props.addAll(starterRepository.findByIsDeleted(false));
      } else {
        StarterModel prop = starterRepository.findFirstByPropertyNameAndIsDeleted(name, false);
        if (prop != null) {
          props.add(prop);
        }
      }
      return new ApiResponse(HttpStatus.OK, Constants.SUCCESS_MESSAGE, props);
    } catch (Exception e) {
      logger.error(CommonHelperFunctions.getStackTrace(e));
      return new ApiResponse(HttpStatus.OK, Constants.SUCCESS_MESSAGE, e.getMessage());
    }
  }

  public ApiResponse createOrUpdateGlobalProp(GettingStartedPojo property) {
    try {
      StarterModel propObject =
          starterRepository.findFirstByPropertyNameAndIsDeleted(property.getName(), false);
      if (propObject != null) {
        propObject.setPropertyValue(property.getValue());

      } else {
        propObject = new StarterModel(property.getName(), property.getValue());
      }
      propObject = starterRepository.save(propObject);
      return new ApiResponse(HttpStatus.OK, Constants.SUCCESS_MESSAGE, propObject);
    } catch (Exception e) {
      logger.error(CommonHelperFunctions.getStackTrace(e));
      return new ApiResponse(HttpStatus.OK, Constants.SUCCESS_MESSAGE, e.getMessage());
    }
  }
}
