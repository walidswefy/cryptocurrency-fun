package com.example.blockchain.bitcoin.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.internal.util.JsonUtil;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Data
@Builder
public class Transaction implements Serializable {
    private boolean coinbase;
    private String sender;
    private String receiver;
    private PublicKey senderKey;
    private long amount;
    private long timestamp;
    private String signature;

    @Override
    public String toString() {
        return "Transaction{" +
            "sender='" + sender + '\'' +
            ", receiver='" + receiver + '\'' +
            ", amount=" + amount +
            ", timestamp=" + timestamp +
            '}';
    }

    @SneakyThrows
    public String asJson() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
