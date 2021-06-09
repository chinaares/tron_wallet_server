//package com.yan.wallet.chain.tron.test;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.protobuf.Any;
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.yan.wallet.chain.utils.StringByteUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.spongycastle.util.encoders.Hex;
//import org.springframework.stereotype.Component;
//import org.tron.api.GrpcAPI;
//import org.tron.common.crypto.Hash;
//import org.tron.common.crypto.*;
//import org.tron.common.utils.*;
//import org.tron.core.config.Parameter;
//import org.tron.core.exception.CancelException;
//import org.tron.protos.Protocol;
//import org.tron.protos.Protocol.Block;
//import org.tron.protos.Protocol.Transaction;
//import org.tron.protos.contract.*;
//import org.tron.walletserver.GrpcClient;
//import org.tron.walletserver.WalletApi;
//
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.tron.demo.ShieldedTRC20Demo.signTransaction;
//
//@Slf4j
//@Component
//public class TransactionSignDemo {
//
//    private static GrpcClient grpcClient = WalletApi.init();
//    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();
//
//    /*设置参照块数据*/
//    public static Transaction setReference(Transaction transaction, Block newestBlock) {
//        long blockHeight = newestBlock.getBlockHeader().getRawData().getNumber();
//        byte[] blockHash = getBlockHash(newestBlock).getBytes();
//        byte[] refBlockNum = ByteArray.fromLong(blockHeight);
//        Transaction.raw rawData = transaction.getRawData().toBuilder()
//                .setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)))
//                .setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)))
//                .setRefBlockNum(blockHeight)
//                .build();
//        return transaction.toBuilder().setRawData(rawData).build();
//    }
//
//    public static Sha256Sm3Hash getBlockHash(Block block) {
//        return Sha256Sm3Hash.of(block.getBlockHeader().getRawData().toByteArray());
//    }
//
//    public static String getTransactionHash(Transaction transaction) {
//        String txid = ByteArray.toHexString(Sha256Sm3Hash.hash(transaction.getRawData().toByteArray()));
//        return txid;
//    }
//
//    public static Transaction createTransaction(byte[] from, byte[] to, long amount) {
//        Transaction.Builder transactionBuilder = Transaction.newBuilder();
//        Block newestBlock = WalletApi.getBlock(-1);
//        /*设置合约内部数据*/
//        Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
//        BalanceContract.TransferContract.Builder transferContractBuilder =
//                BalanceContract.TransferContract.newBuilder();
//        transferContractBuilder.setAmount(amount);
//        ByteString bsTo = ByteString.copyFrom(to);
//        ByteString bsOwner = ByteString.copyFrom(from);
//        transferContractBuilder.setToAddress(bsTo);
//        transferContractBuilder.setOwnerAddress(bsOwner);
//        try {
//            Any any = Any.pack(transferContractBuilder.build());
//            contractBuilder.setParameter(any);
//        } catch (Exception e) {
//            return null;
//        }
//        /*设置备注，交易过期时间等数据*/
//        contractBuilder.setType(Transaction.Contract.ContractType.TransferContract);
//        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
//                .setTimestamp(System.currentTimeMillis())
//                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000)
//                .setData(ByteString.copyFromUtf8("memo"))
//                .setScripts(ByteString.copyFromUtf8("scripts"));
//        Transaction transaction = transactionBuilder.build();
//        Transaction refTransaction = setReference(transaction, newestBlock);
//        return refTransaction;
//    }
//
//    private static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
//            throws InvalidProtocolBufferException {
//        ECKey ecKey = ECKey.fromPrivate(privateKey);
//        Transaction transaction1 = Transaction.parseFrom(transaction);
//        byte[] rawdata = transaction1.getRawData().toByteArray();
//        byte[] hash = Sha256Sm3Hash.hash(rawdata);
//        byte[] sign = ecKey.sign(hash).toByteArray();
//        return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
//    }
//
//    private static boolean broadcast(byte[] transactionBytes) throws InvalidProtocolBufferException {
//        return WalletApi.broadcastTransaction(transactionBytes);
//    }
//
//    public static void main(String[] args) throws InvalidProtocolBufferException, CancelException {
//        String privateStr = "";
//        byte[] privateBytes = ByteArray.fromHexString(privateStr);
//        ECKey ecKey = ECKey.fromPrivate(privateBytes);
//        byte[] from = ecKey.getAddress();
//        byte[] to = WalletApi.decodeFromBase58Check("TN9RRaXkCFtTXRso2GdTZxSxxwufzxLQPP");
//        long amount = 100_000_000L; // 100 TRX, api only receive trx in Sun, and 1 trx = 1000000 Sun
//        Transaction transaction = createTransaction(from, to, amount);
//        byte[] transactionBytes = transaction.toByteArray();
//        byte[] transaction4 = signTransaction2Byte(transactionBytes, privateBytes);
//        boolean result = broadcast(transaction4);
////
////        System.out.println(result);
//
//        String txId = transfer("",
//                "TLLA27KNJuU9EFViiCkruVBTrfwhiyuUYL", 1L, "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
//        System.out.println("txId : " + txId);
//
////        String priKey = "";
////        try {
////            BigInteger priK = new BigInteger(priKey, 16);
////            ECKey temKey = ECKey.fromPrivate(priK);
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
//
////        byte[] address = getAddressFromPk("");
////        System.out.println(StringByteUtils.bytes2HexString(address));
//////        testGenKey();
////        String address2 = StringByteUtils.bytes2HexString(Base58.decode("TY222tTvWPuxEv58bWmSVg6r695BCZveer")).substring(0,42);
////        System.out.println(address2);
//
//    }
//
//    public static void testGenKey() {
//        ECKey eCkey = null;
//        String priKeyHex = "";
//        try {
//            BigInteger priK = new BigInteger(priKeyHex, 16);
//            eCkey = ECKey.fromPrivate(priK);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return;
//        }
//
//        byte[] pubKey = eCkey.getPubKey();
//        byte[] hash = Hash.sha3(Arrays.copyOfRange(pubKey, 1, pubKey.length));
//        byte[] hash_ = Hash.sha3(pubKey);
//        byte[] address = eCkey.getAddress();
//        byte[] hash0 = Sha256Sm3Hash.hash(address);
//        byte[] hash1 = Sha256Sm3Hash.hash(hash0);
//        byte[] checkSum = Arrays.copyOfRange(hash1, 0, 4);
//        byte[] addchecksum = new byte[address.length + 4];
//        System.arraycopy(address, 0, addchecksum, 0, address.length);
//        System.arraycopy(checkSum, 0, addchecksum, address.length, 4);
//        String base58 = Base58.encode(addchecksum);
//        String base58Address = WalletApi.encode58Check(address);
//
//        String pubKeyString = ByteArray.toHexString(pubKey);
//        System.out.println("priKeyHex:::" + priKeyHex);
//        System.out.println("pubKeyString:::" + pubKeyString);
//        System.out.println("hash:::" + ByteArray.toHexString(hash));
//        System.out.println("hash_:::" + ByteArray.toHexString(hash_));
//        System.out.println("address:::" + ByteArray.toHexString(address));
//        System.out.println("hash0:::" + ByteArray.toHexString(hash0));
//        System.out.println("hash1:::" + ByteArray.toHexString(hash1));
//        System.out.println("checkSum:::" + ByteArray.toHexString(checkSum));
//        System.out.println("addchecksum:::" + ByteArray.toHexString(addchecksum));
//        System.out.println("base58:::" + base58);
//        System.out.println("base58Address:::" + base58Address);
//    }
//
//    private static byte[] longTo32Bytes(long value) {
//        byte[] longBytes = ByteArray.fromLong(value);
//        byte[] zeroBytes = new byte[24];
//        return ByteUtil.merge(zeroBytes, longBytes);
//    }
//
//
//    private static byte[] decode58Check(String input) {
//        byte[] decodeCheck = Base58.decode(input);
//        if (decodeCheck.length <= 4) {
//            return null;
//        } else {
//            byte[] decodeData = new byte[decodeCheck.length - 4];
//            System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
//            byte[] hash0 = Sha256Sm3Hash.hash(decodeData);
//            byte[] hash1 = Sha256Sm3Hash.hash(hash0);
//            return hash1[0] == decodeCheck[decodeData.length] && hash1[1] == decodeCheck[decodeData.length + 1] && hash1[2] == decodeCheck[decodeData.length + 2] && hash1[3] == decodeCheck[decodeData.length + 3] ? decodeData : null;
//        }
//    }
//
//    public static String transfer(String privateKey, String toAddress, long amount, String contractAddress) {
//        byte[] shieldedContractAddressPadding = new byte[32];
//        byte[] shieldedTRC20 = decode58Check(toAddress);
//        System.arraycopy(shieldedTRC20, 0,
//                shieldedContractAddressPadding, 11, 21);
//        byte[] valueBytes = longTo32Bytes(amount);
//        String input = Hex.toHexString(ByteUtil.merge(shieldedContractAddressPadding, valueBytes));
//        byte[] myTrc20 = decode58Check(contractAddress);
//        return triggerContract(myTrc20, "transfer(address,uint256)", input, true,
//                0L, 10000000L, "0", 0, privateKey);
//    }
//
//    public static byte[] getAddressFromPk(String pk) {
//        ECKey ecKey = ECKey.fromPrivate(ByteArray.fromHexString(pk));
//        return ecKey.getAddress();
//    }
//
//    private static String triggerContract(byte[] contractAddress, String method, String argsStr,
//                                   Boolean isHex, long callValue, long feeLimit, String tokenId, long tokenValue,
//                                   String priKey) {
//        WalletApi.setAddressPreFixByte(Parameter.CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
//        ECKey temKey = null;
//        try {
//            BigInteger priK = new BigInteger(priKey, 16);
//            temKey = ECKey.fromPrivate(priK);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        final ECKey ecKey = temKey;
//        if (argsStr.equalsIgnoreCase("#")) {
//            argsStr = "";
//        }
//
//        byte[] owner = getAddressFromPk(priKey);
//        byte[] input = Hex.decode(AbiUtil.parseMethod(method, argsStr, isHex));
//        SmartContractOuterClass.TriggerSmartContract.Builder builder = SmartContractOuterClass.TriggerSmartContract.newBuilder();
//        builder.setOwnerAddress(ByteString.copyFrom(owner));
//        builder.setContractAddress(ByteString.copyFrom(contractAddress));
//        builder.setData(ByteString.copyFrom(input));
//        builder.setCallValue(callValue);
//        builder.setTokenId(Long.parseLong(tokenId));
//        builder.setCallTokenValue(tokenValue);
//        SmartContractOuterClass.TriggerSmartContract triggerContract = builder.build();
//
//        GrpcAPI.TransactionExtention transactionExtention = grpcClient.triggerContract(triggerContract);
//        if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
//            System.out.println("RPC create call trx failed!");
//            System.out.println("Code = " + transactionExtention.getResult().getCode());
//            System.out
//                    .println("Message = " + transactionExtention.getResult().getMessage().toStringUtf8());
//            return null;
//        }
//        Transaction transaction = transactionExtention.getTransaction();
//        if (transaction.getRetCount() != 0
//                && transactionExtention.getConstantResult(0) != null
//                && transactionExtention.getResult() != null) {
//            byte[] result = transactionExtention.getConstantResult(0).toByteArray();
//            System.out.println("message:" + transaction.getRet(0).getRet());
//            System.out.println(":" + ByteArray
//                    .toStr(transactionExtention.getResult().getMessage().toByteArray()));
//            System.out.println("Result:" + Hex.toHexString(result));
//            return null;
//        }
//
//        final GrpcAPI.TransactionExtention.Builder texBuilder = GrpcAPI.TransactionExtention.newBuilder();
//        Transaction.Builder transBuilder = Transaction.newBuilder();
//        Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
//                .toBuilder();
//        rawBuilder.setData(ByteString.copyFromUtf8("memo124354"));
//        rawBuilder.setFeeLimit(feeLimit);
//
//        transBuilder.setRawData(rawBuilder);
//        for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
//            ByteString s = transactionExtention.getTransaction().getSignature(i);
//            transBuilder.setSignature(i, s);
//        }
//        for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
//            Transaction.Result r = transactionExtention.getTransaction().getRet(i);
//            transBuilder.setRet(i, r);
//        }
//        texBuilder.setTransaction(transBuilder);
//        texBuilder.setResult(transactionExtention.getResult());
//        texBuilder.setTxid(transactionExtention.getTxid());
//
//        transactionExtention = texBuilder.build();
//        if (transactionExtention == null) {
//            return null;
//        }
//        GrpcAPI.Return ret = transactionExtention.getResult();
//        if (!ret.getResult()) {
//            System.out.println("Code = " + ret.getCode());
//            System.out.println("Message = " + ret.getMessage().toStringUtf8());
//            return null;
//        }
//        transaction = transactionExtention.getTransaction();
//        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
//            System.out.println("Transaction is empty");
//            return null;
//        }
//
////        String key = "shdkcahsdkjhasjd";
////        cacheMap.put(key, transaction);
//        Transaction transaction1;
//        try {
//            transaction1 = Transaction.parseFrom(transaction.toByteArray());
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//            return null;
//        }
//        transaction1 = signTransaction(ecKey, transaction1);
//
////        transaction = TransactionUtils.setTimestamp(transaction);
////        byte[] hash = Sha256Sm3Hash.hash(transaction.getRawData().toByteArray());
////        String valueStr = StringByteUtils.bytes2HexString(hash);
////
////        String signStr = signTx(valueStr, "");
////        ByteString byteString = ByteString.copyFrom(StringByteUtils.hexString2Bytes(signStr));
////
////        Transaction.Builder transactionBuilderSigned = transaction.toBuilder();
////        transactionBuilderSigned.addSignature(byteString);
////        transaction = transactionBuilderSigned.build();
//
//        String txid = ByteArray.toHexString(Sha256Sm3Hash.hash(
//                transaction1.getRawData().toByteArray()));
//        System.out.println("trigger txid = " + txid);
////        WalletApi.broadcastTransaction(transaction);
//        return txid;
//    }
//
//
//
//    private static String signTx(String data, String key) {
//        byte[] hash = StringByteUtils.hexString2Bytes(data);
//        ECKey eCkey = null;
//        try {
//            BigInteger priK = new BigInteger(key, 16);
//            eCkey = ECKey.fromPrivate(priK);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "";
//        }
//
//        SignatureInterface signature = eCkey.sign(hash);
//        ByteString bsSign = ByteString.copyFrom(signature.toByteArray());
//        return StringByteUtils.bytes2HexString(bsSign.toByteArray());
//    }
//
////    public static Transaction signTransaction(ECKey ecKey, Transaction transaction) {
////        WalletApi.setAddressPreFixByte(Parameter.CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
////        if (ecKey == null || ecKey.getPrivKey() == null) {
////            //logger.warn("Warning: Can't sign,there is no private key !!");
////            return null;
////        }
////        transaction = TransactionUtils.setTimestamp(transaction);
////        log.info("Txid in sign is " + ByteArray.toHexString(
////                Sha256Sm3Hash.hash(transaction.getRawData().toByteArray())));
////        return sign(transaction, ecKey);
////    }
//
//    public static Transaction sign(Transaction transaction, SignInterface myKey) {
//        Transaction.Builder transactionBuilderSigned = transaction.toBuilder();
//        byte[] hash = Sha256Sm3Hash.hash(transaction.getRawData().toByteArray());
//        SignatureInterface signature = myKey.sign(hash);
//        ByteString bsSign = ByteString.copyFrom(signature.toByteArray());
//        transactionBuilderSigned.addSignature(bsSign);
//        transaction = transactionBuilderSigned.build();
//        return transaction;
//    }
//
//    /**
//     * 报装成transaction
//     *
//     * @param strTransaction
//     * @return
//     */
//    public static Protocol.Transaction packTransaction(String strTransaction) {
//        JSONObject jsonTransaction = JSONObject.parseObject(strTransaction);
//        JSONObject rawData = jsonTransaction.getJSONObject("raw_data");
//        JSONArray contracts = new JSONArray();
//        JSONArray rawContractArray = rawData.getJSONArray("contract");
//        for (int i = 0; i < rawContractArray.size(); i++) {
//            try {
//                JSONObject contract = rawContractArray.getJSONObject(i);
//                JSONObject parameter = contract.getJSONObject("parameter");
//                String contractType = contract.getString("type");
//                Any any = null;
//                switch (contractType) {
//                    case "AccountCreateContract":
//                        AccountContract.AccountCreateContract.Builder accountCreateContractBuilder = AccountContract.AccountCreateContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                accountCreateContractBuilder);
//                        any = Any.pack(accountCreateContractBuilder.build());
//                        break;
//                    case "TransferContract":
//                        BalanceContract.TransferContract.Builder transferContractBuilder = BalanceContract.TransferContract.newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), transferContractBuilder);
//                        any = Any.pack(transferContractBuilder.build());
//                        break;
//                    case "TransferAssetContract":
//                        AssetIssueContractOuterClass.TransferAssetContract.Builder transferAssetContractBuilder = AssetIssueContractOuterClass.TransferAssetContract
//                                .newBuilder()
//                                ;
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                transferAssetContractBuilder);
//                        any = Any.pack(transferAssetContractBuilder.build());
//                        break;
//                    case "VoteAssetContract":
//                        VoteAssetContractOuterClass.VoteAssetContract.Builder voteAssetContractBuilder = VoteAssetContractOuterClass.VoteAssetContract.newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), voteAssetContractBuilder);
//                        any = Any.pack(voteAssetContractBuilder.build());
//                        break;
//                    case "VoteWitnessContract":
//                        WitnessContract.VoteWitnessContract.Builder voteWitnessContractBuilder = WitnessContract.VoteWitnessContract
//                                .newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), voteWitnessContractBuilder);
//                        any = Any.pack(voteWitnessContractBuilder.build());
//                        break;
//                    case "WitnessCreateContract":
//                        WitnessContract.WitnessCreateContract.Builder witnessCreateContractBuilder = WitnessContract.WitnessCreateContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                witnessCreateContractBuilder);
//                        any = Any.pack(witnessCreateContractBuilder.build());
//                        break;
//                    case "AssetIssueContract":
//                        AssetIssueContractOuterClass.AssetIssueContract.Builder assetIssueContractBuilder = AssetIssueContractOuterClass.AssetIssueContract.newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), assetIssueContractBuilder);
//                        any = Any.pack(assetIssueContractBuilder.build());
//                        break;
//                    case "WitnessUpdateContract":
//                        WitnessContract.WitnessUpdateContract.Builder witnessUpdateContractBuilder = WitnessContract.WitnessUpdateContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                witnessUpdateContractBuilder);
//                        any = Any.pack(witnessUpdateContractBuilder.build());
//                        break;
//                    case "ParticipateAssetIssueContract":
//                        AssetIssueContractOuterClass.ParticipateAssetIssueContract.Builder participateAssetIssueContractBuilder =
//                                AssetIssueContractOuterClass.ParticipateAssetIssueContract.newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                participateAssetIssueContractBuilder);
//                        any = Any.pack(participateAssetIssueContractBuilder.build());
//                        break;
//                    case "AccountUpdateContract":
//                        AccountContract.AccountUpdateContract.Builder accountUpdateContractBuilder = AccountContract.AccountUpdateContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                accountUpdateContractBuilder);
//                        any = Any.pack(accountUpdateContractBuilder.build());
//                        break;
//                    case "FreezeBalanceContract":
//                        BalanceContract.FreezeBalanceContract.Builder freezeBalanceContractBuilder = BalanceContract.FreezeBalanceContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                freezeBalanceContractBuilder);
//                        any = Any.pack(freezeBalanceContractBuilder.build());
//                        break;
//                    case "UnfreezeBalanceContract":
//                        BalanceContract.UnfreezeBalanceContract.Builder unfreezeBalanceContractBuilder = BalanceContract.UnfreezeBalanceContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                unfreezeBalanceContractBuilder);
//                        any = Any.pack(unfreezeBalanceContractBuilder.build());
//                        break;
//                    case "UnfreezeAssetContract":
//                        AssetIssueContractOuterClass.UnfreezeAssetContract.Builder unfreezeAssetContractBuilder = AssetIssueContractOuterClass.UnfreezeAssetContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                unfreezeAssetContractBuilder);
//                        any = Any.pack(unfreezeAssetContractBuilder.build());
//                        break;
//                    case "WithdrawBalanceContract":
//                        BalanceContract.WithdrawBalanceContract.Builder withdrawBalanceContractBuilder = BalanceContract.WithdrawBalanceContract
//                                .newBuilder();
//                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
//                                withdrawBalanceContractBuilder);
//                        any = Any.pack(withdrawBalanceContractBuilder.build());
//                        break;
//                    case "UpdateAssetContract":
//                        AssetIssueContractOuterClass.UpdateAssetContract.Builder updateAssetContractBuilder = AssetIssueContractOuterClass.UpdateAssetContract
//                                .newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), updateAssetContractBuilder);
//                        any = Any.pack(updateAssetContractBuilder.build());
//                        break;
//                    case "SmartContract":
//                        SmartContractOuterClass.SmartContract.Builder smartContractBuilder = SmartContractOuterClass.SmartContract.newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(), smartContractBuilder);
//                        any = Any.pack(smartContractBuilder.build());
//                        break;
//                    case "TriggerSmartContract":
//                        SmartContractOuterClass.TriggerSmartContract.Builder triggerSmartContractBuilder = SmartContractOuterClass.TriggerSmartContract
//                                .newBuilder();
//                        JsonFormat
//                                .merge(parameter.getJSONObject("value").toString(),
//                                        triggerSmartContractBuilder);
//                        any = Any.pack(triggerSmartContractBuilder.build());
//                        break;
//                    // todo add other contract
//                    default:
//                }
//                if (any != null) {
//                    String value = Hex.toHexString(any.getValue().toByteArray());
//                    parameter.put("value", value);
//                    contract.put("parameter", parameter);
//                    contracts.add(contract);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                ;
//            }
//        }
//        rawData.put("contract", contracts);
//        jsonTransaction.put("raw_data", rawData);
//        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
//        try {
//            JsonFormat.merge(jsonTransaction.toString(), transactionBuilder);
//            return transactionBuilder.build();
//        } catch (Exception e) {
//            return null;
//        }
//
//    }
//}