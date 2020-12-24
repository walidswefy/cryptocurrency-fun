package com.blockchain.stats;

import com.blockchain.distributed.DistributedChain;
import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walid.sewaify
 * @since 22-Dec-20
 * <p>
 * Statistics and measures to network performance
 */
@Component
@Data
@Slf4j
@Profile("miner")
@RequiredArgsConstructor
public class NetworkPerformance {
    private final DistributedChain blockChain;

    private long countOfUnconfirmedTransactions;
    private long countOfConfirmedTransactions;
    private long countOfReceivedTransactions;
    private long totalTimeToConfirmation;
    private long numberOfBlocks;
    private long fastestTimeToConfirmation = Long.MAX_VALUE;
    private long slowestTimeToConfirmation = Long.MIN_VALUE;
    private long averageTimeOfConfirmation;
    private Map<String, Long> walletBalances;
    private Map<String, Long> minerBlockCount;

    public void onCompletedBlock(Block block) {
        log.info("refresh statistics after receiving block {}", block.getHash());
        List<Block> blockChain = this.blockChain.getBlockChain();
        numberOfBlocks = blockChain.size();
        countOfConfirmedTransactions = 0;
        totalTimeToConfirmation = 0;
        walletBalances = new HashMap<>();
        minerBlockCount = new HashMap<>();
        for (Block b : blockChain) {
            for (Transaction tx : b.getTransactions()) {
                countOfConfirmedTransactions++;
                long confirmationTime = b.getTimeStamp() - tx.getTimestamp();
                totalTimeToConfirmation += confirmationTime;
                fastestTimeToConfirmation = Math.min(fastestTimeToConfirmation, confirmationTime);
                slowestTimeToConfirmation = Math.max(slowestTimeToConfirmation, confirmationTime);
                walletBalances.merge(tx.getReceiver(), tx.getAmount(), Long::sum);
                if (tx.isCoinbase()) {
                    // coinbase transaction
                    minerBlockCount.merge(tx.getReceiver(), 1L, Long::sum);
                }
            }
        }
        averageTimeOfConfirmation = totalTimeToConfirmation / countOfConfirmedTransactions;
    }

    public void onTransaction(Transaction transaction) {
        countOfReceivedTransactions++;
    }

    public long getCountOfUnconfirmedTransactions() {
        return countOfReceivedTransactions - countOfConfirmedTransactions + numberOfBlocks;
    }

    @Scheduled(fixedDelay = 5000)
    public void reportPerformance() {
        log.info(this.toString());
        List<Block> blocks = blockChain.getBlockChain();
        blocks.stream().map(Block::toString).forEach(log::info);
    }
}
