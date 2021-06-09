package com.yan.wallet.chain.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wallet")
@Data
@Slf4j
public class TronConfig {
    private Boolean isHotWallet;
}
