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
