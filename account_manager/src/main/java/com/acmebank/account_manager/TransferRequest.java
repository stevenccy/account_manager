package com.acmebank.account_manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @NotNull
    private Long fromAccount;

    @NotNull
    private Long toAccount;

    @NotNull
    @Min(0)
    private BigDecimal amount;
}
