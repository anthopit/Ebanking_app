package com.example.ebnakingbackend.repositories;

import com.example.ebnakingbackend.entities.AccountOperation;
import com.example.ebnakingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {
}
