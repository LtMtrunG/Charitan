package com.example.Chartian.donor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="TBL_DONORS")
@NoArgsConstructor
@Getter
@Setter
public class Donor {

    @Id
    @Column(name="user_id")
    private Long userId;

    @Column(name="last_name")
    private String lastName;

    @Column(name="first_name")
    private String firstName;

    @Column(name="address")
    private String address;
}
