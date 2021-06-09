package com.yan.wallet.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yan.wallet.chain")
@Slf4j
public class TronWalletCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TronWalletCenterApplication.class, args);
        log.info("TRON钱包服务启动成功 。。。。。。");
    }

}
