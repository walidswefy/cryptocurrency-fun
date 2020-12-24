package com.blockchain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author walid.sewaify
 * @since 18-Dec-20
 */
public class Satoshi implements Serializable {
    private static final BigDecimal BITCOIN = BigDecimal.valueOf(100000000);

    private final long value;

    public Satoshi(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public BigDecimal units() {
        return BigDecimal.valueOf(value).divide(BITCOIN, MathContext.DECIMAL32);
    }

}
