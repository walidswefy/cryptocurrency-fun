package com.example.blockchain.bitcoin.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walid.sewaify
 * @since 29-Nov-20
 */
@Configuration
public class HazelCastConfig {
    @Bean
    public Config hzConfig() {
        MapConfig mapConfig = new MapConfig().setName("noExpiryCache").setTimeToLiveSeconds(-1);
        Config config = new Config();
        config.setInstanceName("cacheInstance").addMapConfig(mapConfig);
        return config;
    }
}
