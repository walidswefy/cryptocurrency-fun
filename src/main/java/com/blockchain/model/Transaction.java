package com.blockchain.model;

import lombok.Builder;
import lombok.Data;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Data
@Builder
public class Transaction implements Serializable {
    private final boolean coinbase;
    private String sender;
    private String receiver;
    private PublicKey senderKey;
    private long amount;
    private long timestamp;
    private String signature;
    private String hash;

    public boolean isCoinbase() {
        return StringUtils.isEmpty(sender);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "sender='" + sender + '\'' +
            ", receiver='" + receiver + '\'' +
            ", amount=" + amount +
            ", hash=" + hash +
            ", timestamp=" + timestamp +
            '}';
    }
}
