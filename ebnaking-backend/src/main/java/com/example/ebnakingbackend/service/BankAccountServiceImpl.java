package com.example.ebnakingbackend.service;

import com.example.ebnakingbackend.dtos.CustomerDTO;
import com.example.ebnakingbackend.entities.*;
import com.example.ebnakingbackend.enums.OperationType;
import com.example.ebnakingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebnakingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebnakingbackend.exceptions.CustomerNotFoundException;
import com.example.ebnakingbackend.mappers.BankAccoutMapperImpl;
import com.example.ebnakingbackend.repositories.AccountOperationRepository;
import com.example.ebnakingbackend.repositories.BankAccountRepository;
import com.example.ebnakingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccoutMapperImpl dtoMapper;


    @Override
    public Customer saveCustomer(Customer customer){
        log.info("Saving new customer");
        Customer savedCustomer = customerRepository.save(customer);
        return  savedCustomer;
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initailBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw  new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateDat(new Date());
        currentAccount.setBalance(initailBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);

        CurrentAccount saveBankAccount = bankAccountRepository.save(currentAccount);

        return saveBankAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initailBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw  new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateDat(new Date());
        savingAccount.setBalance(initailBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);

        SavingAccount saveBankAccount = bankAccountRepository.save(savingAccount);

        return saveBankAccount;
    }

    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer> customers=customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        /*
        List<CustomerDTO> customerDTOS=new ArrayList<>();
        for (Customer customer:customers){
            CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }
        */
        return customerDTOS;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not "));

        return bankAccount;
    }

    @Override
    public void debit(String accoutId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=getBankAccount(accoutId);
        if (bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Banlance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accoutId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=getBankAccount(accoutId);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount,"Transfert to "+accountIdDestination);
        credit(accountIdDestination, amount, "Transfert to"+accountIdSource);
    }

    @Override
    public List<BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
}
