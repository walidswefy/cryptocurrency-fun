package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.distributed.BlockPublisher;
import com.example.blockchain.bitcoin.distributed.BlockVoting;
import com.example.blockchain.bitcoin.distributed.DistributedChain;
import com.example.blockchain.bitcoin.model.Block;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    private final BlockPublisher blockPublisher;
    private final BlockVoting blockVoting;
    private final DistributedChain distributedChain;
    private int currentComplexity = 3;

    //    @PostConstruct
    public void addGenesisBlock() {
        log.info("adding genesis block");
        if (distributedChain.getBlockChain().isEmpty()) {
            String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
            Block genesis = new Block(previousHash, new ArrayList<>(), 4);
            genesis.mineBlock();
            if (genesis.challengeSolved()) {
                blockPublisher.publishPotentialBlock(genesis);
            }
        }
    }
//
//    @SneakyThrows
//    public void onPotentialBlock(Block block) {
//        log.info("received potential block: {}", block.getHash());
//
//        boolean masterNode = instance.getCluster().getMembers().iterator().next().localMember();
//        if (masterNode) {
//            List<Block> chain = distributedChain.getBlockChain();
//                chain.add(block);
//                blockPublisher.publishConfirmedBlock(block);
//            }
//        }
//    }


}
