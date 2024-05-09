package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User create(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByNationalCode(String nationalCode) {
        return userRepository.findByNationalCode(nationalCode).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User update(Long id, User updatedUser) {
        User user = findById(id);
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setNationalCode(updatedUser.getNationalCode());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
