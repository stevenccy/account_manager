package com.acmebank.account_manager;

import com.acmebank.account_manager.exception.AccountNotFoundException;
import com.acmebank.account_manager.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public BigDecimal getAmount (Long accountNumber) {
        Optional<Account> account = accountRepository.findById(accountNumber);
        return account.map(Account::getAmount).orElseThrow(AccountNotFoundException::new);
    }

    @Transactional
    public void withdraw (Long accountNumber, BigDecimal amount){
        Optional<Account> account = accountRepository.findById(accountNumber);
        BigDecimal balance = account.map(Account::getAmount).orElseThrow(AccountNotFoundException::new);
        if (balance.compareTo(amount)< 0){
            throw new InsufficientBalanceException();
        }
        Account validAccount = account.get();
        validAccount.setAmount(balance.subtract(amount));
        accountRepository.save(validAccount);
    }

    @Transactional
    public void deposit(Long toAccount, BigDecimal amount) {
        Optional<Account> account = accountRepository.findById(toAccount);
        BigDecimal balance = account.map(Account::getAmount).orElseThrow(AccountNotFoundException::new);
        Account validAccount = account.get();
        validAccount.setAmount(balance.add(amount));
        accountRepository.save(validAccount);
    }
}
