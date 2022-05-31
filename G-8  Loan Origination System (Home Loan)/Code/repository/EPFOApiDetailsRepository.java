package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.EPFOApiDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EPFOApiDetailsRepository extends CrudRepository<EPFOApiDetails, Integer> {

  EPFOApiDetails findByApplicationId(int applicationId);

  @Transactional
  @Modifying
  @Query("UPDATE EPFOApiDetails f SET f.request = :request WHERE f.applicationId = :applicationId")
  void updateRequest(@Param("applicationId") int applicationId, @Param("request") String request);

  @Transactional
  @Modifying
  @Query(
      "UPDATE EPFOApiDetails f SET f.response = :response WHERE f.applicationId = :applicationId")
  void updateResponse(
      @Param("applicationId") int applicationId, @Param("response") String response);

  @Transactional
  @Modifying
  @Query(
      "UPDATE EPFOApiDetails f SET f.employmentFiStatus = :employmentFiStatus WHERE f.applicationId = :applicationId")
  void updateEmploymentFiStatus(
      @Param("applicationId") int applicationId,
      @Param("employmentFiStatus") String employmentFiStatus);
}
