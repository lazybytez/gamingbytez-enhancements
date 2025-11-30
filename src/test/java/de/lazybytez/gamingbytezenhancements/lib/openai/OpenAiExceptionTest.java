package de.lazybytez.gamingbytezenhancements.lib.openai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiExceptionTest {
    @Test
    void constructor_setsPropertiesCorrectly() {
        OpenAiException exception = new OpenAiException("Test error", 400, "bad_request");

        assertEquals("Test error", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals("bad_request", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withNoErrorField_returnsNull() {
        String jsonResponse = """
                {
                    "choices": [{
                        "message": {
                            "role": "assistant",
                            "content": "Success"
                        }
                    }]
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 200);

        assertNull(exception);
    }

    @Test
    void createFromResponse_withValidError_parsesCorrectly() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Invalid API key provided",
                        "code": "invalid_api_key"
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 401);

        assertNotNull(exception);
        assertEquals("Invalid API key provided", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
        assertEquals("invalid_api_key", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withRateLimitError_parsesCorrectly() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Rate limit exceeded",
                        "code": "rate_limit_exceeded"
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 429);

        assertNotNull(exception);
        assertEquals("Rate limit exceeded", exception.getMessage());
        assertEquals(429, exception.getStatusCode());
        assertEquals("rate_limit_exceeded", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withMalformedError_throwsNullPointerException() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Some error"
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        assertThrows(NullPointerException.class, () ->
                OpenAiException.createFromResponse(parsedJson, 500)
        );
    }

    @Test
    void createFromResponse_withEmptyErrorObject_throwsNullPointerException() {
        String jsonResponse = """
                {
                    "error": {}
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        assertThrows(NullPointerException.class, () ->
                OpenAiException.createFromResponse(parsedJson, 500)
        );
    }

    @Test
    void createFromResponse_withServerError_parsesCorrectly() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Internal server error",
                        "code": "server_error"
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 500);

        assertNotNull(exception);
        assertEquals("Internal server error", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals("server_error", exception.getErrorCode());
    }
}
