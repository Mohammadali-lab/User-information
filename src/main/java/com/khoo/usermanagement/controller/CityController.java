package com.khoo.usermanagement.controller;

import com.khoo.usermanagement.entity.City;
import com.khoo.usermanagement.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/by-state/{stateId}")
    public List<City> getAllCitiesByState(@PathVariable Long stateId) {
        return cityService.getAllCitiesByState(stateId);
    }
}
