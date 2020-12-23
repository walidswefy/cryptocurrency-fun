package com.example.blockchain.bitcoin.simulator;

import com.example.blockchain.bitcoin.security.BlockChain;
import com.example.blockchain.bitcoin.security.Miner;
import com.example.blockchain.bitcoin.security.MinerWallet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author walid.sewaify
 * @since 21-Dec-20
 */
@Component
@Profile("miner")
@RequiredArgsConstructor
public class BlockChainSimulator {
    private final BlockChain blockChain;
    private final Miner miner;

    private final TaskScheduler scheduler;

    @PostConstruct()
    public void simulate() {
//        scheduler.schedule(miner., Instant.now().plus(1, ChronoUnit.MINUTES));
    }
}
