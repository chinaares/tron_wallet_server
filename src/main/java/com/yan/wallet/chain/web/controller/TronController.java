package com.yan.wallet.chain.web.controller;

import com.yan.wallet.chain.tron.TronUtils;
import com.yan.wallet.chain.tron.bean.Trc10TransferParam;
import com.yan.wallet.chain.tron.bean.Trc20TransferParam;
import com.yan.wallet.chain.tron.bean.TrxTransferParam;
import com.yan.wallet.chain.utils.YanObjectUtils;
import com.yan.wallet.chain.web.param.BroadCastTransactionParam;
import com.yan.wallet.chain.web.param.CreateTransactionParam;
import com.yan.wallet.chain.web.param.SignTransactionParam;
import com.yan.wallet.chain.web.view.BaseResult;
import com.yan.wallet.chain.web.view.CreateTxView;
import com.yan.wallet.chain.web.view.TrxKeyView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tron/")
@Slf4j
@Validated
public class TronController {

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private TronUtils tronUtils;

    @RequestMapping(value = "createTransaction", method = RequestMethod.POST)
    public BaseResult<CreateTxView> create(@RequestBody @Validated CreateTransactionParam param) {
        if (YanObjectUtils.notEmpty(param.getTokenAddress())) {
            return tronUtils.createTrc20Transfer(Trc20TransferParam.builder()
                    .amount(param.getAmount())
                    .fromAddress(param.getFrom())
                    .toAddress(param.getTo())
                    .tokenAddress(param.getTokenAddress())
                    .memo(param.getMemo())
                    .build());
        }

        if (YanObjectUtils.notEmpty(param.getAssetName())) {
            return tronUtils.createTrc10Transfer(Trc10TransferParam.builder()
                    .amount(param.getAmount())
                    .fromAddress(param.getFrom())
                    .toAddress(param.getTo())
                    .assetName(param.getAssetName())
                    .memo(param.getMemo())
                    .build());
        }

        return tronUtils.createTrxTransfer(TrxTransferParam.builder()
                .amount(param.getAmount())
                .fromAddress(param.getFrom())
                .toAddress(param.getTo())
                .memo(param.getMemo())
                .build());
    }

    @RequestMapping(value = "broadCast", method = RequestMethod.POST)
    public BaseResult broadCast(@RequestBody @Validated BroadCastTransactionParam param) {
        return tronUtils.broadCastTx(param.getSignStr());
    }

    @RequestMapping(value = "signTx", method = RequestMethod.POST)
    public BaseResult signTx(@RequestBody @Validated SignTransactionParam param) {
        return tronUtils.signTxWithKey(param.getPriKey(), param.getUnSignTx());
    }

    @RequestMapping(value = "genTrxKey", method = RequestMethod.GET)
    public BaseResult<TrxKeyView> genTrxKey() {
        return TronUtils.genTrxKey();
    }

}
