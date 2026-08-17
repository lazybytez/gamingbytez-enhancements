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
package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.StaticResponseAction;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticResponseConfigurationTest {
    private static final String VALID_ENTRY_YAML = """
            static_response:
              - responseMessage: "Hello there!"
                buzzwords:
                  - "hello"
                  - "hi"
                numerator: 1
                denominator: 1
            """;

    private static final String MIXED_VALIDITY_YAML = """
            static_response:
              - responseMessage: "Hello there!"
                buzzwords:
                  - "hello"
                numerator: 1
                denominator: 1
              - responseMessage: 123
                buzzwords:
                  - "invalid-message"
                numerator: 1
                denominator: 1
              - responseMessage: "Invalid buzzwords"
                buzzwords: "not-a-list"
                numerator: 1
                denominator: 1
              - responseMessage: "Invalid numerator"
                buzzwords:
                  - "buzz"
                numerator: "not-an-int"
                denominator: 1
              - responseMessage: "Invalid denominator"
                buzzwords:
                  - "buzz"
                numerator: 1
                denominator: "not-an-int"
              - responseMessage: "Second valid entry"
                buzzwords:
                  - "second"
                numerator: 1
                denominator: 1
            """;

    private static final String NO_STATIC_RESPONSE_KEY_YAML = """
            other_key: []
            """;

    private StaticResponseConfiguration configuration;
    private Plugin mockPlugin;
    private Path dataFolder;
    private List<LogRecord> logRecords;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        this.dataFolder = tempDir;
        this.logRecords = new java.util.ArrayList<>();

        Logger logger = Logger.getLogger("StaticResponseConfigurationTest-" + UUID.randomUUID());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.WARNING);
        logger.addHandler(this.recordingHandler());

        this.mockPlugin = mock(Plugin.class);
        when(this.mockPlugin.getDataFolder()).thenReturn(this.dataFolder.toFile());
        when(this.mockPlugin.getLogger()).thenReturn(logger);

        this.configuration = new StaticResponseConfiguration(this.mockPlugin);
    }

    @Test
    void getActions_beforeLoad_logsWarningNamingLoad() {
        this.configuration.getActions();

        assertEquals(1, this.logRecords.size());
        assertTrue(this.logRecords.get(0).getMessage().contains("load"));
        assertFalse(this.logRecords.get(0).getMessage().contains("loadSync"));
    }

    @Test
    void getActions_beforeLoad_returnsEmptyList() {
        List<StaticResponseAction> actions = this.configuration.getActions();

        assertTrue(actions.isEmpty());
    }

    @Test
    void load_withoutStaticResponseKey_logsNoWarningOnGetActions() throws IOException, InvalidConfigurationException {
        this.writeConfigFile(NO_STATIC_RESPONSE_KEY_YAML);

        this.configuration.load();
        this.configuration.getActions();

        assertTrue(this.logRecords.isEmpty());
    }

    @Test
    void load_withValidEntry_yieldsMatchingStaticResponseAction() throws IOException, InvalidConfigurationException {
        this.writeConfigFile(VALID_ENTRY_YAML);

        this.configuration.load();
        List<StaticResponseAction> actions = this.configuration.getActions();

        assertEquals(1, actions.size());
        StaticResponseAction action = actions.get(0);
        assertEquals(
                "Hello there!",
                action.getChatBotMessage("hi there", null, Set.of()).message()
        );
        assertTrue(action.supports("hello there", null, Set.of()));
        assertFalse(action.supports("goodbye", null, Set.of()));
        assertTrue(action.chance());
    }

    @Test
    void load_withInvalidEntries_skipsThemButKeepsValidSiblings() throws IOException, InvalidConfigurationException {
        this.writeConfigFile(MIXED_VALIDITY_YAML);

        this.configuration.load();
        List<StaticResponseAction> actions = this.configuration.getActions();

        assertEquals(2, actions.size());
    }

    @Test
    void getActions_returnsImmutableList() throws IOException, InvalidConfigurationException {
        this.writeConfigFile(VALID_ENTRY_YAML);
        this.configuration.load();

        List<StaticResponseAction> actions = this.configuration.getActions();

        assertThrows(UnsupportedOperationException.class, () -> actions.add(
                new StaticResponseAction("extra", new String[]{}, 1, 1)
        ));
    }

    @Test
    void classFields_containNoConcurrencyMachineryLeftovers() {
        for (Field field : StaticResponseConfiguration.class.getDeclaredFields()) {
            assertNotEquals(CopyOnWriteArrayList.class, field.getType());
            assertNotEquals(org.bukkit.configuration.file.YamlConfiguration.class, field.getType());
        }
    }

    private void writeConfigFile(String yaml) throws IOException {
        Path configFile = this.dataFolder.resolve(StaticResponseConfiguration.STATIC_RESPONSE_CONFIG_FILE);
        Files.writeString(configFile, yaml);
    }

    private Handler recordingHandler() {
        return new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record);
            }

            @Override
            public void flush() {
                // No buffering to flush.
            }

            @Override
            public void close() throws SecurityException {
                // No resources to release.
            }
        };
    }
}
