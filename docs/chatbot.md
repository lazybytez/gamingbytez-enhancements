# Chat Bot

An in-game chat bot that responds to player messages based on keyword matching. Responses are selected by a weighted random system. Optionally, the bot can use an OpenAI-compatible API to generate AI responses.

## How It Works

When a player sends a chat message, the bot checks whether any configured actions have keywords that match the message. If multiple actions match, one is selected by weighted random. Each action then applies its own chance check before actually sending a response.

The bot can respond to the entire server (`BROADCAST`), only the message's original recipients (`RECEIVERS`), or only the sender (`SENDER`).

## Static Responses

Static responses are loaded from `chatbot_responses.yaml` (created in the plugin data folder). Each entry defines:

| Field | Description |
|---|---|
| `buzzwords` | List of words that trigger this action (case-insensitive) |
| `message` | The response text |
| `numerator` | Numerator of the chance fraction (e.g. `1` for 1-in-N) |
| `denominator` | Denominator of the chance fraction |
| `weight` | Priority weight for weighted random selection (default: `1`) |

**Example entry:**
```yaml
actions:
  - buzzwords:
      - "hello"
      - "hi"
    message: "Greetings, traveller!"
    numerator: 1
    denominator: 3
    weight: 1
```

## AI Responses (Optional)

When `chatbot.enable_ai_answers` is set to `true`, the bot can generate responses using an OpenAI-compatible API.

**Trigger conditions:**
- Message contains a question word in German (`wer`, `was`, `wann`, `wo`, `warum`, `wie`, `welche`, etc.)
- Message is at most 256 characters
- At least 60 seconds have passed since the last AI response (server-wide rate limit)
- 1-in-4 chance (25%) to respond

AI responses have a weight of `3`, making them higher priority than most static responses.

### Configuration

Edit `config.yml` in the plugin data folder:

```yaml
chatbot:
  enable_ai_answers: false
  system_prompt: ""
  disable_thinking: false
  prompt: |
    Your user prompt template here.
    Use %s as a placeholder for the player's message.

openai:
  apiUrl: "https://api.openai.com/v1/chat/completions"
  apiKey: ""
  organizationId: ""
  model: "gpt-3.5-turbo"
  temperature: 1.0
```

The `prompt` field supports `%s` as a placeholder that will be replaced with the player's message at runtime.

| Field | Description |
|---|---|
| `enable_ai_answers` | Set to `true` to enable AI responses |
| `system_prompt` | Optional system prompt sent as a system-role message. Leave empty to omit |
| `disable_thinking` | When `true`, sends `chat_template_kwargs` with `enable_thinking=false` in API requests. Useful for llama.cpp with models like Gemma 4 |
| `prompt` | User prompt template sent to the API. Must contain `%s` |
| `apiUrl` | API endpoint (default: OpenAI) |
| `apiKey` | Your API key |
| `organizationId` | Optional organization ID |
| `model` | Model to use |
| `temperature` | Sampling temperature (`0.0`–`2.0`) |

## Permissions

No permissions are required. The bot responds based on message content alone.
