package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.Form26asApiDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface Form26asApiDetailsRepository extends JpaRepository<Form26asApiDetails, Integer> {
  Form26asApiDetails findByApplicationId(int applicationId);

  @Transactional
  @Modifying
  @Query(
      "UPDATE Form26asApiDetails f SET f.request = :request WHERE f.applicationId = :applicationId")
  void updateRequest(@Param("applicationId") int applicationId, @Param("request") String request);

  @Transactional
  @Modifying
  @Query(
      "UPDATE Form26asApiDetails f SET f.response = :response WHERE f.applicationId = :applicationId")
  void updateResponse(
      @Param("applicationId") int applicationId, @Param("response") String response);

  @Transactional
  @Modifying
  @Query(
      "UPDATE Form26asApiDetails f SET f.isTaxCredited = :isTaxCredited WHERE f.requestId = :requestId")
  void updateIsTaxCredited(
      @Param("requestId") String requestId, @Param("isTaxCredited") boolean isTaxCredited);
}
