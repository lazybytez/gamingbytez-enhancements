# Chat Bot

An in-game chat bot that responds to player messages based on keyword matching. Responses are selected by a weighted random system. Optionally, the bot can use an OpenAI-compatible API to generate AI responses.

## How It Works

When a player sends a chat message, the bot checks whether any configured actions have keywords that match the message. If multiple actions match, one is selected by weighted random. Each action then applies its own chance check before actually sending a response.

The bot can respond to the entire server (`BROADCAST`), only the message's original recipients (`RECEIVERS`), or only the sender (`SENDER`).

## Static Responses

Static responses are loaded from `static_response_chat_bot.yaml` (created in the plugin data folder). Each entry defines:

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
| `temperature` | Sampling temperature (`0.0` to `2.0`) |

## Commands

**Aliases:** `/chatbot`, `/gbcb`

**Permission:** `gamingbytez.chatbot.admin`, granted to operators by default. A sender without it
does not see the command at all.

Running the command without a subcommand prints the help listing under a heading naming the
invoked command.

### `reload`

Reloads the static responses and the chat bot settings in one go.

```
/gbcb reload
```

### `reload responses`

Re-reads `static_response_chat_bot.yaml` and swaps the active static responses. The file is read
off the server thread and the bot keeps answering with the previous responses until the fresh ones
are in place. If the file cannot be read, the previous responses stay active and the operator is
told so.

```
/gbcb reload responses
```

### `reload settings`

Re-reads the `chatbot` and `openai` sections of `config.yml` and replaces the AI action with one
built from the fresh settings. Turning `enable_ai_answers` off removes AI answers without a
restart. Replacing the action resets its rate limit window and its token usage counter.

```
/gbcb reload settings
```

## Permissions

Responding to chat requires no permission, the bot reacts to message content alone. Managing the
bot requires `gamingbytez.chatbot.admin`, see Commands above.
