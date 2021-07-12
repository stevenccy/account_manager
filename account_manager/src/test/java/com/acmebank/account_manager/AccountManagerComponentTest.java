package com.acmebank.account_manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountManagerComponentTest {

    @Autowired
    private AccountRepository accountRepository;

    @LocalServerPort
    private int randomServerPort;

    @BeforeEach
    public void prepareData (){
        accountRepository.deleteAll();
        Account fromAccount = Account.builder().amount(BigDecimal.TEN).build();
        accountRepository.save(fromAccount);
        Account toAccount = Account.builder().amount(BigDecimal.TEN).build();
        accountRepository.save(toAccount);
        assertEquals(2, accountRepository.findAll().size());
    }
    @Test
    public void givenValidTransferRequest_whenTransfer_shouldHaveCorrectBalanceForFromToAccount (){
        // given
        List<Account> accounts = accountRepository.findAll();
        Long fromAccountId = accounts.get(0).getId();
        Long toAccountId = accounts.get(1).getId();

        TestRestTemplate restTemplate = new TestRestTemplate();

        TransferRequest transferRequest = TransferRequest.builder()
                .fromAccount(fromAccountId).toAccount(toAccountId).amount(BigDecimal.ONE).build();

        // when
        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest);

        restTemplate.postForEntity("http://localhost:"+ randomServerPort + "/account/transfer", request, TransferRequest.class);

        // then
        BigDecimal fromAccountBalance = accountRepository.findById(fromAccountId).map(Account::getAmount).orElse(null);
        BigDecimal toAccountBalance = accountRepository.findById(toAccountId).map(Account::getAmount).orElse(null);

        assertAll(
                ()-> assertEquals(0,BigDecimal.valueOf(9L).compareTo(fromAccountBalance)),
                ()-> assertEquals(0,BigDecimal.valueOf(11L).compareTo(toAccountBalance))
        );


    }
}
