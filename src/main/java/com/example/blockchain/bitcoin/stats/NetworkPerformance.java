package com.example.blockchain.bitcoin.stats;

import com.example.blockchain.bitcoin.distributed.DistributedChain;
import com.example.blockchain.bitcoin.model.Block;
import com.example.blockchain.bitcoin.model.Transaction;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author walid.sewaify
 * @since 22-Dec-20
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

    public void onCompletedBlock(Block block) {
        numberOfBlocks++;
        // block transactions - coinbase transaction
        countOfConfirmedTransactions += block.getTransactions().size();
        for (Transaction tx : block.getTransactions()) {
            long confirmationTime = block.getTimeStamp() - tx.getTimestamp();
            this.totalTimeToConfirmation += confirmationTime;
            fastestTimeToConfirmation = Math.min(fastestTimeToConfirmation, confirmationTime);
            slowestTimeToConfirmation = Math.max(slowestTimeToConfirmation, confirmationTime);
            averageTimeOfConfirmation = totalTimeToConfirmation / countOfConfirmedTransactions;
        }
        countOfUnconfirmedTransactions = countOfReceivedTransactions - countOfConfirmedTransactions + numberOfBlocks - 1;
        log.info(this.toString());
    }

    public void onTransaction(Transaction transaction) {
        // todo validation
        countOfReceivedTransactions++;
        countOfUnconfirmedTransactions = countOfReceivedTransactions - countOfConfirmedTransactions + numberOfBlocks - 1;
    }

    @Scheduled(fixedDelay = 5000)
    public void reportPerformance() {
        log.info(this.toString());
        List<Block> blocks = blockChain.getBlockChain();
        blocks.stream().map(Block::toString).forEach(log::info);
//        Map<Long, String> collect = blocks.stream().flatMap(b -> b.getTransactions().stream())
//            .collect(Collectors.toMap(Transaction::getTimestamp, tx -> tx.getReceiver() + ":" + tx.getAmount()));
//        Map<Long, String> txMap = new TreeMap<>(collect);
//        log.info("chain has {} block with {}", blocks.size(), txMap);

        log.info("balances in chain {}", walletBlanaces());
        log.info("miner blocks {}", minerBlockCount());
    }

    private Map<String, Long> walletBlanaces() {
        Map<String, List<Transaction>> receiverTransactions = blockChain.getBlockChain().stream().flatMap(block -> block.getTransactions().stream())
            .collect(Collectors.groupingBy(Transaction::getReceiver));
        Map<String, Long> balances = new HashMap<>();
        for (String address : receiverTransactions.keySet()) {
            balances.put(address, receiverTransactions.get(address).stream().map(Transaction::getAmount).reduce(Long::sum).get());
        }

        return balances;
    }

    private Map<String, Long> minerBlockCount() {
        Map<String, Long> result = new HashMap<>();
        for (Block block : blockChain.getBlockChain()) {
            if (block.getTransactions().isEmpty()) {
                continue;
            }
            Transaction firstTransaction = block.getTransactions().get(0);
            String minerAddress = firstTransaction.getReceiver();
            result.putIfAbsent(minerAddress, 0L);
            result.merge(minerAddress, 1L, Long::sum);
        }
        return result;
    }
}
