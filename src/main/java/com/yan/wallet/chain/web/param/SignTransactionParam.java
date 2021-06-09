package com.yan.wallet.chain.web.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignTransactionParam {
    private String unSignTx;
    private String priKey;
}
