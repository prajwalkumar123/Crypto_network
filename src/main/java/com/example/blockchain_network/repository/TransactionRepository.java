package com.example.blockchain_network.repository;



import com.example.blockchain_network.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> transactionRowMapper = new RowMapper<Transaction>() {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(rs.getString("transaction_id"));
            transaction.setNodeNumber(rs.getInt("node_number"));
            transaction.setEncryptionKey(rs.getString("encryption_key"));
            transaction.setSourceNode(rs.getInt("source_node"));
            transaction.setDestinationNode(rs.getInt("destination_node"));
            transaction.setTokenId(rs.getLong("token_id"));
            transaction.setEncryptedData(rs.getString("encrypted_data"));
            transaction.setEncryptionAlgorithm(rs.getString("encryption_algorithm"));
            transaction.setStatus(rs.getString("status"));
            transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            return transaction;
        }
    };

    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, node_number, encryption_key, source_node, destination_node, token_id, encrypted_data, encryption_algorithm, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE status = ?, encrypted_data = ?, encryption_algorithm = ?";
        jdbcTemplate.update(sql,
            transaction.getTransactionId(),
            transaction.getNodeNumber(),
            transaction.getEncryptionKey(),
            transaction.getSourceNode(),
            transaction.getDestinationNode(),
            transaction.getTokenId(),
            transaction.getEncryptedData(),
            transaction.getEncryptionAlgorithm(),
            transaction.getStatus(),
            transaction.getStatus(),
            transaction.getEncryptedData(),
            transaction.getEncryptionAlgorithm());
        return transaction;
    }

    public Optional<Transaction> findByTransactionId(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        List<Transaction> transactions = jdbcTemplate.query(sql, transactionRowMapper, transactionId);
        return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions.get(0));
    }

    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }

    public List<Transaction> findByNodeNumber(int nodeNumber) {
        String sql = "SELECT * FROM transactions WHERE node_number = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, nodeNumber);
    }

    public List<Transaction> findBySourceNode(int sourceNode) {
        String sql = "SELECT * FROM transactions WHERE source_node = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, sourceNode);
    }

    public List<Transaction> findByDestinationNode(int destinationNode) {
        String sql = "SELECT * FROM transactions WHERE destination_node = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, destinationNode);
    }

    public List<Transaction> findByStatus(String status) {
        String sql = "SELECT * FROM transactions WHERE status = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, status);
    }

    public List<Transaction> findBySourceOrDestinationNode(int nodeNumber) {
        String sql = "SELECT * FROM transactions WHERE source_node = ? OR destination_node = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, nodeNumber, nodeNumber);
    }

    public List<Transaction> findByTokenId(Long tokenId) {
        String sql = "SELECT * FROM transactions WHERE token_id = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, tokenId);
    }

    public void deleteByTransactionId(String transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        jdbcTemplate.update(sql, transactionId);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM transactions";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }

    public long countByNodeNumber(int nodeNumber) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE source_node = ? OR destination_node = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, nodeNumber, nodeNumber);
        return count != null ? count : 0L;
    }
}