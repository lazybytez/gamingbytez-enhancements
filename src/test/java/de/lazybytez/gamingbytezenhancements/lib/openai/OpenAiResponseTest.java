package de.lazybytez.gamingbytezenhancements.lib.openai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiResponseTest {
    @Test
    void createFromJsonResponse_withValidResponse_parsesCorrectly() throws OpenAiException {
        String jsonResponse = """
                {
                    "choices": [{
                        "message": {
                            "role": "assistant",
                            "content": "Hello! How can I help you?"
                        }
                    }],
                    "usage": {
                        "total_tokens": 42
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiResponse response = OpenAiResponse.createFromJsonResponse(parsedJson);

        assertEquals("assistant", response.role());
        assertEquals("Hello! How can I help you?", response.content());
        assertEquals(42, response.totalTokens());
    }

    @Test
    void createFromJsonResponse_withMultipleChoices_usesFirstChoice() throws OpenAiException {
        String jsonResponse = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "First response"
                            }
                        },
                        {
                            "message": {
                                "role": "assistant",
                                "content": "Second response"
                            }
                        }
                    ],
                    "usage": {
                        "total_tokens": 100
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiResponse response = OpenAiResponse.createFromJsonResponse(parsedJson);

        assertEquals("First response", response.content());
    }

    @Test
    void createFromJsonResponse_withMissingChoices_throwsException() {
        String jsonResponse = """
                {
                    "usage": {
                        "total_tokens": 42
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = assertThrows(OpenAiException.class, () ->
                OpenAiResponse.createFromJsonResponse(parsedJson)
        );

        assertEquals(500, exception.getStatusCode());
        assertEquals("openai_result_parse", exception.getErrorCode());
    }

    @Test
    void createFromJsonResponse_withMissingMessage_throwsException() {
        String jsonResponse = """
                {
                    "choices": [{}],
                    "usage": {
                        "total_tokens": 42
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = assertThrows(OpenAiException.class, () ->
                OpenAiResponse.createFromJsonResponse(parsedJson)
        );

        assertEquals(500, exception.getStatusCode());
        assertEquals("openai_result_parse", exception.getErrorCode());
    }

    @Test
    void createFromJsonResponse_withMissingUsage_throwsException() {
        String jsonResponse = """
                {
                    "choices": [{
                        "message": {
                            "role": "assistant",
                            "content": "Hello!"
                        }
                    }]
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiException exception = assertThrows(OpenAiException.class, () ->
                OpenAiResponse.createFromJsonResponse(parsedJson)
        );

        assertEquals(500, exception.getStatusCode());
        assertEquals("openai_result_parse", exception.getErrorCode());
    }

    @Test
    void createFromJsonResponse_withEmptyChoicesArray_throwsIndexOutOfBoundsException() {
        String jsonResponse = """
                {
                    "choices": [],
                    "usage": {
                        "total_tokens": 42
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        assertThrows(IndexOutOfBoundsException.class, () ->
                OpenAiResponse.createFromJsonResponse(parsedJson)
        );
    }

    @Test
    void createFromJsonResponse_withDifferentRole_parsesCorrectly() throws OpenAiException {
        String jsonResponse = """
                {
                    "choices": [{
                        "message": {
                            "role": "user",
                            "content": "Test message"
                        }
                    }],
                    "usage": {
                        "total_tokens": 10
                    }
                }
                """;
        JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        OpenAiResponse response = OpenAiResponse.createFromJsonResponse(parsedJson);

        assertEquals("user", response.role());
    }
}
