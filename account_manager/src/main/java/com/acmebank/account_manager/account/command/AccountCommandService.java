package com.acmebank.account_manager.account.command;

import com.acmebank.account_manager.AccountService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Log
@Service
public class AccountCommandService {

    @Autowired
    private AccountService accountService;

    public void transfer (Long fromAccount, Long toAccount, BigDecimal amount){
        try{
            accountService.withdraw(fromAccount, amount);
            handleTransferWithdrawSuccess(fromAccount, toAccount, amount);
        }catch (Exception e){
            log.severe(e.getMessage());
        }
    }

    private void handleTransferWithdrawSuccess (Long fromAccount, Long toAccount, BigDecimal amount){
        try{
            accountService.deposit(toAccount, amount);
        }catch (Exception e){
            log.severe(e.getMessage());
            handleTransferDepositFail(fromAccount, amount);
        }
    }

    private void handleTransferDepositFail (Long fromAccount, BigDecimal amount){
        accountService.deposit(fromAccount, amount);
    }

}
