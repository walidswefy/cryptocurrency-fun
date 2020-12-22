package com.example.blockchain.bitcoin.simulator;

import com.example.blockchain.bitcoin.security.Wallet;
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
        Random random = new Random();
        int randomReceiver = random.nextInt(receivers.size());
        String receiverAddress = receivers.get(randomReceiver).getAddress();

        long amount = random.nextInt(1000000);
        walletBalance.merge(receiverAddress, amount, Long::sum);
        wallet.sendMoney(receiverAddress, amount);
        log.info("balances: {}", walletBalance);
    }
}
