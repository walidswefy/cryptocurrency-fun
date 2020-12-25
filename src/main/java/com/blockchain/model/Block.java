package com.blockchain.model;

import com.blockchain.security.IntegrityChecker;
import com.blockchain.security.SecurityUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.blockchain.security.IntegrityChecker.hashPrefix;
import static com.blockchain.security.SecurityUtil.merkleRoot;

/**
 * @author walid.sewaify
 * @since 01-Dec-20
 */
@Data
public class Block implements Serializable {
    private final String prefixString;
    private final String previousHash;
    private final int complexity;
    private final List<Transaction> transactions;
    private final String merkleRoot;
    private long timeStamp;
    private String hash;
    private int nonce;
    private boolean doMining;

    public Block(String previousHash, List<Transaction> transactions, int complexity) {
        this.prefixString = hashPrefix(complexity);
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.merkleRoot = merkleRoot(transactions.stream().map(Transaction::getHash).collect(Collectors.toList()));
        this.complexity = complexity;
    }

    public void mineBlock() {
        doMining = true;
        timeStamp = System.currentTimeMillis();
        hash = IntegrityChecker.calculateBlockHash(this);
        while (doMining && !challengeSolved()) {
            timeStamp = System.currentTimeMillis();
            nonce++;
            hash = IntegrityChecker.calculateBlockHash(this);
        }
    }

    public void stopMining() {
        doMining = false;
    }

    public boolean challengeSolved() {
        return prefixString.equals(hash.substring(0, complexity));
    }

    @Override
    public String toString() {
        return "Block{" +
            "previousHash='" + previousHash + '\'' +
            ", hash='" + hash + '\'' +
            ", transactions=" + transactions +
            '}';
    }
}
