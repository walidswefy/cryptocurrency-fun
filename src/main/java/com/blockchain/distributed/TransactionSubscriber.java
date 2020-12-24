package com.blockchain.distributed;

import com.blockchain.core.Miner;
import com.blockchain.model.Transaction;
import com.blockchain.stats.NetworkPerformance;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author walid.sewaify
 * @since 20-Dec-20
 */
@Component
@AllArgsConstructor
@Profile("miner")
public class TransactionSubscriber {
    private final HazelcastInstance instance;
    private final Miner miner;
    private final NetworkPerformance networkPerformance;


    @PostConstruct
    public void init() {
        ITopic<Transaction> topic = instance.getTopic("transaction");
        topic.addMessageListener(m -> {
            Transaction tx = m.getMessageObject();
            miner.onTransaction(tx);
            networkPerformance.onTransaction(tx);
        });
    }
}
