package de.lazybytez.gamingbytezenhancements.lib.openai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Tailored OpenAI response object that holds the first message's content.
 */
public record OpenAiResponse(String role, String content, int totalTokens) {
    public static final String CHOICES = "choices";
    public static final String MESSAGE = "message";
    public static final String ROLE = "role";
    public static final String CONTENT = "content";
    public static final String USAGE = "usage";
    public static final String TOTAL_TOKENS = "total_tokens";

    public static OpenAiResponse createFromJsonResponse(JsonObject parsedJson) throws OpenAiException {
        try {
            JsonArray choices = parsedJson.get(CHOICES).getAsJsonArray();
            JsonObject firstChoice = choices.get(0).getAsJsonObject();

            JsonObject message = firstChoice.get(MESSAGE).getAsJsonObject();
            String role = message.get(ROLE).getAsString();
            String content = message.get(CONTENT).getAsString();

            JsonObject usage = parsedJson.get(USAGE).getAsJsonObject();
            int totalTokens = usage.get(TOTAL_TOKENS).getAsInt();

            return new OpenAiResponse(role, content, totalTokens);
        } catch (IllegalStateException | NullPointerException e) {
            throw new OpenAiException(e.getMessage(), 500, "openai_result_parse");
        }
    }
}
