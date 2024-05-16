package com.khoo.usermanagement.service.impl;

import com.khoo.usermanagement.dao.CityRepository;
import com.khoo.usermanagement.entity.City;
import com.khoo.usermanagement.entity.State;
import com.khoo.usermanagement.service.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAllCitiesByState(Long stateId) {

        return cityRepository.findAllByState_Id(stateId);
    }

}
