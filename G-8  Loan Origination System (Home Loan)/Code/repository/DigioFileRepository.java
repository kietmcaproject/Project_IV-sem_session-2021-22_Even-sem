package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.DigioFileInfo;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigioFileRepository extends JpaRepository<DigioFileInfo, Long> {

  @Transactional
  DigioFileInfo findTop1ByFileNameOrderByCreatedDateDesc(String fileName);
}
