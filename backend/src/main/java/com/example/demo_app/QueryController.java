package com.example.demo_app;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_app.dto.QueryRequest;
import com.example.demo_app.dto.QueryResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class QueryController {

    private final Nl2SqlService nl2sqlService;

    @PostMapping
    public QueryResult query(@RequestBody QueryRequest query) {
        return nl2sqlService.ask(query.question());
    }
}
