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

## 사전 준비

```bash
cd backend

# 환경 변수 파일 복사
cp .env.example .env
cp .secret.env.example .secret.env
```

`.secret.env`에 Upstage API 키를 설정합니다:

```env
OPENAI_API_KEY=up_xxxxx
```

## 실행 (로컬 빌드 + Docker)

Spring Boot 3.2+ jarmode extract로 레이어를 분리하여 Docker layer cache를 극대화합니다.

```bash
cd backend

# 1. 로컬 JAR 빌드 + 레이어 추출
./gradlew bootJar -x test --parallel
java -Djarmode=tools -jar build/libs/app.jar extract --layers --launcher --destination build/extracted

# 2. Docker 빌드 + 실행
docker compose up -d app --build

# 3. 로그 확인
docker logs -f nl2sql-app
```

> `bash run_docker.sh` (Linux/macOS) / `run_docker.ps1` (Windows) 스크립트로 1~4단계를 자동화할 수 있습니다.

프론트엔드 실행:
```bash
cd frontend && npm install && npm run dev
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
