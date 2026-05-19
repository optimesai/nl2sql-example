package com.example.demo_app;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo_app.dto.QueryResult;
import com.example.demo_app.mapper.RawSqlMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Nl2SqlService {

    private final SqlAssistant sqlAssistant;
    private final IntentClassifier intentClassifier;
    private final AnswerGenerator answerGenerator;
    private final RawSqlMapper rawSqlMapper;
    private final DatabaseSchemaService databaseSchemaService;

    public QueryResult ask(String question) {
        String answer;

        log.info("\nQuestion: {} \n", question);
        
        // Step 0: Get dynamic schema
        String schema = databaseSchemaService.getSchemaDescription();
        log.info("\nStep 0 (Schema):\n{}", schema);

        // Step 1: Intent Classification
        String intent = intentClassifier.classify(question, schema);
        log.info("\nStep 1 (Intent Raw):\n{}", intent);

        if (intent != null && intent.toUpperCase().startsWith("NO")) {
            return new QueryResult(null, null, "데이터 조회와 관련 없는 질문입니다.");
        }

        // Step 2: SQL Generation
        String sql = sqlAssistant.generateSql(schema, question);
        sql = sanitize(sql);
        log.info("\nStep 2 (SQL):\n{}", sql);

        // Step 3: Execution
        List<Map<String, Object>> data;
        try {
            data = rawSqlMapper.executeRawSql(sql);
        } catch (Exception e) {
            System.err.println("Execution Error: " + e.getMessage());
            return new QueryResult(sql, null, "쿼리 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
        log.info("\nStep 3 (Data):\n{} ", data);

        // Step 4: Answer Generation
        answer = answerGenerator.generateAnswer(question, data.toString());
        log.info("\nStep 4 (Answer):\n{} ", answer);
        
        return new QueryResult(sql, data, answer);
    }

    private String sanitize(String sql) {
        return sql.replace("```sql", "").replace("```", "").trim();
    }
}
