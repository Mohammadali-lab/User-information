package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CountyRepository extends JpaRepository<County, Long> {

    List<County> findByStateName(@Param("name") String name);
}
