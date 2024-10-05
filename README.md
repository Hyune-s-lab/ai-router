# ai-router

## overview

```http request
### openai spec chat api
POST /api/chat/completions
```

- chat api 는 주요 ai provider 의 chat 기능을 OpenAI spec 으로 사용할 수 있습니다.
- response spec 은 각 provider 의 응답이 by-pass 됩니다.

### environment

- spring 3.x + kotlin
- spring-ai 1.0.0-M2

### supported provider api

- [x] `OpenAI` POST /api/chat/completions
- [ ] `Anthropic` POST /v1/messages

### how to run

1. `domain/chat/src/main/resources/domain-chat.yaml` 에 api key 를 설정 합니다.
2. `app/api-app/http-client/chatCompletion.http` 에서 실행할 수 있습니다.

## feature

### phase 1. spring-ai api, webclient 방식으로 구현

- 자유도를 위해 spring-ai 의 api 방식과 (<-> mode 방식) webclient 직접 호출 방식을 지원 합니다.
    - request 를 할 때 header 를 통해 조작할 수 있습니다.

### phase 2. spring-ai api, chatClient, webclient 방식으로 구현

- chat api 는 provider 의 응답이 by-pass 되는 spring-ai api 방식으로 지원 됩니다.
- 비교를 위해 타 구현 방식도 api 로 구현 하였습니다.

```http request
### openai spec chat api By spring-ai
POST /api/chat/completions/spring-ai

### openai spec chat api By chatClient
POST /api/chat/completions/chatClient

### openai spec chat api By webclient
POST /api/chat/completions/webclient
```
