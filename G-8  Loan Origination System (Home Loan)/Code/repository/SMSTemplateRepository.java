package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.SMSTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSTemplateRepository extends CrudRepository<SMSTemplate, String> {

  SMSTemplate findBySmsIndex(String smsNumber);
}
