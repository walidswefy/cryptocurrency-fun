package com.example.blockchain.bitcoin.pubsub;

import com.example.blockchain.bitcoin.model.Block;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author walid.sewaify
 * @since 20-Dec-20
 */
@Component
@AllArgsConstructor
@Profile("miner")
public class BlockPublisher {
    private final HazelcastInstance instance;

    public void publishConfirmedBlock(Block t) {
        ITopic<Block> topic = instance.getTopic("confirmedBlock");
        topic.publish(t);
    }

    public void publishPotentialBlock(Block t) {
        ITopic<Block> topic = instance.getTopic("potentialBlock");
        topic.publish(t);
    }
}
