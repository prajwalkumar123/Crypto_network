package com.example.blockchain_network.entity;



import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private int nodeNumber;
    private String encryptionKey;
    private int sourceNode;
    private int destinationNode;
    private Long tokenId;
    private String encryptedData;
    private String encryptionAlgorithm;
    private String status; // PENDING, COMPLETED, FAILED
    private LocalDateTime timestamp;

    // Default constructor
    public Transaction() {}

    // Constructor with parameters
    public Transaction(String transactionId, int nodeNumber, String encryptionKey, int sourceNode, int destinationNode, Long tokenId) {
        this.transactionId = transactionId;
        this.nodeNumber = nodeNumber;
        this.encryptionKey = encryptionKey;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.tokenId = tokenId;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public int getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(int sourceNode) {
        this.sourceNode = sourceNode;
    }

    public int getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(int destinationNode) {
        this.destinationNode = destinationNode;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", nodeNumber=" + nodeNumber +
                ", sourceNode=" + sourceNode +
                ", destinationNode=" + destinationNode +
                ", status='" + status + '\'' +
                '}';
    }
}