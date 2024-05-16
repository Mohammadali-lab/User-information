package com.khoo.usermanagement.service;

import com.khoo.usermanagement.entity.City;

import java.util.List;

public interface CityService {

    List<City> getAllCitiesByState(Long stateId);
}
