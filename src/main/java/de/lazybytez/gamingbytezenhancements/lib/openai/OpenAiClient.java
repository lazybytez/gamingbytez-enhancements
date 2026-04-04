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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Simple client to use the OpenAI API.
 */
public class OpenAiClient {
    public static final String HTTP_METHOD = "POST";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_BEARER_TEMPLATE = "Bearer %s";
    public static final String HEADER_ORGANIZATION = "OpenAI-Organization";

    public static final String BODY_MODEL = "model";
    public static final String BODY_MESSAGES = "messages";

    public static final String BODY_TEMPERATURE = "temperature";
    public static final String BODY_CHAT_TEMPLATE_KWARGS = "chat_template_kwargs";
    public static final String BODY_ENABLE_THINKING = "enable_thinking";

    public static final String MESSAGE_ROLE = "role";
    public static final String MESSAGE_ROLE_USER = "user";
    public static final String MESSAGE_ROLE_SYSTEM = "system";

    public static final String MESSAGE_CONTENT = "content";

    private final String apiUrl;
    private final String apiKey;
    private final String organization;
    private final double temperature;

    private final String model;

    /**
     * Creates a new OpenAI client.
     *
     * @param apiUrl       the API endpoint URL
     * @param apiKey       the API key for authentication
     * @param organization the organization ID
     * @param model        the model identifier to use
     * @param temperature  the sampling temperature
     */
    public OpenAiClient(String apiUrl, String apiKey, String organization, String model, double temperature) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.organization = organization;
        this.model = model;
        this.temperature = temperature;
    }

    @NotNull
    private static String getResponseBodyAsString(HttpURLConnection httpURLConnection) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        httpURLConnection.getInputStream(),
                        StandardCharsets.UTF_8
                ))
        ) {
            for (int c; (c = bufferedReader.read()) >= 0; ) {
                stringBuilder.append((char) c);
            }

        } catch (IOException e) {
            // Fallback to error stream and if that fails, fallback to outer catch block
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            for (int c; (c = bufferedReader.read()) >= 0; ) {
                stringBuilder.append((char) c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Sends a chat completion request.
     *
     * @param inputMessage    the user message content
     * @param systemPrompt    optional system prompt; {@code null} to omit from the request
     * @param disableThinking when {@code true}, adds {@code chat_template_kwargs} to disable model thinking
     * @return the parsed API response
     * @throws IOException     on network errors
     * @throws OpenAiException on API errors
     */
    public OpenAiResponse completion(
            String inputMessage,
            String systemPrompt,
            boolean disableThinking
    ) throws IOException, OpenAiException {
        String body = this.getRequestJsonWithSingleMessage(inputMessage, systemPrompt, disableThinking);
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection httpURLConnection = getHttpURLConnection(bodyBytes);
        httpURLConnection.getOutputStream().write(bodyBytes);

        String responseBody = getResponseBodyAsString(httpURLConnection);

        int statusCode = httpURLConnection.getResponseCode();
        JsonObject parsedBody;
        try {
            parsedBody = JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException e) {
            parsedBody = new JsonObject();

            JsonObject errorObject = new JsonObject();
            errorObject.addProperty(
                    OpenAiError.MESSAGE,
                    "Could not parse JSON body (Status: " + statusCode + "): " + e.getMessage()
            );
            errorObject.addProperty(OpenAiError.CODE, "json_parse_failed");

            parsedBody.add(OpenAiError.ERROR, errorObject);
        }

        OpenAiException possibleException = OpenAiException.createFromResponse(parsedBody, statusCode);
        if (possibleException != null) {
            throw possibleException;
        }

        return OpenAiResponse.createFromJsonResponse(parsedBody);
    }

    @NotNull
    private HttpURLConnection getHttpURLConnection(byte[] bodyBytes) throws IOException {
        URL url = new URL(this.apiUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(HTTP_METHOD);

        httpURLConnection.addRequestProperty(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_JSON);
        httpURLConnection.addRequestProperty(
                HEADER_AUTHORIZATION,
                String.format(AUTHORIZATION_BEARER_TEMPLATE, this.apiKey)
        );
        httpURLConnection.addRequestProperty(HEADER_ORGANIZATION, this.organization);
        httpURLConnection.addRequestProperty(
                HEADER_CONTENT_LENGTH,
                String.valueOf(bodyBytes.length)
        );

        httpURLConnection.setDoOutput(true);
        return httpURLConnection;
    }

    String getRequestJsonWithSingleMessage(
            String message,
            String systemPrompt,
            boolean disableThinking
    ) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(BODY_MODEL, this.model);
        jsonObject.add(BODY_MESSAGES, this.getMessageElement(message, systemPrompt));
        jsonObject.addProperty(BODY_TEMPERATURE, this.temperature);

        if (disableThinking) {
            JsonObject chatTemplateKwargs = new JsonObject();
            chatTemplateKwargs.addProperty(BODY_ENABLE_THINKING, false);
            jsonObject.add(BODY_CHAT_TEMPLATE_KWARGS, chatTemplateKwargs);
        }

        return jsonObject.toString();
    }

    private JsonArray getMessageElement(String message, String systemPrompt) {
        JsonArray jsonArray = new JsonArray();

        if (systemPrompt != null) {
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty(MESSAGE_ROLE, MESSAGE_ROLE_SYSTEM);
            systemMessage.addProperty(MESSAGE_CONTENT, systemPrompt);
            jsonArray.add(systemMessage);
        }

        JsonObject messageObject = new JsonObject();
        messageObject.addProperty(MESSAGE_ROLE, MESSAGE_ROLE_USER);
        messageObject.addProperty(MESSAGE_CONTENT, message);
        jsonArray.add(messageObject);

        return jsonArray;
    }
}
