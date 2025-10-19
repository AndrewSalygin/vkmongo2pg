package com.andrewsalygin.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private static final int BATCH_SIZE = 500;

    private final JdbcTemplate jdbcTemplate;

    public void insertVkGroup(String groupId) {
        jdbcTemplate.update(
        "INSERT INTO vk_group(id) VALUES (?) ON CONFLICT DO NOTHING",
            groupId
        );
    }

    public void batchInsert(String tableName, List<Map<String, Object>> batch) {
        if (batch.isEmpty()) return;

        for (int i = 0; i < batch.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, batch.size());
            List<Map<String, Object>> sublist = batch.subList(i, end);

            String[] columns = sublist.getFirst().keySet().toArray(new String[0]);
            String sql = buildInsertSql(tableName, columns);

            List<Object[]> batchArgs = sublist.stream()
                .map(map -> Arrays.stream(columns).map(map::get).toArray())
                .toList();

            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    private String buildInsertSql(String tableName, String[] columns) {
        String columnNames = String.join(", ", columns);
        String placeholders = Arrays.stream(columns).map(c -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + "(" + columnNames + ") VALUES (" + placeholders + ") ON CONFLICT DO NOTHING";
    }
}
