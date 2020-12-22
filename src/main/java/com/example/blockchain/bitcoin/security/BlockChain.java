package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.model.Block;
import com.example.blockchain.bitcoin.pubsub.BlockPublisher;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Component
@RequiredArgsConstructor
@Profile("miner")
@Slf4j
public class BlockChain {
    private final HazelcastInstance instance;
    //    private final List<Block> chain = new ArrayList<>();
    private final BlockPublisher blockPublisher;
    private int currentComplexity = 3;

    //    @PostConstruct
    public void addGenesisBlock() {
        log.info("adding genesis block");
        if (getBlockChain().isEmpty()) {
            String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
            Block genesis = new Block(previousHash, new ArrayList<>(), 4);
            genesis.mineBlock();
            if (genesis.challengeSolved()) {
                blockPublisher.publishPotentialBlock(genesis);
            }
        }
    }

    @SneakyThrows
    public void onPotentialBlock(Block block) {
        log.info("received potential block: {}", block);

        boolean masterNode = instance.getCluster().getMembers().iterator().next().localMember();
        if (masterNode) {
            List<Block> chain = getBlockChain();
            String lastBlockHash = chain.isEmpty() ? null : chain.get(chain.size() - 1).getHash();

            IExecutorService executor = instance.getExecutorService("blockVerificationExecution");
            Map<Member, Future<Boolean>> result =
                executor.submitToAllMembers(new BlockVerifier(lastBlockHash, currentComplexity, block));
            int votes = 0;
            for (Future<Boolean> future : result.values()) {
                if (future.get()) {
                    votes++;
                } else {
                    votes--;
                }
            }

            log.info("we get {} votes to add block {}", votes, block);

            if (votes > 0) {
                chain.add(block);
                blockPublisher.publishConfirmedBlock(block);
            }
        }
    }

    public void onConfirmedBlock(Block block) {
//        FencedLock newblock = instance.getCPSubsystem().getLock("newblock");
//        newblock.lock();
//        getBlocks().add(block);
//        newblock.lock();
//        log.info("Block added to chain");
    }

    public List<Block> getBlockChain() {
        return instance.getList("blockchain");
    }
}
