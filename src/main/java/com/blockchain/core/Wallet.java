package com.blockchain.core;

import com.blockchain.distributed.TransactionPublisher;
import com.blockchain.model.Transaction;
import com.blockchain.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Wallet {
    private final TransactionPublisher publisher;
    private KeyPair pair;
    private String address;

    @PostConstruct
    public void init() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            this.pair = keyGen.generateKeyPair();
            // address in bitcoin in Base58, for this we use hexadecimal encoding
            this.address = SecurityUtil.getHash(getPublicKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("RSA is not supported!", e);
        }
    }

    /**
     * build transaction, sign it, and publish it to the network
     */
    protected Transaction createTransaction(String sender, String receiver, long amount) {
        long time = System.currentTimeMillis();
        String transactionAsText = String.format("%s:%s:%s:%s", time, amount, sender, receiver);
        String signature = signTransaction(transactionAsText);
        String signatureHash = SecurityUtil.getHash(signature.getBytes());
        return Transaction.builder().sender(sender).receiver(receiver).amount(amount).timestamp(time)
            .senderKey(getPublicKey()).signature(signature).hash(signatureHash).build();
    }

    public Transaction sendMoney(String receiver, long amount) {
        Transaction tx = createTransaction(address, receiver, amount);
        publisher.publish(tx);
        log.info("Sending funds from wallet: {}", tx);
        return tx;
    }

    protected final String signTransaction(String transactionAsText) {
        return SecurityUtil.encryptText(transactionAsText, pair.getPrivate());
    }

    public PublicKey getPublicKey() {
        return pair.getPublic();
    }

    public String getAddress() {
        return address;
    }
}
