package com.example.Chartian.donor.repository;

import com.example.Chartian.donor.entity.Donor;
import com.example.Chartian.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    boolean existsById(Long Id);
}
