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

/**
 * Exception that can be thrown when OpenAI API requests fail.
 */
public class OpenAiException extends Exception {
    private final int statusCode;

    private final String errorCode;

    /**
     * The most of a response body a log line carries, so an HTML error page cannot fill the log.
     */
    private static final int MAX_QUOTED_BODY = 500;

    public OpenAiException(String message, int statusCode, String errorCode) {
        super(message);

        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    /**
     * Create an error from some OpenAI API response JSON.
     * <p>
     * The status decides whether the call failed, not the shape of the body. Only OpenAI itself
     * wraps a failure in an {@code error} object, while other servers behind the same API report one
     * at the top level or in prose, and reading the shape alone let those answers reach the success
     * parser and fail there as a missing field.
     * <p>
     * A body that says nothing recognisable is quoted back rather than summarised, because whatever
     * the server did say is the only description of the failure anyone has.
     *
     * @param parsedBody the parsed response body
     * @param statusCode the HTTP status the response carried
     * @param rawBody    the response body as it arrived, quoted when nothing else is recognisable
     * @return the failure the response describes, or null when the response is a success
     */
    public static OpenAiException createFromResponse(JsonObject parsedBody, int statusCode, String rawBody) {
        boolean failedStatus = statusCode < 200 || statusCode > 299;

        if (!parsedBody.has(OpenAiError.ERROR) && !failedStatus) {
            return null;
        }

        if (!parsedBody.has(OpenAiError.ERROR)) {
            return new OpenAiException(
                    "Request failed with status " + statusCode + ": " + OpenAiException.quote(rawBody),
                    statusCode,
                    "http_" + statusCode);
        }

        try {
            JsonObject error = parsedBody.get(OpenAiError.ERROR).getAsJsonObject();
            String errorMessage = error.get(OpenAiError.MESSAGE).getAsString();
            String errorCode = error.has(OpenAiError.CODE) && !error.get(OpenAiError.CODE).isJsonNull()
                    ? error.get(OpenAiError.CODE).getAsString()
                    : "http_" + statusCode;

            return new OpenAiException(errorMessage, statusCode, errorCode);
        } catch (IllegalStateException | NullPointerException e) {
            return new OpenAiException(
                    "Unreadable error body (status " + statusCode + "): " + OpenAiException.quote(rawBody),
                    statusCode,
                    "failed_to_parse_json");
        }
    }

    /**
     * Shorten a body for a log line, since a server may answer with an entire HTML page.
     *
     * @param rawBody the body as it arrived
     * @return the body, cut to a length a log line can carry
     */
    private static String quote(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            return "<empty body>";
        }

        String collapsed = rawBody.strip().replaceAll("\\s+", " ");

        if (collapsed.length() <= OpenAiException.MAX_QUOTED_BODY) {
            return collapsed;
        }

        return collapsed.substring(0, OpenAiException.MAX_QUOTED_BODY) + "...";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
