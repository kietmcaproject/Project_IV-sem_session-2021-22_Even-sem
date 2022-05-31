package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.PragatiAppDetails;
import org.springframework.data.repository.CrudRepository;

public interface PragatiAppDetailsRepository extends CrudRepository<PragatiAppDetails, Integer> {

  PragatiAppDetails findByApplicationId(int applicationId);

  PragatiAppDetails findByKulizaAppId(String kulizaAppId);

  PragatiAppDetails findByUcic(String ucic);
}
