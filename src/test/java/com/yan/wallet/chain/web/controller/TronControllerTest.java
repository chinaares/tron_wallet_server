package com.yan.wallet.chain.web.controller;

import com.alibaba.fastjson.JSON;
import com.yan.wallet.chain.web.param.BroadCastTransactionParam;
import com.yan.wallet.chain.web.param.CreateTransactionParam;
import com.yan.wallet.chain.web.param.SignTransactionParam;
import com.yan.wallet.chain.web.view.BaseResult;
import com.yan.wallet.chain.web.view.CreateTxView;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TronControllerTest {

    @Autowired
    private TronController tronController;

    private static String from = "TY222tTvWPuxEv58bWmSVg6r695BCZveer";
    private static String to = "TLLA27KNJuU9EFViiCkruVBTrfwhiyuUYL";
    private static String tokenAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
    private static String priKey = "";

    @Test
    void testTransferTrx() {
        CreateTransactionParam param ;
        param = CreateTransactionParam.builder()
                .from(from)
                .to(to)
                .amount(BigInteger.valueOf(100))
                .currency("TRX")
                .memo("test_add_0001")
                .build();
        BaseResult<CreateTxView> createTxViewBaseResult = tronController.create(param);
        System.out.println("create => " + JSON.toJSONString(createTxViewBaseResult));
        sleep(12);
        SignTransactionParam signTransactionParam = SignTransactionParam.builder()
                .priKey(priKey)
                .unSignTx(createTxViewBaseResult.getData().getUnSignTxStr())
                .build();
        BaseResult<String> signResult = tronController.signTx(signTransactionParam);
        System.out.println("signTx => " + JSON.toJSONString(signResult));
        sleep(12);
        BroadCastTransactionParam broadCastTransactionParam = BroadCastTransactionParam.builder()
                .signStr(signResult.getData())
                .build();
        BaseResult broadCastResult = tronController.broadCast(broadCastTransactionParam);
        System.out.println("broadCast => " + JSON.toJSONString(broadCastResult));
    }

    void sleep(int value) {
        try {
            Thread.sleep(value * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTransferTrc20() {
        CreateTransactionParam param ;
        param = CreateTransactionParam.builder()
                .from(from)
                .to(to)
                .amount(BigInteger.valueOf(100))
                .currency("USDT")
                .memo("test_add_0001")
                .tokenAddress(tokenAddress)
                .build();
        BaseResult<CreateTxView> createTxViewBaseResult = tronController.create(param);
        System.out.println("create => " + JSON.toJSONString(createTxViewBaseResult));
        sleep(12);
        SignTransactionParam signTransactionParam = SignTransactionParam.builder()
                .priKey(priKey)
                .unSignTx(createTxViewBaseResult.getData().getUnSignTxStr())
                .build();
        BaseResult<String> signResult = tronController.signTx(signTransactionParam);
        System.out.println("signTx => " + JSON.toJSONString(signResult));
        sleep(12);
        BroadCastTransactionParam broadCastTransactionParam = BroadCastTransactionParam.builder()
                .signStr(signResult.getData())
                .build();
        BaseResult broadCastResult = tronController.broadCast(broadCastTransactionParam);
        System.out.println("broadCast => " + JSON.toJSONString(broadCastResult));

    }

    @Test
    void testTransferTrc10() {
        CreateTransactionParam param ;
        param = CreateTransactionParam.builder()
                .from(from)
                .to(to)
                .amount(BigInteger.valueOf(100))
                .currency("AAMT")
                .memo("test_add_0001")
                .assetName("31303034303731")
                .build();
        BaseResult<CreateTxView> createTxViewBaseResult = tronController.create(param);
        System.out.println("create => " + JSON.toJSONString(createTxViewBaseResult));
        SignTransactionParam signTransactionParam = SignTransactionParam.builder()
                .priKey(priKey)
                .unSignTx(createTxViewBaseResult.getData().getUnSignTxStr())
                .build();
        BaseResult<String> signResult = tronController.signTx(signTransactionParam);
        System.out.println("signTx => " + JSON.toJSONString(signResult));
        BroadCastTransactionParam broadCastTransactionParam = BroadCastTransactionParam.builder()
                .signStr(signResult.getData())
                .build();
        BaseResult broadCastResult = tronController.broadCast(broadCastTransactionParam);
        System.out.println("broadCast => " + JSON.toJSONString(broadCastResult));

    }

    @Test
    void create() {
        CreateTransactionParam param ;
        param = CreateTransactionParam.builder()
                .from(from)
                .to(to)
                .amount(BigInteger.valueOf(100))
                .currency("USDT")
                .memo("test_add_0001")
                .tokenAddress(tokenAddress)
                .build();
        BaseResult<CreateTxView> result = tronController.create(param);
        System.out.println("create => " + JSON.toJSONString(result));
    }


    @Test
    void broadCast() {
        BroadCastTransactionParam param = BroadCastTransactionParam.builder()
                .signStr("0AE2010A02B50222080C18271B9E200AF240E8B68EF49E2F520D746573745F6164645F303030315AAE01081F12A9010A31747970652E676F6F676C65617069732E636F6D2F70726F746F636F6C2E54726967676572536D617274436F6E747261637412740A1541F1DADBEF0E07D4B30D5C4BF0CEBCFE52BE4F26A9121541A614F803B6FD780986A42C78EC9C7F77E6DED13C2244A9059CBB00000000000000000000004171A780FE891C036EC8044B8FA13EAF6C6652487200000000000000000000000000000000000000000000000000000000000000647099F68AF49E2F900180ADE20412419DD223E617E623F9E3028DCD46DE9DA5DA5BBC1F024987E85D7FEF88A09DF24565604C0D3D02A7E88DD61C6619E8C8982F1FE605987F560AC657DF79D41EC8F800")
                .build();
        BaseResult result = tronController.broadCast(param);
        System.out.println("broadCast => " + JSON.toJSONString(result));
    }

    @Test
    void signTx() {
        SignTransactionParam param = SignTransactionParam.builder()
                .priKey(priKey)
                .unSignTx("0AE2010A02B50222080C18271B9E200AF240E8B68EF49E2F520D746573745F6164645F303030315AAE01081F12A9010A31747970652E676F6F676C65617069732E636F6D2F70726F746F636F6C2E54726967676572536D617274436F6E747261637412740A1541F1DADBEF0E07D4B30D5C4BF0CEBCFE52BE4F26A9121541A614F803B6FD780986A42C78EC9C7F77E6DED13C2244A9059CBB00000000000000000000004171A780FE891C036EC8044B8FA13EAF6C6652487200000000000000000000000000000000000000000000000000000000000000647099F68AF49E2F900180ADE204")
                .build();
        BaseResult result = tronController.signTx(param);
        System.out.println("signTx => " + JSON.toJSONString(result));
    }

    @Test
    void genTrxKey() {
        BaseResult result = tronController.genTrxKey();
        System.out.println("genTrxKey => " + JSON.toJSONString(result));
    }
}