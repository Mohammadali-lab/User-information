package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.City;
import com.khoo.usermanagement.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "cities", path = "cities")
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByState(State state);
}
