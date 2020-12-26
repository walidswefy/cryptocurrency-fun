package com.blockchain.security;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
public abstract class SecurityUtil {
    private SecurityUtil() {
    }

    /**
     * Calculate transactions hash root as the master hash of hash tree
     */
    public static String merkleRoot(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) throw new IllegalArgumentException();
        if (hashes.size() == 1) return hashes.get(0);

        List<String> reducedList = new ArrayList<>();
        for (int i = 0; i < hashes.size(); i = i + 2) {
            if (i == hashes.size() - 1) {
                reducedList.add(hashes.get(i));
            } else {
                reducedList.add(hash((hashes.get(i) + hashes.get(i + 1)).getBytes()));
            }
        }
        // recursively process hash tree levels
        return merkleRoot(reducedList);
    }

    public static String encryptText(String msg, Key key) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public static String decryptText(String msg, Key key) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(msg)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("RSA");
    }

    @SneakyThrows
    public static String hash(byte[] data) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(data);
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }
}
