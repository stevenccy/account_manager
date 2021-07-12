package com.acmebank.account_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "account not found")
public class AccountNotFoundException extends RuntimeException{
}
