package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.distributed.BlockPublisher;
import com.example.blockchain.bitcoin.distributed.DistributedChain;
import com.example.blockchain.bitcoin.model.Block;
import com.example.blockchain.bitcoin.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.blockchain.bitcoin.security.IntegrityChecker.verifyTransaction;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Component
@RequiredArgsConstructor
@Profile("miner")
@Slf4j
public class Miner {
    private final MinerWallet minerWallet;
    private final BlockPublisher blockPublisher;
    private final DistributedChain blockChain;
    // unconfirmed transactions
    List<Transaction> transactions = new ArrayList<>();
    private Block blockInMining;

    @PostConstruct
    public void mineGenesis() {
        if (blockChain.getBlockChain().isEmpty()) {
            runAsync(() -> doMining("0", transactions));
        }
    }

    public void onTransaction(Transaction transaction) {
        log.info("received transaction: {}", transaction);

        if (verifyTransaction(transaction)) {
            transactions.add(transaction);
            if (blockInMining != null) {
                runAsync(() -> doMining(blockInMining.getPreviousHash(), transactions));
            }
        }
    }

    public void onConfirmedBlock(Block block) {
        log.info("miner {} received block: {}", minerWallet.getAddress(), block);

        Set<String> confirmedTransactions = block.getTransactions().stream().map(Transaction::getSignature)
            .collect(Collectors.toSet());
        transactions = transactions.stream().filter(t -> !confirmedTransactions.contains(t.getSignature()))
            .collect(Collectors.toList());

        runAsync(() -> doMining(block.getHash(), transactions));
    }

    private void doMining(String previousHash, List<Transaction> transactions) {
        log.info("Miner {} started mining for {} transactions", minerWallet.getAddress(), transactions.size());
        List<Transaction> blockTransactions = rebuildTransactions(transactions);
        if (blockInMining != null) {
            blockInMining.stopMining();
        }
        blockInMining = new Block(previousHash, blockTransactions, blockChain.challengeComplexity());
        blockInMining.mineBlock();
        if (blockInMining.challengeSolved()) {
            log.info("Good news! Miner {} solved the challenge for block {} ", minerWallet.getAddress(), blockInMining);
            blockPublisher.publishPotentialBlock(blockInMining);
        } else {
            // retry after shuffling transactions
            doMining(blockInMining.getPreviousHash(), transactions);
        }
    }

    private List<Transaction> rebuildTransactions(List<Transaction> transactions) {
        transactions = transactions.stream().filter(tx -> !tx.isCoinbase()).collect(Collectors.toList());
        Collections.shuffle(transactions);
        Transaction coinTransaction = getCoinBaseTransaction(transactions.size());
        transactions.add(0, coinTransaction);
        return transactions;
    }

    private Transaction getCoinBaseTransaction(int numberOfTransactions) {
        long award = 1000000 + numberOfTransactions * 0;
        return minerWallet.coinBase(award);
    }
}
