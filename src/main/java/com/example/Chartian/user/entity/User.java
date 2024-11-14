package com.example.Chartian.user.entity;

import com.example.Chartian.utils.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="TBL_USERS")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="is_verified")
    private boolean isVerified;

    @ManyToOne
    private Role role;
}
