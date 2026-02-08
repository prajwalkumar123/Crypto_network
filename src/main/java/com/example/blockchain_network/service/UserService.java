package com.example.blockchain_network.service;



import com.example.blockchain_network.entity.User;
import com.example.blockchain_network.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    public User registerUser(User user) throws Exception {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email already exists");
        }

        // Hash password
        user.setPassword(hashPassword(user.getPassword()));
        
        // Assign random node number between 1 and 10
        user.setNodeNumber(random.nextInt(10) + 1);

        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(hashPassword(password))) {
                return userOpt;
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findUsersByNode(int nodeNumber) {
        return userRepository.findByNodeNumber(nodeNumber);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserNode(Long userId, int newNodeNumber) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNodeNumber(newNodeNumber);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public boolean validateUser(String username, String password) {
        Optional<User> user = loginUser(username, password);
        return user.isPresent();
    }

    public User changePassword(Long userId, String currentPassword, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(hashPassword(currentPassword))) {
                user.setPassword(hashPassword(newPassword));
                return userRepository.save(user);
            } else {
                throw new Exception("Current password is incorrect");
            }
        }
        throw new Exception("User not found");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public int getNextNode(int currentNode) {
        return currentNode >= 10 ? 1 : currentNode + 1;
    }

    public int getPreviousNode(int currentNode) {
        return currentNode <= 1 ? 10 : currentNode - 1;
    }

    public boolean isValidNodeNumber(int nodeNumber) {
        return nodeNumber >= 1 && nodeNumber <= 10;
    }
}