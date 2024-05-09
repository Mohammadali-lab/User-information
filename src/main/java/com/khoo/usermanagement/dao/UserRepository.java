package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNationalCode(String nationalCode);

    List<User> findByAgeAndCity_Name(int age, String cityName);

    @Query("SELECT c.name, COUNT(u.id) FROM User u JOIN u.city c GROUP BY c.id")
    List<Object[]> countUsersByCity();

    @Query("SELECT c.name, COUNT(u.id) FROM User u JOIN u.city c WHERE u.birthDate BETWEEN :startDate AND :endDate GROUP BY c.id")
    List<Object[]> countUsersByCityAndAge(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
