package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.BcmAllocationDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BcmAllocationDetailsRepository
    extends CrudRepository<BcmAllocationDetails, Integer> {
  BcmAllocationDetails findByBranchName(String branchName);

  @Transactional
  @Modifying
  @Query("UPDATE BcmAllocationDetails f SET f.count = :count WHERE f.branchName = :branchName")
  void updateCount(@Param("branchName") String branchName, @Param("count") int count);
}
