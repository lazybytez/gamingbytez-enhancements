package de.lazybytez.gamingbytezenhancements.lib.openai;

import com.google.gson.JsonObject;

/**
 * Exception that can be thrown when OpenAI API requests fail.
 */
public class OpenAiException extends Exception {
    private final int statusCode;

    private final String errorCode;

    public OpenAiException(String message, int statusCode, String errorCode) {
        super(message);

        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    /**
     * Create an error from some OpenAI API response JSON.
     */
    public static OpenAiException createFromResponse(JsonObject parsedBody, int statusCode) {
        if (!parsedBody.has(OpenAiError.ERROR)) {
            return null;
        }

        try {
            JsonObject error = parsedBody.get(OpenAiError.ERROR).getAsJsonObject();
            String errorMessage = error.get(OpenAiError.MESSAGE).getAsString();
            String errorCode = error.get(OpenAiError.CODE).getAsString();

            return new OpenAiException(errorMessage, statusCode, errorCode);
        } catch (IllegalStateException e) {
            return new OpenAiException(e.getMessage(), statusCode, "failed_to_parse_json");
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
