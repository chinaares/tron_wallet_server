package com.yan.wallet.chain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxBeanInfo {
    private String id;
    private String currency;
    private Integer txType;
}
