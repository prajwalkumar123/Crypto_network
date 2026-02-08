package com.example.blockchain_network.service;


import com.example.blockchain_network.entity.Token;
import com.example.blockchain_network.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EncryptionService encryptionService;

    public Token createToken(Long userId, int nodeNumber, String tokenText) {
        Token token = new Token(userId, nodeNumber, tokenText);
        token.setStatus("PENDING");
        return tokenRepository.save(token);
    }

    public Token encryptAndPrepareForTransfer(Long tokenId, String key) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            
            // Get random encryption algorithm
            String algorithm = encryptionService.getRandomAlgorithm();
            
            // Encrypt the token text
            String encryptedText = encryptionService.encrypt(token.getTokenText(), key, algorithm);
            
            // Update token with encryption details
            token.setEncryptedText(encryptedText);
            token.setEncryptionKey(key);
            token.setEncryptionAlgorithm(algorithm);
            token.setStatus("ENCRYPTED");
            
            return tokenRepository.save(token);
        }
        throw new RuntimeException("Token not found");
    }

    public String decryptToken(Long tokenId, String key) throws Exception {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            
            if (token.getEncryptedText() == null || token.getEncryptionAlgorithm() == null) {
                throw new Exception("Token is not encrypted");
            }
            
            if (!key.equals(token.getEncryptionKey())) {
                throw new Exception("Invalid decryption key");
            }
            
            // Decrypt the text
            String decryptedText = encryptionService.decrypt(
                token.getEncryptedText(), 
                key, 
                token.getEncryptionAlgorithm()
            );
            
            // Update token status
            token.setStatus("DECRYPTED");
            tokenRepository.save(token);
            
            return decryptedText;
        }
        throw new Exception("Token not found");
    }

    public Token transferToken(Long tokenId, int destinationNode) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setNodeNumber(destinationNode);
            token.setStatus("TRANSFERRED");
            return tokenRepository.save(token);
        }
        throw new RuntimeException("Token not found");
    }

    public Token receiveToken(Long tokenId) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setStatus("RECEIVED");
            return tokenRepository.save(token);
        }
        throw new RuntimeException("Token not found");
    }

    public Optional<Token> findById(Long id) {
        return tokenRepository.findById(id);
    }

    public List<Token> findTokensByUser(Long userId) {
        return tokenRepository.findByUserId(userId);
    }

    public List<Token> findTokensByNode(int nodeNumber) {
        return tokenRepository.findByNodeNumber(nodeNumber);
    }

    public List<Token> findTokensByUserAndNode(Long userId, int nodeNumber) {
        return tokenRepository.findByUserIdAndNodeNumber(userId, nodeNumber);
    }

    public List<Token> findPendingTokens() {
        return tokenRepository.findByStatus("PENDING");
    }

    public List<Token> findTransferredTokens() {
        return tokenRepository.findByStatus("TRANSFERRED");
    }

    public List<Token> findReceivedTokens() {
        return tokenRepository.findByStatus("RECEIVED");
    }

    public List<Token> findEncryptedTokens() {
        return tokenRepository.findByStatus("ENCRYPTED");
    }

    public List<Token> findDecryptedTokens() {
        return tokenRepository.findByStatus("DECRYPTED");
    }

    public List<Token> findAllTokens() {
        return tokenRepository.findAll();
    }

    public Token updateToken(Token token) {
        return tokenRepository.save(token);
    }

    public void deleteToken(Long id) {
        tokenRepository.deleteById(id);
    }

    public long getTotalTokens() {
        return tokenRepository.count();
    }

    public long getTokenCountByStatus(String status) {
        return tokenRepository.countByStatus(status);
    }

    public boolean canDecryptToken(Long tokenId, String key) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            return token.getEncryptionKey() != null && token.getEncryptionKey().equals(key);
        }
        return false;
    }

    public List<Token> findTokensForDecryption(int nodeNumber) {
        // Find tokens that have been transferred to this node and are encrypted
        List<Token> nodeTokens = tokenRepository.findByNodeNumber(nodeNumber);
        return nodeTokens.stream()
                .filter(token -> "TRANSFERRED".equals(token.getStatus()) || "ENCRYPTED".equals(token.getStatus()))
                .filter(token -> token.getEncryptedText() != null)
                .toList();
    }
}