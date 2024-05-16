package com.khoo.usermanagement.service.impl;

import com.khoo.usermanagement.dao.StateRepository;
import com.khoo.usermanagement.entity.State;
import com.khoo.usermanagement.service.StateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    private StateRepository stateRepository;

    public StateServiceImpl(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }
}
