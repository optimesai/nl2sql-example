# nl2sql-example

LangChain4j 기반 NL2SQL PoC 프로젝트. 자연어 질문을 SQL로 변환하고 MySQL에서 실행하여 결과를 자연어로 응답합니다.

## 파이프라인

```
질문 → IntentClassifier(의도 분류)
     → DatabaseSchemaService(스키마 로드)
     → SqlAssistant(SQL 생성)
     → 보안 검증(SELECT/SHOW 화이트리스트)
     → RawSqlMapper(MyBatis 실행)
     → AnswerGenerator(자연어 응답 생성)
```

## 실행

```bash
# DB 실행
cd backend && docker compose up -d mysql

# 백엔드 실행
./gradlew bootRun

# 프론트엔드 실행 (선택)
cd ../frontend && npm install && npm run dev
```

## API

```http
POST /api/query
X-Employee-Id: user-1
Content-Type: application/json

{"question": "오늘 입고된 품목 수는?"}
```

## 기술 스택

| 영역 | 기술 |
|------|------|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot |
| AI | LangChain4j + OpenAI |
| DB | MySQL + MyBatis |
| Frontend | Vue 3 + TypeScript |
