package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.LoanApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("LoanApplicationRepository")
public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Long> {
  LoanApplication findByInquiryNumber(String inquiryNumber);

  LoanApplication findTop1ByOrderByCreatedDesc();
}
