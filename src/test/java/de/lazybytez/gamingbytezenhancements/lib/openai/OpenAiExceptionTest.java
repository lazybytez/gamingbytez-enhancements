/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 200, jsonResponse);

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

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 401, jsonResponse);

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

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 429, jsonResponse);

        assertNotNull(exception);
        assertEquals("Rate limit exceeded", exception.getMessage());
        assertEquals(429, exception.getStatusCode());
        assertEquals("rate_limit_exceeded", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withErrorMissingItsCode_reportsTheMessage() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Some error"
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 500, jsonResponse);

        assertEquals("Some error", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals("http_500", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withNullErrorCode_reportsTheMessage() {
        String jsonResponse = """
                {
                    "error": {
                        "message": "Unknown parameter: chat_template_kwargs",
                        "code": null
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 400, jsonResponse);

        assertEquals("Unknown parameter: chat_template_kwargs", exception.getMessage());
        assertEquals("http_400", exception.getErrorCode());
    }

    @Test
    void createFromResponse_withEmptyErrorObject_quotesTheBody() {
        String jsonResponse = """
                {
                    "error": {}
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 500, jsonResponse);

        assertEquals("failed_to_parse_json", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("\"error\": {}"));
    }

    @Test
    void createFromResponse_withFailingStatusAndNoErrorField_reportsTheStatusAndBody() {
        String jsonResponse = """
                {
                    "object": "error",
                    "message": "Unknown parameter chat_template_kwargs",
                    "type": "BadRequestError"
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 400, jsonResponse);

        assertEquals(400, exception.getStatusCode());
        assertEquals("http_400", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Unknown parameter chat_template_kwargs"));
    }

    @Test
    void createFromResponse_withSuccessStatusAndNoErrorField_returnsNull() {
        String jsonResponse = """
                {
                    "choices": []
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        assertNull(OpenAiException.createFromResponse(parsedJson, 200, jsonResponse));
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

        OpenAiException exception = OpenAiException.createFromResponse(parsedJson, 500, jsonResponse);

        assertNotNull(exception);
        assertEquals("Internal server error", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals("server_error", exception.getErrorCode());
    }
}
