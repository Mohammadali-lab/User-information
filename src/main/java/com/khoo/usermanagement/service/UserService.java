package com.khoo.usermanagement.service;

import com.khoo.usermanagement.entity.User;

public interface UserService {

    User create(User user);

    User findById(Long id);

    User findByNationalCode(String nationalCode);

    User update(Long id, User updatedUser);

    void delete(Long id);
}
