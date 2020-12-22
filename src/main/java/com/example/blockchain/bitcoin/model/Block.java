package com.example.blockchain.bitcoin.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.List;

import static com.example.blockchain.bitcoin.security.IntegrityChecker.calculateBlockHash;

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
    private long timeStamp;
    private String hash;
    private int nonce;
    private boolean startMining;

    public Block(String previousHash, List<Transaction> transactions, int complexity) {
        this.prefixString = new String(new char[complexity]).replace('\0', '0');
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.complexity = complexity;
    }

    public void mineBlock() {
        startMining = true;
        timeStamp = System.currentTimeMillis();
        hash = calculateBlockHash(this);
        while (startMining && !challengeSolved()) {
            timeStamp = System.currentTimeMillis();
            nonce++;
            hash = calculateBlockHash(this);
        }
    }

    public void stopMining() {
        startMining = false;
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

    @SneakyThrows
    public String asJson() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
