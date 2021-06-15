package com.yan.wallet.chain.tron.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trc20TransferParam {
    private String fromAddress;
    private String toAddress;
    private BigInteger amount;
    private String tokenAddress;
    private String memo;
}
