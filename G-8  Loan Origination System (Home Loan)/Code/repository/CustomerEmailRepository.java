package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.CustomerEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerEmailRepository extends JpaRepository<CustomerEmail, Integer> {

  CustomerEmail findByApplicationId(int applicationId);

  @Transactional
  @Modifying
  @Query("UPDATE CustomerEmail f SET f.reqId = :reqId WHERE f.applicationId = :applicationId")
  void updateReqId(@Param("applicationId") int applicationId, @Param("reqId") String reqId);

  @Transactional
  @Modifying
  @Query("UPDATE CustomerEmail f SET f.customerVerified = :customerVerified WHERE f.reqId = :reqId")
  void updateCustomerVerified(
      @Param("reqId") String reqId, @Param("customerVerified") String customerVerified);

  @Transactional
  @Modifying
  @Query("UPDATE CustomerEmail f SET f.isLinkTriggered = :isLinkTriggered WHERE f.reqId = :reqId")
  void updateLinkTriggered(
      @Param("reqId") String reqId, @Param("isLinkTriggered") boolean isLinkTriggered);

  @Transactional
  @Modifying
  @Query("UPDATE CustomerEmail f SET f.email = :email WHERE f.applicationId = :applicationId")
  void updateEmail(@Param("applicationId") int applicationId, @Param("email") String email);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CustomerEmail f SET f.verification = :verification WHERE f.applicationId = :applicationId")
  void updateVerification(
      @Param("applicationId") int applicationId, @Param("verification") String verification);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CustomerEmail f SET f.isApplicantEmailPresent = :isApplicantEmailPresent WHERE f.applicationId = :applicationId")
  void updateIsApplicantEmailPresent(
      @Param("applicationId") int applicationId,
      @Param("isApplicantEmailPresent") boolean isApplicantEmailPresent);
}
