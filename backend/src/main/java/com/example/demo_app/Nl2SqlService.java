package com.example.demo_app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo_app.dto.QueryResult;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Nl2SqlService {

    private final IntentClassifier intentClassifier;
    private final DatabaseSchemaService databaseSchemaService;

    public QueryResult ask(String question) {
        String answer;

        // Step 0: Get dynamic schema
        String schema = databaseSchemaService.getSchemaDescription();
        log.info("\nStep 0 (Schema):\n{}", schema);

        // Step 1: Intent Classification
        String intent = intentClassifier.classify(question, schema);
        log.info("\nStep 1 (Intent Raw):\n{}", intent);

        if (intent != null && intent.toUpperCase().startsWith("NO")) {
            return new QueryResult(null, null, "데이터 조회와 관련 없는 질문입니다.");
        }
        
        return new QueryResult(sql, data, answer);
    }
}
