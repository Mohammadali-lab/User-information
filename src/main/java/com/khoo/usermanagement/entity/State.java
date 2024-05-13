package com.khoo.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "state")
@Getter
@Setter
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "state")
    @JsonIgnore
    private List<User> userList;

    @OneToMany(mappedBy = "state", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<City> cityList;
}
