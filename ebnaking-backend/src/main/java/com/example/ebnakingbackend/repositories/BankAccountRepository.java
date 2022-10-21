package com.example.ebnakingbackend.repositories;

import com.example.ebnakingbackend.entities.BankAccount;
import com.example.ebnakingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
