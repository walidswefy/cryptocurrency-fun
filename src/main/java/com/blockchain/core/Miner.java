package com.blockchain.core;

import com.blockchain.distributed.BlockPublisher;
import com.blockchain.distributed.DistributedChain;
import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
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

import static com.blockchain.security.IntegrityChecker.verifyTransaction;
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
    private static final int BLOCK_REWARD = 1000000;
    private static final int TX_REWARD = 1000;

    private final MinerWallet minerWallet;
    private final BlockPublisher blockPublisher;
    private final DistributedChain blockChain;
    // unconfirmed transactions
    List<Transaction> transactions = new ArrayList<>();
    // block being solved
    private Block blockInMining;

    @PostConstruct
    public void mineGenesis() {
        // add genesis block in chain is empty (historical moment!)
        if (blockChain.getBlockChain().isEmpty()) {
            runAsync(() -> doMining("00000"));
        }
    }

    /**
     * verify the transaction, add it to unconfirmed list and reset the mining
     */
    public void onTransaction(Transaction transaction) {
        log.info("received transaction: {}", transaction);

        if (verifyTransaction(transaction)) {
            transactions.add(transaction);
            if (blockInMining != null) {
                runAsync(() -> doMining(blockInMining.getPreviousHash()));
            }
        }
    }

    /**
     * reset the mining incase a new block is added to the chain
     */
    public void onConfirmedBlock(Block block) {
        log.info("miner {} received block: {}", minerWallet.getAddress(), block);

        Set<String> confirmedTransactions = block.getTransactions().stream().map(Transaction::getSignature)
            .collect(Collectors.toSet());
        transactions = transactions.stream().filter(t -> !confirmedTransactions.contains(t.getSignature()))
            .collect(Collectors.toList());

        runAsync(() -> doMining(block.getHash()));
    }

    /**
     * Try to solve the challenge, and in case of failure restructure the transaction and try again
     */
    private void doMining(String previousHash) {
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
            doMining(blockInMining.getPreviousHash());
        }
    }

    /**
     * shuffle transactions and put a new coinbase transaction in the head of the list
     */
    private List<Transaction> rebuildTransactions(List<Transaction> transactions) {
        transactions = transactions.stream().filter(tx -> !tx.isCoinbase()).collect(Collectors.toList());
        Collections.shuffle(transactions);
        Transaction coinTransaction = getCoinBaseTransaction(transactions.size());
        transactions.add(0, coinTransaction);
        return transactions;
    }

    /**
     * calculate block award and sign the transaction using the miner wallet
     */
    private Transaction getCoinBaseTransaction(int numberOfTransactions) {
        long award = BLOCK_REWARD + numberOfTransactions * TX_REWARD;
        return minerWallet.coinBase(award);
    }
}
