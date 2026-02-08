package com.example.blockchain_network;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.blockchain_network")
public class BlockchainNetworkApplication {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(BlockchainNetworkApplication.class, args);
    }

    @PostConstruct
    public void initializeDatabase() {
        try {
            // Create users table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    node_number INT DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);

            // Create tokens table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS tokens (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    node_number INT NOT NULL,
                    token_text TEXT NOT NULL,
                    encrypted_text TEXT,
                    encryption_key VARCHAR(255),
                    encryption_algorithm VARCHAR(50),
                    status VARCHAR(20) DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Create transactions table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id VARCHAR(255) PRIMARY KEY,
                    node_number INT NOT NULL,
                    encryption_key VARCHAR(255) NOT NULL,
                    source_node INT NOT NULL,
                    destination_node INT NOT NULL,
                    token_id BIGINT,
                    encrypted_data TEXT,
                    encryption_algorithm VARCHAR(50),
                    status VARCHAR(20) DEFAULT 'PENDING',
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (token_id) REFERENCES tokens(id)
                )
            """);

            System.out.println("Database tables initialized successfully!");

        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}