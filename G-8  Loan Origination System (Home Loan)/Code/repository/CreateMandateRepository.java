package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.CreateMandate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CreateMandateRepository extends CrudRepository<CreateMandate, Long> {
  CreateMandate findByMandateId(String mandateId);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CreateMandate f SET f.mandateId = :mandateId WHERE f.processInstanceId = :processInstanceId")
  void updateMandateId(
      @Param("processInstanceId") String processInstanceId, @Param("mandateId") String mandateId);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CreateMandate f SET f.reTriggerCount = :reTriggerCount WHERE f.processInstanceId = :processInstanceId")
  void updateReTriggerCount(
      @Param("processInstanceId") String processInstanceId,
      @Param("reTriggerCount") int reTriggerCount);
}
