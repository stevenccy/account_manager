package com.acmebank.account_manager.account.command;

import com.acmebank.account_manager.AccountService;
import com.acmebank.account_manager.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCommandServiceTest {

    @InjectMocks
    private AccountCommandService accountCommandService;

    @Mock
    private AccountService accountService;

    @Test
    public void givenWithdrawFail_whenTransfer_shouldNotDepositInToAccount(){
        // given
        Long fromAccountId = 123L;
        Long toAccountId = 456L;
        BigDecimal amount = BigDecimal.TEN;

        doThrow(new AccountNotFoundException()).when(accountService).withdraw(fromAccountId, amount);

        // when
        accountCommandService.transfer(fromAccountId, toAccountId, amount);

        // then
        verify(accountService, never()).deposit(toAccountId, amount);

    }

    @Test
    public void givenDepositFailed_whenTransfer_shouldDepositInFromAccount(){
        // given
        Long fromAccountId = 123L;
        Long toAccountId = 456L;
        BigDecimal amount = BigDecimal.TEN;

        doThrow(new AccountNotFoundException()).when(accountService).deposit(toAccountId, amount);

        // when
        accountCommandService.transfer(fromAccountId, toAccountId, amount);

        // then
        verify(accountService, times(1)).withdraw(fromAccountId, amount);
        verify(accountService, times(1)).deposit(fromAccountId, amount);

    }

}