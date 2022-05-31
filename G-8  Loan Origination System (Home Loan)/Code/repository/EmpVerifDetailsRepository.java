package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.EmpVerifDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpVerifDetailsRepository extends JpaRepository<EmpVerifDetails, Integer> {
  EmpVerifDetails findByApplicationId(int applicationId);
}
