# 波场(TRON)钱包服务
主要实现了TRON区块链的钱包相关操作

* 地址创建
* TRX 转账创建
* TRC20 转账交易创建
* TRC10 转账交易创建
* 交易都是支持memo

## 如何使用
git clone https://github.com/yanwankun/tron_wallet_server.git

## 安装需要的依赖
```text
mvn install:install-file -Dfile=wallet-cli-4.2.1.jar -DgroupId=io.tron -DartifactId=wallet-cli -Dversion=4.2.1-SNAPSHOT -Dpackaging=jar -DlocalRepositoryPath=D:\maven\repository
mvn install:install-file -Dfile=tron-protobuf-1.0-SNAPSHOT.jar -DgroupId=org.tron -DartifactId=tron-protobuf -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DlocalRepositoryPath=D:\maven\repository
```

## 测试
```text
private static String from = "TY222tTvWPuxEv58bWmSVg6r695BCZveer";
private static String to = "TLLA27KNJuU9EFViiCkruVBTrfwhiyuUYL";
private static String tokenAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"; #这个是TRC20_USDT 的合约地址
private static String priKey = ""; # 这里填写你自己的私钥,是上面from地址的私钥
```

### 转账TRX
```
@Test
void testTransferTrx() {
    CreateTransactionParam param ;
    param = CreateTransactionParam.builder()
            .from(from)
            .to(to)
            .amount(100L)
            .currency("TRX")
            .id("test_0001")
            .precision(5)
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
```
## 转账TRC20
```text
@Test
void testTransferTrc20() {
    CreateTransactionParam param ;
    param = CreateTransactionParam.builder()
            .from(from)
            .to(to)
            .amount(100L)
            .currency("USDT")
            .id("test_0001")
            .precision(5)
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
```

## 转账TRC10
```text
@Test
void testTransferTrc10() {
    CreateTransactionParam param ;
    param = CreateTransactionParam.builder()
            .from(from)
            .to(to)
            .amount(100L)
            .currency("AAMT")
            .id("test_0001")
            .precision(5)
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
```

## 特别感谢
* tron-sdk

    我参考和借鉴了很多,解决了我写这个的很多问题
   * [jasonthinks/tron-sdk](https://github.com/jasonthinks/tron-sdk)
   
* wallet-cli

    弄这个真的是要气死人啊，首先我对gradle不是很熟悉，其次我始终没有搞明白为什么他会集成了 spring-cloud-sonsul,还好的是最后也算参考完成了我的代码编写
   * [tronprotocol/wallet-cli](https://github.com/tronprotocol/wallet-cli)
    
## 联系我
* qq : 1101186635

#### 如果你想请我喝杯可乐，我也不会拒绝的
![支付宝收款码](./static/ali_pay.jpg)
![TRON收款码](./static/tron_pay.jpg)
![微信收款码](./static/wechat_pay.jpg)




