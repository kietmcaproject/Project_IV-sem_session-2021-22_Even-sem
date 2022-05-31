package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.StarterModel;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarterRepository extends CrudRepository<StarterModel, Long> {

  public StarterModel findFirstByPropertyNameAndIsDeleted(String propertyName, Boolean isDelelted);

  public List<StarterModel> findByIsDeleted(Boolean isDelelted);
}
