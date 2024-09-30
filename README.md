# ai-router

## feature

- 여러 ai provider 의 Chat api 를 하나의 spec 으로 사용할 수 있습니다.
- OpenAI spec 으로 stream, non-stream api 를 지원합니다.
    - `POST /api/chat/completions`

## supported provider

- [x] `OpenAI` /api/chat/completions
- [ ] `Anthropic` /v1/messages

## how to run

1. `domain/chat/src/main/resources/domain-chat.yaml` 에 api key 를 설정 합니다.
2. `app/api-app/http-client/chatCompletion.http`

## technical point

- spring-ai 라이브러리를 사용하여 ai provider 의 api 를 추상화합니다.
- 자유도를 위해 spring-ai 의 api 방식과 (<-> mode 방식) webclient 직접 호출 방식을 지원 합니다.
    - header 를 통해 동적으로 조작할 수 있습니다. 
