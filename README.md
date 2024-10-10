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

### phase 3. anthropic 연동

> 연동 테스트를 하다보니 by-pass 가 불가능한 케이스를 발견 하였습니다.  
> 원인 파악에 시간이 걸릴 것 같아 한번 끊고 갑니다.

- openai stream

```shell
### request body
{
  "model": "gpt-4o",
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant."
    },
    {
      "role": "user",
      "content": "Hello!"
    }
  ],
  "stream": true
}


### https://api.openai.com/v1/chat/completions
data: {"id":"chatcmpl-AGqf9IeeentpDcJmJ6BMmg1knWagP","object":"chat.completion.chunk","created":1728579047,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_4ea369768a","choices":[{"index":0,"delta":{"role":"assistant","content":"","refusal":null},"logprobs":null,"finish_reason":null}]}

...

data: {"id":"chatcmpl-AGqf9IeeentpDcJmJ6BMmg1knWagP","object":"chat.completion.chunk","created":1728579047,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_4ea369768a","choices":[{"index":0,"delta":{},"logprobs":null,"finish_reason":"stop"}]}

data: [DONE]


### {{host}}/api/chat/completions/spring-api/openai 
# value 가 null 인 key 는 제거
# 그 외에는 native 와 동일

data:{"id":"chatcmpl-AGqhzt4YH9ezylnhdtuitrqnwQhmR","choices":[{"index":0,"delta":{"content":"","role":"assistant"}}],"created":1728579223,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_6b68a8204b","object":"chat.completion.chunk"}

...

data:{"id":"chatcmpl-AGqhzt4YH9ezylnhdtuitrqnwQhmR","choices":[{"finish_reason":"stop","index":0,"delta":{}}],"created":1728579223,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_6b68a8204b","object":"chat.completion.chunk"}

data:[DONE]


### {{host}}/api/chat/completions/chat-client/openai
# header 로 오던 값들이 객체에 들어감

data:{"result":{"output":{"messageType":"ASSISTANT","metadata":{"refusal":"","finishReason":"","index":0,"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","role":"ASSISTANT","messageType":"ASSISTANT"},"toolCalls":[],"content":""},"metadata":{"finishReason":"","contentFilterMetadata":null}},"results":[{"output":{"messageType":"ASSISTANT","metadata":{"refusal":"","finishReason":"","index":0,"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","role":"ASSISTANT","messageType":"ASSISTANT"},"toolCalls":[],"content":""},"metadata":{"finishReason":"","contentFilterMetadata":null}}],"metadata":{"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","model":"gpt-4o-2024-08-06","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"promptTokens":0,"generationTokens":0,"totalTokens":0},"promptMetadata":[],"empty":false}}

...

data:{"result":{"output":{"messageType":"ASSISTANT","metadata":{"refusal":"","finishReason":"STOP","index":0,"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","role":"ASSISTANT","messageType":"ASSISTANT"},"toolCalls":[],"content":null},"metadata":{"finishReason":"STOP","contentFilterMetadata":null}},"results":[{"output":{"messageType":"ASSISTANT","metadata":{"refusal":"","finishReason":"STOP","index":0,"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","role":"ASSISTANT","messageType":"ASSISTANT"},"toolCalls":[],"content":null},"metadata":{"finishReason":"STOP","contentFilterMetadata":null}}],"metadata":{"id":"chatcmpl-AGqii9sl6oslnjsubQDQHdRoSyiia","model":"gpt-4o-2024-08-06","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"promptTokens":0,"generationTokens":0,"totalTokens":0},"promptMetadata":[],"empty":false}}

data:[DONE]


### {{host}}/api/chat/completions/webclient/openai
# native 와 동일
data:{"id":"chatcmpl-AGqlHuImdEzzlj1pEHJrAmVG4DjuT","object":"chat.completion.chunk","created":1728579427,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_a20a4ee344","choices":[{"index":0,"delta":{"role":"assistant","content":"","refusal":null},"logprobs":null,"finish_reason":null}]}

...

data:{"id":"chatcmpl-AGqlHuImdEzzlj1pEHJrAmVG4DjuT","object":"chat.completion.chunk","created":1728579427,"model":"gpt-4o-2024-08-06","system_fingerprint":"fp_a20a4ee344","choices":[{"index":0,"delta":{},"logprobs":null,"finish_reason":"stop"}]}

data:[DONE]
``` 

- anthropic stream

```shell
### request body
{
  "model": "claude-3-5-sonnet-20240620",
  "max_tokens": 1024,
  "messages": [
    {
      "role": "user",
      "content": "Hello, world"
    }
  ],
  "stream": true
}


### https://api.anthropic.com/v1/messages
event: message_start
data: {"type":"message_start","message":{"id":"msg_012nSPYvyXnALYZhmguHPZNf","type":"message","role":"assistant","model":"claude-3-5-sonnet-20240620","content":[],"stop_reason":null,"stop_sequence":null,"usage":{"input_tokens":10,"output_tokens":3}}           }

event: content_block_start
data: {"type":"content_block_start","index":0,"content_block":{"type":"text","text":""}        }

event: ping
data: {"type": "ping"}

...

event: message_delta
data: {"type":"message_delta","delta":{"stop_reason":"end_turn","stop_sequence":null},"usage":{"output_tokens":30}               }

event: message_stop
data: {"type":"message_stop"}


### {{host}}/api/chat/completions/spring-api/anthropic
# native 와 매우 다름
data:{"result":null,"results":[],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":18,"promptTokens":15,"generationTokens":3},"promptMetadata":[],"empty":false}}

data:{"result":{"output":{"messageType":"ASSISTANT","metadata":{"messageType":"ASSISTANT"},"toolCalls":[],"content":""},"metadata":{"finishReason":null,"contentFilterMetadata":null}},"results":[{"output":{"messageType":"ASSISTANT","metadata":{"messageType":"ASSISTANT"},"toolCalls":[],"content":""},"metadata":{"finishReason":null,"contentFilterMetadata":null}}],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":18,"promptTokens":15,"generationTokens":3},"promptMetadata":[],"empty":false}}

data:{"result":{"output":{"messageType":"ASSISTANT","metadata":{"messageType":"ASSISTANT"},"toolCalls":[],"content":"Hello! It"},"metadata":{"finishReason":null,"contentFilterMetadata":null}},"results":[{"output":{"messageType":"ASSISTANT","metadata":{"messageType":"ASSISTANT"},"toolCalls":[],"content":"Hello! It"},"metadata":{"finishReason":null,"contentFilterMetadata":null}}],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":18,"promptTokens":15,"generationTokens":3},"promptMetadata":[],"empty":false}}

...

data:{"result":null,"results":[],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":18,"promptTokens":15,"generationTokens":3},"promptMetadata":[],"empty":false}}

data:{"result":null,"results":[],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":52,"promptTokens":15,"generationTokens":37},"promptMetadata":[],"empty":false}}

data:{"result":null,"results":[],"metadata":{"id":"msg_01S9ZWAePTcpNaZtptVz1pHZ","model":"claude-3-5-sonnet-20240620","rateLimit":{"tokensLimit":0,"tokensReset":"PT0S","requestsLimit":0,"requestsRemaining":0,"requestsReset":"PT0S","tokensRemaining":0},"usage":{"totalTokens":52,"promptTokens":15,"generationTokens":37},"promptMetadata":[],"empty":false}}


### {{host}}/api/chat/completions/chat-client/anthropic
# native 의 event 가 객체에 들어감
data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"MESSAGE_START","role":"assistant","content":[],"model":"claude-3-5-sonnet-20240620","usage":{"input_tokens":15,"output_tokens":3}}

data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"CONTENT_BLOCK_START","role":"assistant","content":[{"type":"text","text":"","index":0}],"model":"claude-3-5-sonnet-20240620","usage":{"input_tokens":15,"output_tokens":3}}

data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"CONTENT_BLOCK_DELTA","role":"assistant","content":[{"type":"text_delta","text":"Hello! It","index":0}],"model":"claude-3-5-sonnet-20240620","usage":{"input_tokens":15,"output_tokens":3}}

...

data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"CONTENT_BLOCK_STOP","role":"assistant","content":[],"model":"claude-3-5-sonnet-20240620","usage":{"input_tokens":15,"output_tokens":3}}

data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"MESSAGE_DELTA","role":"assistant","content":[],"model":"claude-3-5-sonnet-20240620","stop_reason":"end_turn","usage":{"input_tokens":15,"output_tokens":37}}

data:{"id":"msg_014ekwp8UXUmohUFoUpw3cqL","type":"MESSAGE_DELTA","role":"assistant","content":[],"model":"claude-3-5-sonnet-20240620","stop_reason":"end_turn","usage":{"input_tokens":15,"output_tokens":37}}


### {{host}}/api/chat/completions/webclient/anthropic
# event 만 제외하고 native 와 동일
data:{"type":"message_start","message":{"id":"msg_01EeLcoRZLnQwBPEYRvbp8Ne","type":"message","role":"assistant","model":"claude-3-5-sonnet-20240620","content":[],"stop_reason":null,"stop_sequence":null,"usage":{"input_tokens":9,"output_tokens":3}}}

data:{"type":"content_block_start","index":0,"content_block":{"type":"text","text":""}}

data:{"type":"ping"}

...

data:{"type":"content_block_delta","index":0,"delta":{"type":"text_delta","text":" need help with anything."}}

data:{"type":"content_block_stop","index":0}

data:{"type":"message_delta","delta":{"stop_reason":"end_turn","stop_sequence":null},"usage":{"output_tokens":30}}

data:{"type":"message_stop"}
```
