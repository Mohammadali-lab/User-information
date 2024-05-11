package com.khoo.usermanagement.service.impl;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public User create(User user) {

        Optional<User> returnedUser = userRepository.findByNationalCode(user.getNationalCode());

        if(returnedUser.isPresent())
            return null;

        user.setConfirmed(false);
        user.setAdmin(true);

        User user1 = userRepository.save(user);
        return user1;

    }
}
