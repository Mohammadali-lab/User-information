package com.khoo.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "county")
@Getter
@Setter
public class County {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @OneToMany(mappedBy = "county", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<City> city;

    @OneToMany(mappedBy = "county")
    @JsonIgnore
    private List<User> userList;
}
