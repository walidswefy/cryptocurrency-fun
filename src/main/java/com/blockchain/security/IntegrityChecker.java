package com.blockchain.security;

import com.blockchain.model.Block;
import com.blockchain.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
public class IntegrityChecker {

    /**
     * Verify transaction data and signature
     */
    public static boolean verifyTransaction(Transaction transaction) {
        try {
            String decryptedTransaction = SecurityUtil.decryptText(transaction.getSignature(), transaction.getSenderKey());
            String[] transactionData = decryptedTransaction.split(":");
            String timestamp = transactionData[0];
            String amount = transactionData[1];
            String sender = transactionData[2];
            String receiver = transactionData[3];
            return Long.toString(transaction.getTimestamp()).equals(timestamp)
                && Long.toString(transaction.getAmount()).equals(amount)
                && receiver.equals(transaction.getReceiver())
                && transaction.getSender().equals(sender);
        } catch (SecurityException e) {
            return false;
        }
    }

    /**
     * Verify block sequence, transactions and challenge
     */
    public static boolean verifyBlock(String lastBlockHash, int complexity, Block block) {
        boolean validSequence = lastBlockHash == null || lastBlockHash.equals(block.getPreviousHash());
        if (!validSequence) {
            return false;
        }

        String prefix = hashPrefix(complexity);
        boolean validHash = calculateBlockHash(block).startsWith(prefix);
        if (!validHash) {
            return false;
        }

        List<Transaction> transactions = block.getTransactions();
        boolean validCoinBaseTransaction = transactions.get(0).isCoinbase() &&
            transactions.stream().filter(Transaction::isCoinbase).count() == 1;
        if (!validCoinBaseTransaction) {
            return false;
        }

        return transactions.stream().allMatch(IntegrityChecker::verifyTransaction);
    }

    public static String calculateBlockHash(Block block) {
        String txs = block.getTransactions().stream().map(Transaction::getHash)
            .collect(Collectors.joining(":"));

        String content = block.getPreviousHash() + ":" +
            block.getTimeStamp() + ":" +
            block.getNonce() + ":" +
            txs;
        return SecurityUtil.getHash(content.getBytes());
    }

    public static String hashPrefix(int complexity) {
        return new String(new char[complexity]).replace('\0', '0');
    }
}
