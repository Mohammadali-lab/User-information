package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.City;
import com.khoo.usermanagement.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByState_Id(Long stateId);
}
