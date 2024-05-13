package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByCountyCode(@Param("code") String code);
}
