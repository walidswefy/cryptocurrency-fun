package com.example.blockchain.bitcoin.distributed;

import com.example.blockchain.bitcoin.model.Block;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.Callable;

import static com.example.blockchain.bitcoin.security.IntegrityChecker.verifyBlock;

/**
 * @author walid.sewaify
 * @since 23-Dec-20
 */
@RequiredArgsConstructor
public class BlockVerifier implements Callable<Boolean>, Serializable {
    private final String lastBlockHash;
    private final int currentComplexity;
    private final Block block;

    @Override
    public Boolean call() throws Exception {
        return verifyBlock(lastBlockHash, currentComplexity, block);
    }
}
