package com.example.ebnakingbackend.service;

import com.example.ebnakingbackend.dtos.CustomerDTO;
import com.example.ebnakingbackend.entities.BankAccount;
import com.example.ebnakingbackend.entities.CurrentAccount;
import com.example.ebnakingbackend.entities.Customer;
import com.example.ebnakingbackend.entities.SavingAccount;
import com.example.ebnakingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebnakingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebnakingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    public Customer saveCustomer(Customer customer);

    CurrentAccount saveCurrentBankAccount(double initailBalance, double overDraft, Long customerId) throws CustomerNotFoundException;

    SavingAccount saveSavingBankAccount(double initailBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    List<CustomerDTO> listCustomer();

    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;

    void debit(String accoutId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;

    void credit(String accoutId, double amount, String description) throws BankAccountNotFoundException;

    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccount> bankAccountList();
}
