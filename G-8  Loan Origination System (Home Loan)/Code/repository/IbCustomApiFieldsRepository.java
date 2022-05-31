package com.kuliza.workbench.repository;

import com.kuliza.workbench.model.IbCustomApiFields;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IbCustomApiFieldsRepository extends CrudRepository<IbCustomApiFields, Long> {}
