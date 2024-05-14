package com.khoo.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.*;
import java.util.Date;

@Entity
@Table(name = "user")
@Setter
@Getter
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "national_code", nullable = false, unique = true)
    private String nationalCode;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "is_enable")
    private boolean isEnable;

    @Column(name = "confirmed_code")
    private String confirmedCode;

    @Column(name = "confirm_code_register_time")
    private Date confirmCodeRegisterTime;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    public int getAge() {
        Date now = new Date();
        Instant nowInstant = now.toInstant();
        LocalDate nowDate = LocalDate.ofInstant(nowInstant, ZoneId.systemDefault());
        return Period.between(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), nowDate).getYears();
    }

}
