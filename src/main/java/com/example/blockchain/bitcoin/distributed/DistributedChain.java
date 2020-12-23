package com.example.blockchain.bitcoin.distributed;

import com.example.blockchain.bitcoin.model.Block;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author walid.sewaify
 * @since 23-Dec-20
 */
@Component
@Slf4j
@Profile("miner")
@RequiredArgsConstructor
public class DistributedChain {
    private final HazelcastInstance instance;
    private final BlockPublisher blockPublisher;
    private final BlockVoting voting;

    public List<Block> getBlockChain() {
        return instance.getList("blockchain");
    }

    public Optional<Block> lastBlock() {
        List<Block> blockChain = getBlockChain();
        return blockChain.isEmpty() ? Optional.empty() : Optional.of(blockChain.get(blockChain.size() - 1));
    }

    public int challengeComplexity() {
        // should be based on latest blocks mining effort
        return 4;
    }

    @SneakyThrows
    public void onPotentialBlock(Block block) {
        log.info("received potential block: {}", block.getHash());
        String lastBlockHash = lastBlock().isEmpty() ? null : lastBlock().get().getHash();
        if (isMasterNode() && voting.majorityVotes(lastBlockHash, challengeComplexity(), block)) {
            getBlockChain().add(block);
            blockPublisher.publishConfirmedBlock(block);
        }
    }


    public boolean isMasterNode() {
        return instance.getCluster().getMembers().iterator().next().localMember();
    }
}
