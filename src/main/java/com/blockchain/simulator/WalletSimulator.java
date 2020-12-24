package com.blockchain.simulator;

import com.blockchain.core.Wallet;
import com.blockchain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author walid.sewaify
 * @since 21-Dec-20
 */
@Component
@Profile("!miner")
@RequiredArgsConstructor
@Slf4j
public class WalletSimulator {
    // infinite balance wallet!
    private final Wallet wallet;
    private final Map<String, Long> walletBalance = new HashMap<>();
    private List<Wallet> receivers;

    @PostConstruct
    void initWallets() {
        receivers = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Wallet receiverWallet = new Wallet(null);
            receiverWallet.init();
            receivers.add(receiverWallet);
            walletBalance.put(receiverWallet.getAddress(), 0L);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void simulate() {
        // keeping sending random amount from main wallet to a random receiver wallet
        Random random = new Random();
        int randomReceiver = random.nextInt(receivers.size());
        String receiverAddress = receivers.get(randomReceiver).getAddress();

        long amount = random.nextInt(1000000);
        walletBalance.merge(receiverAddress, amount, Long::sum);
        Transaction transaction = wallet.sendMoney(receiverAddress, amount);
        log.info("After transaction {} -> Receivers balances: {}", transaction.getHash(), walletBalance);
    }
}
