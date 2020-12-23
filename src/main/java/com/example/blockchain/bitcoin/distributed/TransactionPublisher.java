package com.example.blockchain.bitcoin.distributed;

import com.example.blockchain.bitcoin.model.Transaction;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author walid.sewaify
 * @since 20-Dec-20
 */
@Component
@AllArgsConstructor
public class TransactionPublisher {
    private final HazelcastInstance instance;

    public void publish(Transaction t) {
        ITopic<Transaction> topic = instance.getTopic("transaction");
        topic.publish(t);
    }
}
