package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.ResourceNotFoundException;
import com.khoo.usermanagement.security.jwt.JwtUtil;
import com.khoo.usermanagement.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    private JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }


    public ConfirmationCode register(User user) throws DuplicateUserException {

        Optional<User> returnedUser = userRepository.findByNationalCode(user.getNationalCode());

        if(returnedUser.isPresent())
            throw new DuplicateUserException("user is already exist");

        user.setConfirmed(false);
        user.setAdmin(false);
        String code = Integer.toString(generateConfirmCode());
        user.setConfirmedCode(code);

        User user1 = userRepository.save(user);
        return new ConfirmationCode(user1.getNationalCode(), code);

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

    public List<Object[]> numberOfUsersPerCity() {
        return userRepository.countUsersByCity();
    }

    @Override
    public List<Object[]> numberOfUsersPerCityFilteredByAge(int age) {
        LocalDate startDate = LocalDate.now().minusYears(age);
        LocalDate endDate = startDate.plusYears(1);
//        Date startDateParam = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date endDateParam = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersPerCityByAge(startDate, endDate);
    }

    @Override
    public int numberOfUsersFilteredByAgeAndCity(int age, String cityName) {
        LocalDate startDate = LocalDate.now().minusYears(age);
        LocalDate endDate = startDate.plusYears(1);
//        Date startDateParam = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date endDateParam = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByCityAndAge(startDate, endDate, cityName);
    }

    @Override
    public String confirmUser(ConfirmationCode confirmationCode) {
        Optional<User> user = userRepository.findByNationalCode(confirmationCode.getUserNationalCode());
        if (user.isPresent()){
            User user1 = user.get();
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            if (user1.getCreatedAt().isAfter(fiveMinutesAgo) &&
                    confirmationCode.getConfirmedCode().equals(user1.getConfirmedCode())) {

                user1.setConfirmed(true);
                userRepository.save(user1);
                return jwtUtil.generateToken(user1.getNationalCode());

            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    public int generateConfirmCode() {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

}
