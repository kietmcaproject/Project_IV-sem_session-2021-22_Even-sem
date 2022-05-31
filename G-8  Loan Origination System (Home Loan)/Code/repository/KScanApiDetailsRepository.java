package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.KScanApiDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface KScanApiDetailsRepository extends CrudRepository<KScanApiDetails, Integer> {
  KScanApiDetails findByApplicationId(int applicationId);

  @Transactional
  @Modifying
  @Query("UPDATE KScanApiDetails f SET f.request = :request WHERE f.applicationId = :applicationId")
  void updateRequest(@Param("applicationId") int applicationId, @Param("request") String request);

  @Transactional
  @Modifying
  @Query(
      "UPDATE KScanApiDetails f SET f.response = :response WHERE f.applicationId = :applicationId")
  void updateResponse(
      @Param("applicationId") int applicationId, @Param("response") String response);

  @Transactional
  @Modifying
  @Query(
      "UPDATE KScanApiDetails f SET f.empSearchRequest = :empSearchRequest WHERE f.applicationId = :applicationId")
  void updateEmpSearchRequest(
      @Param("applicationId") int applicationId,
      @Param("empSearchRequest") String empSearchRequest);

  @Transactional
  @Modifying
  @Query(
      "UPDATE KScanApiDetails f SET f.empSearchResponse = :empSearchResponse WHERE f.applicationId = :applicationId")
  void updateEmpSearchResponse(
      @Param("applicationId") int applicationId,
      @Param("empSearchResponse") String empSearchResponse);

  @Transactional
  @Modifying
  @Query(
      "UPDATE KScanApiDetails f SET f.isUniqueMatch = :isUniqueMatch WHERE f.applicationId = :applicationId")
  void updateIsUniqueMatch(
      @Param("applicationId") int applicationId, @Param("isUniqueMatch") boolean isUniqueMatch);

  @Transactional
  @Modifying
  @Query(
      "UPDATE KScanApiDetails f SET f.isNameFound = :isNameFound WHERE f.applicationId = :applicationId")
  void updateIsNameFound(
      @Param("applicationId") int applicationId, @Param("isNameFound") boolean isNameFound);
}
