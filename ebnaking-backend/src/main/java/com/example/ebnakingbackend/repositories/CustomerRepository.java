package com.example.ebnakingbackend.repositories;

import com.example.ebnakingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
