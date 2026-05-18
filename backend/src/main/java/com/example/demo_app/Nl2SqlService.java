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

@Service
@RequiredArgsConstructor
public class Nl2SqlService {

    private final ChatModel chatModel;

    private final String SYSTEM_PROMPT = """
        You are a SQL expert. Translate natural language into MySQL query.
        Database Schema:
        Table: inventory
        Columns: id (int), item_name (varchar), quantity (int)
        
        Rules:
        - Return ONLY the SQL query.
        - Do not use markdown backticks.
        - Only generate SELECT queries.
        """;

    public QueryResult ask(String question) {
        List<ChatMessage> messages = List.of(
            SystemMessage.from(SYSTEM_PROMPT),
            UserMessage.from(question)
        );
        
        ChatRequest chatRequest = ChatRequest.builder()
            .messages(messages)
            .build();

        ChatResponse chatResponse = chatModel.chat(chatRequest);
        String sql = chatResponse.aiMessage().text();

        // Remove potential backticks if LLM includes them
        sql = sql.replace("```sql", "").replace("```", "").trim();
        
        System.out.println("Generated SQL: " + sql);
        
        return new QueryResult(sql);
    }
}
