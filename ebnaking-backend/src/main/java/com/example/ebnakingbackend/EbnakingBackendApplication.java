package com.example.ebnakingbackend;

import com.example.ebnakingbackend.entities.*;
import com.example.ebnakingbackend.enums.AccountStatus;
import com.example.ebnakingbackend.enums.OperationType;
import com.example.ebnakingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebnakingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebnakingbackend.exceptions.CustomerNotFoundException;
import com.example.ebnakingbackend.repositories.AccountOperationRepository;
import com.example.ebnakingbackend.repositories.BankAccountRepository;
import com.example.ebnakingbackend.repositories.CustomerRepository;
import com.example.ebnakingbackend.service.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbnakingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbnakingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Adeline", "Mehdi", "Adrien").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setMail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 9000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*12000, 5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                    for (BankAccount bankAccount:bankAccounts){
                        for (int i = 0; i<10; i++){
                            bankAccountService.credit(bankAccount.getId() , 10000+Math.random()*120000, "Credit");
                            bankAccountService.debit(bankAccount.getId(), 1000+Math.random()*9000, "Debit");
                        }
                    }
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                } catch (BankAccountNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (BalanceNotSufficientException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }
    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Anthony", "Thibault", "Hamid").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setMail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*900000);
                currentAccount.setCreateDat(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*900000);
                savingAccount.setCreateDat(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(acc->{
                for (int i = 0; i<10 ; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }
            });
        };
    }
}
