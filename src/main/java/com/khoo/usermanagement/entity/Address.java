package com.khoo.usermanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "address")
    private String address;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    @ManyToOne
    @JoinColumn(name = "state_id")
    public State state;
}
