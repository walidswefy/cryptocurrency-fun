package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.model.Transaction;
import com.example.blockchain.bitcoin.pubsub.TransactionPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import static com.example.blockchain.bitcoin.security.HashUtil.encryptText;
import static com.example.blockchain.bitcoin.security.HashUtil.getHash;

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
            this.address = getHash(getPublicKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("RSA is not supported!", e);
        }
    }

    public Transaction sendMoney(String receiver, long amount) {
        long time = System.currentTimeMillis();
        String transactionAsText = String.format("%s:%s:%s:%s", time, amount, address, receiver);
        String signature = signTransaction(transactionAsText);
        Transaction tx = Transaction.builder().sender(address).receiver(receiver).amount(amount).timestamp(time)
            .senderKey(getPublicKey()).signature(signature).build();
        publisher.publish(tx);
        log.info("Sending transaction: {}", tx);
        return tx;
    }

    protected final String signTransaction(String transactionAsText) {
        return encryptText(transactionAsText, pair.getPrivate());
    }


    public PublicKey getPublicKey() {
        return pair.getPublic();
    }

    public String getAddress() {
        return address;
    }
}
