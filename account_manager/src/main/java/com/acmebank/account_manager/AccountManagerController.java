package com.acmebank.account_manager;

import com.acmebank.account_manager.account.command.AccountCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController()
@RequestMapping("/account")
public class AccountManagerController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountCommandService accountCommandService;

    @GetMapping("/{accountNumber}/amount")
    public BigDecimal getBalance(@PathVariable Long accountNumber){
        return accountService.getAmount(accountNumber);
    }


    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public void transfer (@Valid @RequestBody TransferRequest transferRequest){
        accountCommandService.transfer(transferRequest.getFromAccount(), transferRequest.getToAccount(), transferRequest.getAmount());
    }
}
