package com.yan.wallet.chain.tron;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yan.wallet.chain.config.TronConfig;
import com.yan.wallet.chain.tron.bean.Trc10TransferParam;
import com.yan.wallet.chain.tron.bean.Trc20TransferParam;
import com.yan.wallet.chain.tron.bean.TrxTransferParam;
import com.yan.wallet.chain.utils.NumUtils;
import com.yan.wallet.chain.utils.StringByteUtils;
import com.yan.wallet.chain.utils.YanObjectUtils;
import com.yan.wallet.chain.utils.YanStrUtils;
import com.yan.wallet.chain.web.view.BaseResult;
import com.yan.wallet.chain.web.view.CreateTxView;
import com.yan.wallet.chain.web.view.TrxKeyView;
import lombok.extern.slf4j.Slf4j;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Sm3Hash;
import org.tron.common.utils.*;
import org.tron.core.config.Parameter;
import org.tron.protos.Protocol;
import org.tron.protos.contract.AssetIssueContractOuterClass;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.walletserver.GrpcClient;
import org.tron.walletserver.WalletApi;

import java.math.BigInteger;
import java.util.Arrays;

@Slf4j
@Component
public class TronUtils implements InitializingBean {

    private static GrpcClient grpcClient;

    @Autowired
    private TronConfig tronConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (tronConfig.getIsHotWallet()) {
            grpcClient = WalletApi.init();
        }
    }

    public static BaseResult<TrxKeyView> genTrxKey() {
        ECKey eCkey = new ECKey(Utils.getRandom());
        byte[] address = eCkey.getAddress();
        byte[] hash0 = Sha256Sm3Hash.hash(address);
        byte[] hash1 = Sha256Sm3Hash.hash(hash0);
        byte[] checkSum = Arrays.copyOfRange(hash1, 0, 4);
        byte[] addCheckSum = new byte[address.length + 4];
        System.arraycopy(address, 0, addCheckSum, 0, address.length);
        System.arraycopy(checkSum, 0, addCheckSum, address.length, 4);
        String base58 = Base58.encode(addCheckSum);

        return new BaseResult<>().makeSuccessResult(TrxKeyView.builder()
                .priKey(StringByteUtils.bytes2HexString(eCkey.getPrivateKey()))
                .address(base58)
                .build());
    }

    public BaseResult<CreateTxView> createTrxTransfer(TrxTransferParam param) {
        if (tronConfig.getIsHotWallet()) {
            return new BaseResult<>().makeFailedResult("Not a hot wallet, can’t handle internet operations");
        }
        byte[] from = WalletApi.decodeFromBase58Check(param.getFromAddress());
        byte[] to = WalletApi.decodeFromBase58Check(param.getToAddress());
        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
        Protocol.Block newestBlock = WalletApi.getBlock(-1);
        /*设置合约内部数据*/
        Protocol.Transaction.Contract.Builder contractBuilder = Protocol.Transaction.Contract.newBuilder();
        BalanceContract.TransferContract.Builder transferContractBuilder =
                BalanceContract.TransferContract.newBuilder();
        transferContractBuilder.setAmount(param.getAmount());
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsOwner = ByteString.copyFrom(from);
        transferContractBuilder.setToAddress(bsTo);
        transferContractBuilder.setOwnerAddress(bsOwner);
        try {
            Any any = Any.pack(transferContractBuilder.build());
            contractBuilder.setParameter(any);
        } catch (Exception e) {
            return null;
        }
        contractBuilder.setType(Protocol.Transaction.Contract.ContractType.TransferContract);
        if (YanObjectUtils.notEmpty(param.getMemo())) {
            transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                    .setData(ByteString.copyFromUtf8(param.getMemo())).setScripts(ByteString.copyFromUtf8("scripts"));
        } else {
            transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                    .setScripts(ByteString.copyFromUtf8("scripts"));
        }
        Protocol.Transaction transaction = transactionBuilder.build();
        transaction = setReference(transaction, newestBlock);
        return processTransaction(transaction);
    }

    public BaseResult<CreateTxView> createTrc20Transfer(Trc20TransferParam param) {
        if (tronConfig.getIsHotWallet()) {
            return new BaseResult<>().makeFailedResult("Not a hot wallet, can’t handle internet operations");
        }
        byte[] shieldedContractAddressPadding = new byte[32];
        byte[] shieldedTRC20 = decode58Check(param.getToAddress());
        System.arraycopy(shieldedTRC20, 0,
                shieldedContractAddressPadding, 11, 21);
//        byte[] valueBytes = longTo32Bytes(param.getAmount());
        byte[] valueBytes = bigInterTo32Byte(param.getAmount());
        String input = Hex.toHexString(ByteUtil.merge(shieldedContractAddressPadding, valueBytes));
        byte[] tokenAddress = decode58Check(param.getTokenAddress());
        return triggerContract(tokenAddress, "transfer(address,uint256)", input, true,
                0L, 10000000L, "0", 0, param.getFromAddress(), param.getMemo());
    }

    public BaseResult<CreateTxView> createTrc10Transfer(Trc10TransferParam param) {
        if (tronConfig.getIsHotWallet()) {
            return new BaseResult<>().makeFailedResult("Not a hot wallet, can’t handle internet operations");
        }
        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
        Protocol.Transaction.Contract.Builder contractBuilder = Protocol.Transaction.Contract.newBuilder();
        AssetIssueContractOuterClass.TransferAssetContract.Builder transferAssetContractBuilder = AssetIssueContractOuterClass.TransferAssetContract
                .newBuilder();
        byte[] from = getAddressFromAddress(param.getFromAddress());
        byte[] to = getAddressFromAddress(param.getToAddress());
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsOwner = ByteString.copyFrom(from);
        transferAssetContractBuilder.setAmount(param.getAmount());
        transferAssetContractBuilder.setAssetName(ByteString.copyFrom(ByteArray.fromHexString(param.getAssetName())));
        transferAssetContractBuilder.setOwnerAddress(bsOwner);
        transferAssetContractBuilder.setToAddress(bsTo);
        try {
            Any any = Any.pack(transferAssetContractBuilder.build());
            contractBuilder.setParameter(any);
        } catch (Exception e) {
            return null;
        }
        /*设置备注，交易过期时间等数据*/
        contractBuilder.setType(Protocol.Transaction.Contract.ContractType.TransferAssetContract);
        if (YanObjectUtils.notEmpty(param.getMemo())) {
            transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                    .setData(ByteString.copyFromUtf8(param.getMemo()))
                    .setScripts(ByteString.copyFromUtf8("scripts"));
        } else {
            transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                    .setScripts(ByteString.copyFromUtf8("scripts"));
        }
        Protocol.Transaction transaction = transactionBuilder.build();
        Protocol.Block newestBlock = WalletApi.getBlock(-1);
        transaction = setReference(transaction, newestBlock);
        return processTransaction(transaction);
    }

    public BaseResult broadCastTx(String signStr) {
        BaseResult result = new BaseResult();
        if (tronConfig.getIsHotWallet()) {
            return result.makeFailedResult("Not a hot wallet, can’t handle internet operations");
        }
        try {
            boolean bool = WalletApi.broadcastTransaction(StringByteUtils.hexString2Bytes(signStr));
            if (bool) {
                return result.makeSuccessResult();
            } else {
                return result.makeFailedResult();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return result.makeFailedResult(e.getMessage());
        }
    }

    private static byte[] decode58Check(String input) {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        } else {
            byte[] decodeData = new byte[decodeCheck.length - 4];
            System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
            byte[] hash0 = Sha256Sm3Hash.hash(decodeData);
            byte[] hash1 = Sha256Sm3Hash.hash(hash0);
            return hash1[0] == decodeCheck[decodeData.length] && hash1[1] == decodeCheck[decodeData.length + 1] && hash1[2] == decodeCheck[decodeData.length + 2] && hash1[3] == decodeCheck[decodeData.length + 3] ? decodeData : null;
        }
    }

    private static byte[] longTo32Bytes(long value) {
        byte[] longBytes = ByteArray.fromLong(value);
        byte[] zeroBytes = new byte[24];
        return ByteUtil.merge(zeroBytes, longBytes);
    }

    public static byte[] bigInterTo32Byte(BigInteger value) {
        String value1 = NumUtils.bigInterToHexString(value);
        if (value1.startsWith("0x") || value1.startsWith("0X")) {
            value1 = value1.substring(2);
        }
        value1 = YanStrUtils.lPend(value1, "0", 64);
        return StringByteUtils.hexString2Bytes(value1);
    }

    private static byte[] getAddressFromAddress(String address) {
        return StringByteUtils.hexString2Bytes(StringByteUtils.bytes2HexString(Base58.decode(address)).substring(0,42));
    }

    private static BaseResult<CreateTxView> triggerContract(byte[] contractAddress, String method, String argsStr,
                                          Boolean isHex, long callValue, long feeLimit, String tokenId, long tokenValue,
                                          String fromAddress, String memo) {
        WalletApi.setAddressPreFixByte(Parameter.CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);

        byte[] owner = getAddressFromAddress(fromAddress);
        byte[] input = Hex.decode(AbiUtil.parseMethod(method, argsStr, isHex));
        SmartContractOuterClass.TriggerSmartContract.Builder builder = SmartContractOuterClass.TriggerSmartContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setContractAddress(ByteString.copyFrom(contractAddress));
        builder.setData(ByteString.copyFrom(input));
        builder.setCallValue(callValue);
        builder.setTokenId(Long.parseLong(tokenId));
        builder.setCallTokenValue(tokenValue);
        SmartContractOuterClass.TriggerSmartContract triggerContract = builder.build();

        GrpcAPI.TransactionExtention transactionExt = grpcClient.triggerContract(triggerContract);
        if (transactionExt == null || !transactionExt.getResult().getResult()) {
            log.info("RPC create call trx failed!");
            log.info("Code = " + transactionExt.getResult().getCode());
            log.info("Message = " + transactionExt.getResult().getMessage().toStringUtf8());
            return new BaseResult<>().makeFailedResult(transactionExt.getResult().getCode().getNumber(), transactionExt.getResult().getMessage().toStringUtf8());
        }
        Protocol.Transaction transaction = transactionExt.getTransaction();
        if (transaction.getRetCount() != 0
                && transactionExt.getConstantResult(0) != null
                && transactionExt.getResult() != null) {
            byte[] resultByte = transactionExt.getConstantResult(0).toByteArray();
            log.info("message:" + transaction.getRet(0).getRet());
            log.info(":" + ByteArray.toStr(transactionExt.getResult().getMessage().toByteArray()));
            log.info("Result:" + Hex.toHexString(resultByte));
            return new BaseResult<>().makeFailedResult(transaction.getRet(0).getRet().getNumber(), transactionExt.getResult().getMessage().toStringUtf8());
        }

        final GrpcAPI.TransactionExtention.Builder texBuilder = GrpcAPI.TransactionExtention.newBuilder();
        Protocol.Transaction.Builder transBuilder = Protocol.Transaction.newBuilder();
        Protocol.Transaction.raw.Builder rawBuilder = transactionExt.getTransaction().getRawData()
                .toBuilder();
        if (memo != null && !memo.trim().equals("")) {
            rawBuilder.setData(ByteString.copyFromUtf8(memo));
        }
        rawBuilder.setFeeLimit(feeLimit);

        transBuilder.setRawData(rawBuilder);
        for (int i = 0; i < transactionExt.getTransaction().getSignatureCount(); i++) {
            ByteString s = transactionExt.getTransaction().getSignature(i);
            transBuilder.setSignature(i, s);
        }
        for (int i = 0; i < transactionExt.getTransaction().getRetCount(); i++) {
            Protocol.Transaction.Result r = transactionExt.getTransaction().getRet(i);
            transBuilder.setRet(i, r);
        }
        texBuilder.setTransaction(transBuilder);
        texBuilder.setResult(transactionExt.getResult());
        texBuilder.setTxid(transactionExt.getTxid());

        transactionExt = texBuilder.build();
        if (transactionExt == null) {
            return null;
        }
        GrpcAPI.Return ret = transactionExt.getResult();
        if (!ret.getResult()) {
            log.info("Code = " + ret.getCode());
            log.info("Message = " + ret.getMessage().toStringUtf8());
            return null;
        }
        transaction = transactionExt.getTransaction();
        Protocol.Block newestBlock = WalletApi.getBlock(-1);
        transaction = setReference(transaction, newestBlock);
        return processTransaction(transaction);
    }

    private static BaseResult<CreateTxView> processTransaction(Protocol.Transaction transaction) {
        BaseResult<CreateTxView> result = new BaseResult<>();
        String txId = ByteArray.toHexString(Sha256Sm3Hash.hash(
                transaction.getRawData().toByteArray()));
        return result.makeSuccessResult(CreateTxView.builder()
                .txId(txId)
                .unSignTxStr(StringByteUtils.bytes2HexString(transaction.toByteArray()))
                .build());
    }

    /*设置参照块数据*/
    public static Protocol.Transaction setReference(Protocol.Transaction transaction, Protocol.Block newestBlock) {
        long blockHeight = newestBlock.getBlockHeader().getRawData().getNumber();
        byte[] blockHash = getBlockHash(newestBlock).getBytes();
        byte[] refBlockNum = ByteArray.fromLong(blockHeight);
        Protocol.Transaction.raw rawData = transaction.getRawData().toBuilder()
                .setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)))
                .setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)))
                .setRefBlockNum(blockHeight)
                .setTimestamp(System.currentTimeMillis())
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000)
                .build();
        return transaction.toBuilder().setRawData(rawData).build();
    }

    public static Sha256Sm3Hash getBlockHash(Protocol.Block block) {
        return Sha256Sm3Hash.of(block.getBlockHeader().getRawData().toByteArray());
    }

    public BaseResult signTxWithKey(String priKey, String unSignTx) {
        BaseResult result = new BaseResult();
        byte[] privateBytes = ByteArray.fromHexString(priKey);
        try {
            String hexStr = StringByteUtils.bytes2HexString(signTransaction2Byte(StringByteUtils.hexString2Bytes(unSignTx), privateBytes));
            return result.makeSuccessResult(hexStr);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return result.makeFailedResult(e.getMessage());
        }
    }

    public static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
            throws InvalidProtocolBufferException {
        ECKey ecKey = ECKey.fromPrivate(privateKey);
        Protocol.Transaction transaction1 = Protocol.Transaction.parseFrom(transaction);
        byte[] rawData = transaction1.getRawData().toByteArray();
        byte[] hash = Sha256Sm3Hash.hash(rawData);
        byte[] sign = ecKey.sign(hash).toByteArray();
        return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
    }

}
