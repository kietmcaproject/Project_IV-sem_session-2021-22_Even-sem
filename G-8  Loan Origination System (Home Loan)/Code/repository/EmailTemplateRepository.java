package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.EmailTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, String> {

  EmailTemplate findByTemplateId(String templateId);
}
