package com.example.blockchain_network.service;



import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EncryptionService {

    private final Random random = new Random();
    private final String[] algorithms = {"AFFINE", "CAESAR", "BLOCK"};

    public String getRandomAlgorithm() {
        return algorithms[random.nextInt(algorithms.length)];
    }

    public String encrypt(String text, String key, String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "AFFINE":
                return affineEncrypt(text, key);
            case "CAESAR":
                return caesarEncrypt(text, key);
            case "BLOCK":
                return blockEncrypt(text, key);
            default:
                throw new IllegalArgumentException("Unknown encryption algorithm: " + algorithm);
        }
    }

    public String decrypt(String encryptedText, String key, String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "AFFINE":
                return affineDecrypt(encryptedText, key);
            case "CAESAR":
                return caesarDecrypt(encryptedText, key);
            case "BLOCK":
                return blockDecrypt(encryptedText, key);
            default:
                throw new IllegalArgumentException("Unknown encryption algorithm: " + algorithm);
        }
    }

    // Caesar Cipher Implementation
    private String caesarEncrypt(String text, String key) {
        int shift = Math.abs(key.hashCode() % 26);
        StringBuilder encrypted = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base + shift) % 26 + base);
                encrypted.append(shifted);
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }

    private String caesarDecrypt(String encryptedText, String key) {
        int shift = Math.abs(key.hashCode() % 26);
        StringBuilder decrypted = new StringBuilder();
        
        for (char c : encryptedText.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base - shift + 26) % 26 + base);
                decrypted.append(shifted);
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
    }

    // Affine Cipher Implementation
    private String affineEncrypt(String text, String key) {
        int a = Math.abs(key.hashCode() % 25) + 1; // Ensure a is coprime with 26
        if (gcd(a, 26) != 1) a = 7; // Default safe value
        int b = Math.abs(key.hashCode() % 26);
        
        StringBuilder encrypted = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int x = c - base;
                char shifted = (char) ((a * x + b) % 26 + base);
                encrypted.append(shifted);
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }

    private String affineDecrypt(String encryptedText, String key) {
        int a = Math.abs(key.hashCode() % 25) + 1;
        if (gcd(a, 26) != 1) a = 7;
        int b = Math.abs(key.hashCode() % 26);
        int aInverse = modInverse(a, 26);
        
        StringBuilder decrypted = new StringBuilder();
        for (char c : encryptedText.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int y = c - base;
                char shifted = (char) ((aInverse * (y - b + 26)) % 26 + base);
                decrypted.append(shifted);
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
    }

    // Block Cipher Implementation (Simple substitution with key)
    private String blockEncrypt(String text, String key) {
        StringBuilder encrypted = new StringBuilder();
        int keyIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char keyChar = key.charAt(keyIndex % key.length());
                int shift = Character.toLowerCase(keyChar) - 'a';
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base + shift) % 26 + base);
                encrypted.append(shifted);
                keyIndex++;
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }

    private String blockDecrypt(String encryptedText, String key) {
        StringBuilder decrypted = new StringBuilder();
        int keyIndex = 0;
        
        for (char c : encryptedText.toCharArray()) {
            if (Character.isLetter(c)) {
                char keyChar = key.charAt(keyIndex % key.length());
                int shift = Character.toLowerCase(keyChar) - 'a';
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base - shift + 26) % 26 + base);
                decrypted.append(shifted);
                keyIndex++;
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
    }

    // Helper methods for Affine cipher
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return 1;
    }
}