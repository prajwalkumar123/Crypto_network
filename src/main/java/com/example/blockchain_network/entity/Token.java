package com.example.blockchain_network.entity;



import java.time.LocalDateTime;

public class Token {
    private Long id;
    private Long userId;
    private int nodeNumber;
    private String tokenText;
    private String encryptedText;
    private String encryptionKey;
    private String encryptionAlgorithm;
    private String status; // PENDING, TRANSFERRED, RECEIVED
    private LocalDateTime createdAt;

    // Default constructor
    public Token() {}

    // Constructor with parameters
    public Token(Long userId, int nodeNumber, String tokenText) {
        this.userId = userId;
        this.nodeNumber = nodeNumber;
        this.tokenText = tokenText;
        this.status = "PENDING";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public String getTokenText() {
        return tokenText;
    }

    public void setTokenText(String tokenText) {
        this.tokenText = tokenText;
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", userId=" + userId +
                ", nodeNumber=" + nodeNumber +
                ", tokenText='" + tokenText + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}