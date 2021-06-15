package com.yan.wallet.chain.web.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionParam {
    private String from;
    private String to;
    private BigInteger amount;
    private String currency;
    private String tokenAddress;
    private String assetName;
    private String memo;
}
