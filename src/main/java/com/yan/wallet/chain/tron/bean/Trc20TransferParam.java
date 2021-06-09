package com.yan.wallet.chain.tron.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trc20TransferParam {
    private String fromAddress;
    private String toAddress;
    private Long amount;
    private String tokenAddress;
    private String memo;
}
