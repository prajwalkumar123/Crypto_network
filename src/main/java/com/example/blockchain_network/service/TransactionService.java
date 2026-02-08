package com.example.blockchain_network.service;


import com.example.blockchain_network.entity.Transaction;
import com.example.blockchain_network.entity.Token;
import com.example.blockchain_network.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TokenService tokenService;

    public Transaction createTransaction(int sourceNode, int destinationNode, Long tokenId, String encryptionKey) {
        // Generate unique transaction ID
        String transactionId = generateTransactionId();
        
        // Get token details
        Optional<Token> tokenOpt = tokenService.findById(tokenId);
        if (!tokenOpt.isPresent()) {
            throw new RuntimeException("Token not found");
        }
        
        Token token = tokenOpt.get();
        
        // Create transaction
        Transaction transaction = new Transaction(
            transactionId,
            sourceNode,
            encryptionKey,
            sourceNode,
            destinationNode,
            tokenId
        );
        
        transaction.setEncryptedData(token.getEncryptedText());
        transaction.setEncryptionAlgorithm(token.getEncryptionAlgorithm());
        transaction.setStatus("PENDING");
        
        return transactionRepository.save(transaction);
    }

    public Transaction completeTransaction(String transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus("COMPLETED");
            
            // Update token status and node
            if (transaction.getTokenId() != null) {
                tokenService.transferToken(transaction.getTokenId(), transaction.getDestinationNode());
            }
            
            return transactionRepository.save(transaction);
        }
        throw new RuntimeException("Transaction not found");
    }

    public Transaction failTransaction(String transactionId, String reason) {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus("FAILED");
            return transactionRepository.save(transaction);
        }
        throw new RuntimeException("Transaction not found");
    }

    public Optional<Transaction> findByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findTransactionsByNode(int nodeNumber) {
        return transactionRepository.findByNodeNumber(nodeNumber);
    }

    public List<Transaction> findTransactionsBySourceNode(int sourceNode) {
        return transactionRepository.findBySourceNode(sourceNode);
    }

    public List<Transaction> findTransactionsByDestinationNode(int destinationNode) {
        return transactionRepository.findByDestinationNode(destinationNode);
    }

    public List<Transaction> findTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    public List<Transaction> findTransactionsForNode(int nodeNumber) {
        // Find all transactions where the node is either source or destination
        return transactionRepository.findBySourceOrDestinationNode(nodeNumber);
    }

    public List<Transaction> findTransactionsByToken(Long tokenId) {
        return transactionRepository.findByTokenId(tokenId);
    }

    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus("PENDING");
    }

    public List<Transaction> getCompletedTransactions() {
        return transactionRepository.findByStatus("COMPLETED");
    }

    public List<Transaction> getFailedTransactions() {
        return transactionRepository.findByStatus("FAILED");
    }

    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    public long getTransactionCountByStatus(String status) {
        return transactionRepository.countByStatus(status);
    }

    public long getTransactionCountByNode(int nodeNumber) {
        return transactionRepository.countByNodeNumber(nodeNumber);
    }

    public void deleteTransaction(String transactionId) {
        transactionRepository.deleteByTransactionId(transactionId);
    }

    public Transaction processTransfer(int sourceNode, int destinationNode, Long tokenId, String encryptionKey) {
        try {
            // Encrypt and prepare token
            Token encryptedToken = tokenService.encryptAndPrepareForTransfer(tokenId, encryptionKey);
            
            // Create transaction record
            Transaction transaction = createTransaction(sourceNode, destinationNode, tokenId, encryptionKey);
            
            // Complete the transaction
            return completeTransaction(transaction.getTransactionId());
            
        } catch (Exception e) {
            // Create transaction and mark as failed
            Transaction transaction = createTransaction(sourceNode, destinationNode, tokenId, encryptionKey);
            return failTransaction(transaction.getTransactionId(), e.getMessage());
        }
    }

    public boolean verifyTransactionKey(String transactionId, String key) {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            return transaction.getEncryptionKey().equals(key);
        }
        return false;
    }

    public String decryptTransactionData(String transactionId, String key) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            
            if (!transaction.getEncryptionKey().equals(key)) {
                throw new Exception("Invalid decryption key");
            }
            
            if (transaction.getTokenId() != null) {
                return tokenService.decryptToken(transaction.getTokenId(), key);
            }
        }
        throw new Exception("Transaction not found or cannot be decrypted");
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    public boolean isValidTransaction(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId).isPresent();
    }

    public Transaction updateTransactionStatus(String transactionId, String newStatus) {
        Optional<Transaction> transactionOpt = transactionRepository.findByTransactionId(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus(newStatus);
            return transactionRepository.save(transaction);
        }
        throw new RuntimeException("Transaction not found");
    }
}