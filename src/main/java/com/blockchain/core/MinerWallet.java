package com.blockchain.core;

import com.blockchain.distributed.TransactionPublisher;
import com.blockchain.model.Transaction;
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
        // sender assumed empty for coinbase transaction
        return createTransaction("", getAddress(), awardAmount);
    }
}
