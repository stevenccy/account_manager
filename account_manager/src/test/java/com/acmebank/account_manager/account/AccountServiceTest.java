package com.acmebank.account_manager.account;

import com.acmebank.account_manager.Account;
import com.acmebank.account_manager.AccountRepository;
import com.acmebank.account_manager.AccountService;
import com.acmebank.account_manager.exception.AccountNotFoundException;
import com.acmebank.account_manager.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Captor
    ArgumentCaptor<Account> accountArgumentCaptor;

    @Test
    public void givenCannotFindAccount_whenWithdraw_thenThrowAccountNotFoundError (){
        // given
        Long accountId = 123L;
        doReturn(Optional.empty()).when(accountRepository).findById(accountId);

        // when, then
        assertThrows(AccountNotFoundException.class, ()->accountService.withdraw(accountId, BigDecimal.ONE));
    }

    @Test
    public void givenBalanceLessThanWithdrawAmount_whenWithdraw_thenThrowInsufficientBalanceException (){
        // given
        Long accountId = 123L;
        doReturn(Optional.of(Account.builder().id(accountId).amount(BigDecimal.ONE).build())).when(accountRepository).findById(accountId);

        // when, then
        assertThrows(InsufficientBalanceException.class, ()->accountService.withdraw(accountId, BigDecimal.valueOf(2L)));
    }

    @Test
    public void givenNormalAccount_whenWithdraw_thenSaveSubtractedAmount (){
        // given
        Long accountId = 123L;
        Account account = Account.builder().id(accountId).amount(BigDecimal.TEN).build();
        doReturn(Optional.of(account)).when(accountRepository).findById(accountId);

        // when
        accountService.withdraw(accountId, BigDecimal.ONE);

        // then
        verify(accountRepository, times(1)).save(accountArgumentCaptor.capture());
        Account savedAccount = accountArgumentCaptor.getValue();
        assertEquals(0, BigDecimal.valueOf(9L).compareTo(savedAccount.getAmount()));
    }

    @Test
    public void givenCannotFindAccount_whenDeposit_thenThrowAccountNotFoundError (){
        // given
        Long accountId = 123L;
        doReturn(Optional.empty()).when(accountRepository).findById(accountId);

        // when, then
        assertThrows(AccountNotFoundException.class, ()->accountService.deposit(accountId, BigDecimal.ONE));
    }

    @Test
    public void givenNormalAccount_whenDeposit_thenSaveAddedAmount (){
        // given
        Long accountId = 123L;
        Account account = Account.builder().id(accountId).amount(BigDecimal.TEN).build();
        doReturn(Optional.of(account)).when(accountRepository).findById(accountId);

        // when
        accountService.deposit(accountId, BigDecimal.ONE);

        // then
        verify(accountRepository, times(1)).save(accountArgumentCaptor.capture());
        Account savedAccount = accountArgumentCaptor.getValue();
        assertEquals(0, BigDecimal.valueOf(11L).compareTo(savedAccount.getAmount()));
    }
}