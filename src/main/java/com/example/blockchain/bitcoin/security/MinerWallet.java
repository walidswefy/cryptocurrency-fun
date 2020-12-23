package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.model.Transaction;
import com.example.blockchain.bitcoin.distributed.TransactionPublisher;
import org.springframework.stereotype.Component;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Component
public class MinerWallet extends Wallet {
    public MinerWallet(TransactionPublisher publisher) {
        super(publisher);
    }

    public Transaction coinBase(long awardAmount) {
        long time = System.currentTimeMillis();
        String transactionAsText = String.format("%s:%s:%s", time, awardAmount, getAddress());
        String signature = signTransaction(transactionAsText);
        return Transaction.builder().receiver(getAddress()).amount(awardAmount).timestamp(time).coinbase(true)
            .senderKey(getPublicKey()).signature(signature).build();
    }
}
