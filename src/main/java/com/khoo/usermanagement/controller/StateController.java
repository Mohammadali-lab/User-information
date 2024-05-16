package com.khoo.usermanagement.controller;

import com.khoo.usermanagement.entity.State;
import com.khoo.usermanagement.service.StateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/states")
public class StateController {

    private StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping()
    public List<State> getAllStates() {
        return stateService.getAllStates();
    }
}
