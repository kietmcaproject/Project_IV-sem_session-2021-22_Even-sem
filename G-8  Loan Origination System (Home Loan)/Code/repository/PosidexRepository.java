package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.Posidex;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("PosidexRepository")
public interface PosidexRepository extends CrudRepository<Posidex, Long> {
  Posidex findTop1ByOrderByCreatedDesc();
}
