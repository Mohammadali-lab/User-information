package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.security.jwt.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
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


    public ConfirmationCode register(User user){

        Optional<User> returnedUser = userRepository.findByNationalCode(user.getNationalCode());

        if(returnedUser.isPresent())
            throw new DuplicateUserException("user with nationalCode " + returnedUser.get().getNationalCode() + " is already exist");

        user.setEnable(false);
        user.setAdmin(false);
        String code = Integer.toString(generateConfirmCode());
        user.setConfirmedCode(code);
        user.setConfirmCodeRegisterTime(new Date(System.currentTimeMillis()));

        User savedUser = userRepository.save(user);
        return new ConfirmationCode(savedUser.getNationalCode(), code);

    }

    public ConfirmationCode login(String nationalCode) {

        Optional<User> returnedUser = userRepository.findByNationalCode(nationalCode);

        if(!returnedUser.isPresent())
            throw new UserNotFoundException("user not found");

        User user = returnedUser.get();

        user.setEnable(false);
        String code = Integer.toString(generateConfirmCode());
        user.setConfirmedCode(code);
        user.setConfirmCodeRegisterTime(new Date(System.currentTimeMillis()));

        User savedUser = userRepository.save(user);
        return new ConfirmationCode(savedUser.getNationalCode(), code);

    }

    public void logout(String nationalCode){

        User user = userRepository.findByNationalCode(nationalCode).get();
        user.setEnable(false);
        userRepository.save(user);
    }

    public User create(User user) {

        Optional<User> returnedUser = userRepository.findByNationalCode(user.getNationalCode());

        if(returnedUser.isPresent())
            throw new DuplicateUserException("user with nationalCode " + returnedUser.get().getNationalCode() + " is already exist");

        user.setEnable(false);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user " + id + " not found"));
    }

    public User findByNationalCode(String nationalCode) {
        return userRepository.findByNationalCode(nationalCode).orElseThrow(() -> new UserNotFoundException("user " + nationalCode + " not found"));
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
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
        return userRepository.countUsersPerCityByAge(startDate, endDate);
    }

    @Override
    public int numberOfUsersFilteredByAgeAndCity(int age, String cityName) {
        LocalDate startDate = LocalDate.now().minusYears(age);
        LocalDate endDate = startDate.plusYears(1);
        return userRepository.countUsersByCityAndAge(startDate, endDate, cityName);
    }

    @Override
    public UserDTO confirmUser(ConfirmationCode confirmationCode) {
        Optional<User> returnedUser = userRepository.findByNationalCode(confirmationCode.getUserNationalCode());
        if (returnedUser.isPresent()){
            User user = returnedUser.get();
            Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            if (user.getConfirmCodeRegisterTime().after(fiveMinutesAgo) &&
                    confirmationCode.getConfirmedCode().equals(user.getConfirmedCode())) {

                user.setEnable(true);
                User savedUser = userRepository.save(user);

                String token = jwtUtil.generateToken(user.getNationalCode());
                UserDTO userDTO = new UserDTO();
                userDTO.setUser(savedUser);
                userDTO.setToken(token);
                userDTO.setMessage("user saved successfully");
                return userDTO;
            }
            throw new UnauthorizedException("otp code " + confirmationCode.getConfirmedCode() + " from user " + user.getNationalCode() + " is invalid");
        }
        throw new UserNotFoundException("user " + confirmationCode.getUserNationalCode() + " not found");
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
