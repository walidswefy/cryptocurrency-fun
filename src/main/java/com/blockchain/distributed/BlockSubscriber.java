package com.blockchain.distributed;

import com.blockchain.model.Block;
import com.blockchain.core.Miner;
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
public class BlockSubscriber {
    private final HazelcastInstance instance;
    private final Miner miner;
    private final DistributedChain blockChain;
    private final NetworkPerformance networkPerformance;

    @PostConstruct
    public void init() {
        ITopic<Block> confirmedBlockTopic = instance.getTopic("confirmedBlock");
        confirmedBlockTopic.addMessageListener(m -> {
            miner.onConfirmedBlock(m.getMessageObject());
            networkPerformance.onCompletedBlock(m.getMessageObject());
        });

        ITopic<Block> potentialBlockTopic = instance.getTopic("potentialBlock");
        potentialBlockTopic.addMessageListener(m -> blockChain.onPotentialBlock(m.getMessageObject()));
    }

}
