package com.example.blockchain.bitcoin.security;

import com.example.blockchain.bitcoin.model.Block;
import com.example.blockchain.bitcoin.model.Transaction;

import java.util.List;

/**
 * @author walid.sewaify
 * @since 19-Dec-20
 */
public class IntegrityChecker {

    public static boolean verifyTransaction(Transaction transaction) {
        try {
            String decryptedTransaction = HashUtil.decryptText(transaction.getSignature(), transaction.getSenderKey());
            String[] transactionData = decryptedTransaction.split(":");
            String timestamp = transactionData[0];
            String amount = transactionData[1];
            String sender = transaction.isCoinbase() ? null : transactionData[2];
            String receiver = transaction.isCoinbase() ? transactionData[2] : transactionData[3];
            boolean validData = Long.toString(transaction.getTimestamp()).equals(timestamp)
                && Long.toString(transaction.getAmount()).equals(amount)
                && receiver.equals(transaction.getReceiver());
            if (transaction.isCoinbase()) {
                return validData;
            }
            return validData && transaction.getSender().equals(sender);
        } catch (SecurityException e) {
            return false;
        }
    }

    public static boolean verifyBlock(String lastBlockHash, int complexity, Block block) {
        boolean validSequence = lastBlockHash == null || lastBlockHash.equals(block.getPreviousHash());
        if (!validSequence) {
            return false;
        }

        String prefix = new String(new char[complexity]).replace('\0', '0');
        boolean validHash = calculateBlockHash(block).startsWith(prefix);
        if (!validHash) {
            return false;
        }

        List<Transaction> transactions = block.getTransactions();
        if (lastBlockHash == null) {
            // genesis block
            return transactions.size() == 0;
        }

        boolean validCoinBaseTransaction = transactions.get(0).isCoinbase() &&
            transactions.stream().filter(Transaction::isCoinbase).count() == 1;
        if (!validCoinBaseTransaction) {
            return false;
        }

        return transactions.stream().allMatch(IntegrityChecker::verifyTransaction);
    }

    public static String calculateBlockHash(Block block) {
        String dataToHash = block.getPreviousHash() + block.getTimeStamp() + block.getNonce() + block.getTransactions();
        return HashUtil.getHash(dataToHash.getBytes());
    }
}
