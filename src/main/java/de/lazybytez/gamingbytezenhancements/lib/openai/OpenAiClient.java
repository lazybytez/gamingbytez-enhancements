package de.lazybytez.gamingbytezenhancements.lib.openai;

import com.google.gson.*;
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

    public static final String MESSAGE_ROLE = "role";
    public static final String MESSAGE_ROLE_USER = "user";

    public static final String MESSAGE_CONTENT = "content";

    private final String apiUrl;
    private final String apiKey;
    private final String organization;
    private final double temperature;

    private final String model;

    public OpenAiClient(String apiUrl, String apiKey, String organization, String model, double temperature) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.organization = organization;
        this.model = model;
        this.temperature = temperature;
    }

    public OpenAiResponse completion(String inputMessage) throws IOException, OpenAiException {
        String body = this.getRequestJsonWithSingleMessage(inputMessage);
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection httpURLConnection = getHttpURLConnection(bodyBytes);
        httpURLConnection.getOutputStream().write(bodyBytes);

        String responseBody = getResponseBodyAsString(httpURLConnection);

        int statusCode = httpURLConnection.getResponseCode();
        JsonObject parsedBody;
        try {
            parsedBody = JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (JsonSyntaxException|IllegalStateException e) {
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

    private String getRequestJsonWithSingleMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(BODY_MODEL, this.model);
        jsonObject.add(BODY_MESSAGES, getMessageElement(message));
        jsonObject.addProperty(BODY_TEMPERATURE, this.temperature);


        return jsonObject.toString();
    }

    private JsonArray getMessageElement(String message) {
        JsonArray jsonArray = new JsonArray();

        JsonObject messageObject = new JsonObject();
        messageObject.addProperty(MESSAGE_ROLE, MESSAGE_ROLE_USER);
        messageObject.addProperty(MESSAGE_CONTENT, message);

        jsonArray.add(messageObject);

        return jsonArray;
    }
}
