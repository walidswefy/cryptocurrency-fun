package com.blockchain.distributed;

import com.blockchain.model.Block;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author walid.sewaify
 * @since 23-Dec-20
 */
@Component
@Slf4j
@Profile("miner")
@RequiredArgsConstructor
public class BlockVoting {
    private final HazelcastInstance instance;

    /**
     * Master node will ask all network members to verify the block and decide based on the majority of votes
     */
    @SneakyThrows
    public boolean majorityVotes(String lastBlockHash, int complexity, Block block) {
        log.info("checking if block is accepted by majority of workers: {}", block.getHash());
        IExecutorService executor = instance.getExecutorService("blockVerificationExecution");
        Map<Member, Future<Boolean>> result =
            executor.submitToAllMembers(new BlockVerifier(lastBlockHash, complexity, block));
        int votes = 0;
        for (Future<Boolean> future : result.values()) {
            if (future.get()) {
                votes++;
            } else {
                votes--;
            }
        }
        log.info("Network receives {} votes to add block {}", votes, block.getComplexity());
        return votes > 0;
    }

}
