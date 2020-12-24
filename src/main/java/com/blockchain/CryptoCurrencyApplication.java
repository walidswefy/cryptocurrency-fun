package com.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author walid.sewaify
 * @since 12-Dec-20
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class CryptoCurrencyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CryptoCurrencyApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Application in running state!");
    }
}
