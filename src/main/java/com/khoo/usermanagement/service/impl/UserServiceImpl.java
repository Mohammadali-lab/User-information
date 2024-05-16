package com.khoo.usermanagement.service.impl;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.security.jwt.JwtUtil;
import com.khoo.usermanagement.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    private RedisTemplate<Long, User> redisTemplate;

    private UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    private JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, UserDetailsService userDetailsService,
                           JwtUtil jwtUtil, RedisTemplate<Long, User> redisTemplate) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }


    public ConfirmationCode register(User user){

        Optional<User> returnedUser = userRepository.findByNationalCode(user.getNationalCode());

        if(returnedUser.isPresent())
            throw new DuplicateUserException("user with nationalCode " + returnedUser.get().getNationalCode() + " is already exist");

        user.setEnable(false);
        user.setAdmin(false);
        user.setRemoved(false);

        String code = Integer.toString(generateConfirmCode());
        user.setConfirmedCode(code);
        user.setConfirmCodeRegisterTime(new Date(System.currentTimeMillis()));

        User savedUser = userRepository.save(user);
        return new ConfirmationCode(savedUser.getNationalCode(), code);

    }

    public ConfirmationCode login(User user) {

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
        user.setRemoved(false);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        User user = readUserFromRedis(id);
        if(user!=null)
            return user;
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user " + id + " not found"));
    }

    public User findByNationalCode(String nationalCode) {

        Optional<User> user = userRepository.findByNationalCode(nationalCode);
        if(user.isPresent() && !user.get().isRemoved())
            return user.get();
        throw new UserNotFoundException("user " + nationalCode + " not found");
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User updateProfile(String nationalCode, User updatedUser) {

        User user = userRepository.findByNationalCode(nationalCode).get();
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setBirthDate(updatedUser.getBirthDate());
        if(updatedUser.getCity()!=null)
            user.setCity(updatedUser.getCity());
        if(updatedUser.getState()!=null)
            user.setState(updatedUser.getState());
        saveUserInRedis(user);
        return userRepository.save(user);

    }

    public User update(Long id, User user) {

        User returnedUser = findById(id);
        if(returnedUser==null)
            throw new UserNotFoundException("user" + id + "not found");
        user.setId(id);
        saveUserInRedis(user);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        if(user==null)
            throw new UserNotFoundException("user" + id + "not found");
        user.setRemoved(true);
        userRepository.save(user);
    }

    public List<Object[]> numberOfUsersPerCity() {
        return userRepository.countUsersByCity();
    }

    @Override
    public List<Object[]> numberOfUsersPerCityFilteredByAge(int age) {
        LocalDate date1 = LocalDate.now().minusYears(age);
        LocalDate date2 = date1.plusYears(1);
        Date startDate = Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(date2.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersPerCityByAge(startDate, endDate);
    }

    @Override
    public int numberOfUsersFilteredByAgeAndCity(int age, Long cityId) {
        LocalDate date1 = LocalDate.now().minusYears(age);
        LocalDate date2 = date1.plusYears(1);
        Date startDate = Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(date2.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return userRepository.countUsersByCityAndAge(startDate, endDate, cityId);
    }

    @Override
    public UserDTO confirmUser(User user, String code) {

        Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
        if (user.getConfirmCodeRegisterTime().after(fiveMinutesAgo) &&
                code.equals(user.getConfirmedCode())) {

            user.setEnable(true);
            User savedUser = userRepository.save(user);

            saveUserInRedis(savedUser);

            String token = jwtUtil.generateToken(user.getNationalCode());
            UserDTO userDTO = new UserDTO();
            userDTO.setUser(savedUser);
            userDTO.setToken(token);
            userDTO.setMessage("user saved successfully");
            return userDTO;
        }
        throw new UnauthorizedException("otp code " + code + " from user " + user.getNationalCode() + " is invalid");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    private int generateConfirmCode() {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    void saveUserInRedis(User user) {

        try {
            redisTemplate.opsForValue().set(user.getId(), user);
        } catch (Exception ex) {
            System.out.println("*** Could not connect to Redis ***");
        }
    }

    public User readUserFromRedis(Long id) {

        try {
            return redisTemplate.opsForValue().get(id);
        } catch (Exception ex) {
            System.out.println("*** Could not connect to Redis ***");
            return null;
        }
    }

}
