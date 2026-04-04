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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiClientTest {
    private static final String API_URL = "https://api.example.com/v1/chat/completions";
    private static final String API_KEY = "test-key";
    private static final String ORGANIZATION = "test-org";
    private static final String MODEL = "gpt-4";
    private static final double TEMPERATURE = 0.7;

    private OpenAiClient createClient() {
        return new OpenAiClient(API_URL, API_KEY, ORGANIZATION, MODEL, TEMPERATURE);
    }

    @Test
    void getRequestJson_withoutSystemPrompt_containsOnlyUserMessage() {
        OpenAiClient client = this.createClient();

        String json = client.getRequestJsonWithSingleMessage("Hello", null, false);
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();

        assertEquals(MODEL, parsed.get("model").getAsString());
        assertEquals(TEMPERATURE, parsed.get("temperature").getAsDouble());

        JsonArray messages = parsed.getAsJsonArray("messages");
        assertEquals(1, messages.size());

        JsonObject userMessage = messages.get(0).getAsJsonObject();
        assertEquals("user", userMessage.get("role").getAsString());
        assertEquals("Hello", userMessage.get("content").getAsString());
    }

    @Test
    void getRequestJson_withSystemPrompt_prependsSystemMessage() {
        OpenAiClient client = this.createClient();

        String json = client.getRequestJsonWithSingleMessage("Hello", "You are a helpful assistant.", false);
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();
        JsonArray messages = parsed.getAsJsonArray("messages");

        assertEquals(2, messages.size());

        JsonObject systemMessage = messages.get(0).getAsJsonObject();
        assertEquals("system", systemMessage.get("role").getAsString());
        assertEquals("You are a helpful assistant.", systemMessage.get("content").getAsString());

        JsonObject userMessage = messages.get(1).getAsJsonObject();
        assertEquals("user", userMessage.get("role").getAsString());
        assertEquals("Hello", userMessage.get("content").getAsString());
    }

    @Test
    void getRequestJson_withSystemPrompt_preservesSpecialCharacters() {
        OpenAiClient client = this.createClient();
        String systemPrompt = "You are a bot.\nUse \"quotes\" and special chars: <>&";

        String json = client.getRequestJsonWithSingleMessage("Test", systemPrompt, false);
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();
        JsonArray messages = parsed.getAsJsonArray("messages");

        assertEquals(systemPrompt, messages.get(0).getAsJsonObject().get("content").getAsString());
    }

    @Test
    void getRequestJson_withDisableThinking_addsChatTemplateKwargs() {
        OpenAiClient client = this.createClient();

        String json = client.getRequestJsonWithSingleMessage("Hello", null, true);
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();

        assertTrue(parsed.has("chat_template_kwargs"));
        JsonObject kwargs = parsed.getAsJsonObject("chat_template_kwargs");
        assertFalse(kwargs.get("enable_thinking").getAsBoolean());
    }

    @Test
    void getRequestJson_withoutDisableThinking_omitsChatTemplateKwargs() {
        OpenAiClient client = this.createClient();

        String json = client.getRequestJsonWithSingleMessage("Hello", null, false);
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();

        assertFalse(parsed.has("chat_template_kwargs"));
    }
}
