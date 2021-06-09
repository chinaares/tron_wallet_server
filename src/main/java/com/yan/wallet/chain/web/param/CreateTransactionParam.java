package com.yan.wallet.chain.web.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionParam {
    private String id;
    private String from;
    private String to;
    private Long amount;
    private String currency;
    private String tokenAddress;
    private Integer precision;
    private String assetName;
    private String memo;
}
