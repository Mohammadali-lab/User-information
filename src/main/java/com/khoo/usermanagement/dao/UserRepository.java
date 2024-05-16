package com.khoo.usermanagement.dao;

import com.khoo.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNationalCode(String nationalCode);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT c.name, COUNT(u.id) FROM User u JOIN u.city c GROUP BY c.id")
    List<Object[]> countUsersByCity();

    @Query("SELECT c.name, COUNT(u.id) FROM User u JOIN u.city c WHERE u.birthDate BETWEEN :startDate AND :endDate GROUP BY c.id")
    List<Object[]> countUsersPerCityByAge(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(u.id) FROM User u JOIN u.city c WHERE u.birthDate BETWEEN :startDate AND :endDate AND c.id=:cityId")
    int countUsersByCityAndAge(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                               @Param("cityId") Long cityId);
}
