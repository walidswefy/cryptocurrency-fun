package com.blockchain.distributed;

import com.blockchain.model.Block;
import com.blockchain.security.IntegrityChecker;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.Callable;

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
        return IntegrityChecker.verifyBlock(lastBlockHash, currentComplexity, block);
    }
}
