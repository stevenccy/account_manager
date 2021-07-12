package com.acmebank.account_manager;

import com.acmebank.account_manager.account.command.AccountCommandService;
import com.acmebank.account_manager.exception.AccountNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountManagerController.class)
class AccountManagerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountCommandService accountCommandService;

    @Test
    public void givenAnValidAccount_whenGetAmount_thenReturnAmount() throws Exception {
        // given
        Long accountNum = 123L;
        BigDecimal expectedAmount = BigDecimal.valueOf(1000L);
        doReturn(expectedAmount).when(accountService).getAmount(accountNum);

        // when, then
        this.mockMvc.perform(get("/account/{accountNum}/amount", accountNum)).andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedAmount.toString())));
    }

    @Test
    public void givenAccountNotFound_whenGetAmount_thenReturnAmount() throws Exception {
        // given
        Long accountNum = 12345L;
        doThrow(new AccountNotFoundException()).when(accountService).getAmount(accountNum);

        // when, then
        this.mockMvc.perform(get("/account/{accountNum}/amount", accountNum)).
                andExpect(status().isNotFound());
    }

    @Test
    public void givenTransferRequestMissingFromAccount_whenTransfer_thenReturnBadRequest() throws Exception {
        // given
        Long toAccount = 123L;
        TransferRequest transferRequest = TransferRequest.builder().toAccount(toAccount).amount(BigDecimal.TEN).build();

        // when, then
        this.mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());
        verify(accountCommandService, never()).transfer(anyLong(), anyLong(), any());
    }

    @Test
    public void givenTransferRequestNegativeAmount_whenTransfer_thenReturnBadRequest() throws Exception {
        // given
        Long toAccount = 123L;
        Long fromAccount = 456L;
        // when, then
        TransferRequest transferRequest = TransferRequest.builder().fromAccount(fromAccount).toAccount(toAccount).amount(BigDecimal.valueOf(-1L)).build();
        this.mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());
        verify(accountCommandService, never()).transfer(anyLong(), anyLong(), any());
    }

    @Test
    public void givenValidTransferRequest_whenTransfer_thenCallAccountCommandService() throws Exception {
        // given
        Long toAccount = 123L;
        Long fromAccount = 456L;
        // when, then
        TransferRequest transferRequest = TransferRequest.builder().fromAccount(fromAccount).toAccount(toAccount).amount(BigDecimal.TEN).build();
        this.mockMvc.perform(post("/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().is(201));
        verify(accountCommandService, times(1)).transfer(fromAccount, toAccount, BigDecimal.TEN);

    }

}