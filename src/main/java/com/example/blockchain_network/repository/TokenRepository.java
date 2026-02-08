package com.example.blockchain_network.repository;



import com.example.blockchain_network.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class TokenRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Token> tokenRowMapper = new RowMapper<Token>() {
        @Override
        public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
            Token token = new Token();
            token.setId(rs.getLong("id"));
            token.setUserId(rs.getLong("user_id"));
            token.setNodeNumber(rs.getInt("node_number"));
            token.setTokenText(rs.getString("token_text"));
            token.setEncryptedText(rs.getString("encrypted_text"));
            token.setEncryptionKey(rs.getString("encryption_key"));
            token.setEncryptionAlgorithm(rs.getString("encryption_algorithm"));
            token.setStatus(rs.getString("status"));
            token.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return token;
        }
    };

    public Token save(Token token) {
        if (token.getId() == null) {
            // Insert new token
            String sql = "INSERT INTO tokens (user_id, node_number, token_text, encrypted_text, encryption_key, encryption_algorithm, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, token.getUserId());
                ps.setInt(2, token.getNodeNumber());
                ps.setString(3, token.getTokenText());
                ps.setString(4, token.getEncryptedText());
                ps.setString(5, token.getEncryptionKey());
                ps.setString(6, token.getEncryptionAlgorithm());
                ps.setString(7, token.getStatus());
                return ps;
            }, keyHolder);
            
            token.setId(keyHolder.getKey().longValue());
        } else {
            // Update existing token
            String sql = "UPDATE tokens SET user_id = ?, node_number = ?, token_text = ?, encrypted_text = ?, encryption_key = ?, encryption_algorithm = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql, token.getUserId(), token.getNodeNumber(), token.getTokenText(), token.getEncryptedText(), token.getEncryptionKey(), token.getEncryptionAlgorithm(), token.getStatus(), token.getId());
        }
        return token;
    }

    public Optional<Token> findById(Long id) {
        String sql = "SELECT * FROM tokens WHERE id = ?";
        List<Token> tokens = jdbcTemplate.query(sql, tokenRowMapper, id);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    public List<Token> findByUserId(Long userId) {
        String sql = "SELECT * FROM tokens WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, tokenRowMapper, userId);
    }

    public List<Token> findByNodeNumber(int nodeNumber) {
        String sql = "SELECT * FROM tokens WHERE node_number = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, tokenRowMapper, nodeNumber);
    }

    public List<Token> findByStatus(String status) {
        String sql = "SELECT * FROM tokens WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, tokenRowMapper, status);
    }

    public List<Token> findByUserIdAndNodeNumber(Long userId, int nodeNumber) {
        String sql = "SELECT * FROM tokens WHERE user_id = ? AND node_number = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, tokenRowMapper, userId, nodeNumber);
    }

    public List<Token> findAll() {
        String sql = "SELECT * FROM tokens ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, tokenRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM tokens WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM tokens";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM tokens WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }
}