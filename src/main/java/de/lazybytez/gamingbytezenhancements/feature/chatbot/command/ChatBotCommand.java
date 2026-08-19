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
package de.lazybytez.gamingbytezenhancements.feature.chatbot.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.messages.ChatBotMessages;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.command.PluginCommand;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;

/**
 * The command managing the Chat Bot feature.
 * <p>
 * Both reload paths leave the running bot answering throughout: the static responses are read
 * off the server thread and swapped in as one snapshot, and the settings reload replaces the AI
 * action in place. A bare reload runs both.
 */
public final class ChatBotCommand implements PluginCommand {
    private static final String ADMIN_PERMISSION = "gamingbytez.chatbot.admin";
    private static final String LABEL = "chatbot";

    private final ChatBotFeature feature;
    private final Messenger messenger;

    /**
     * Bind the command to the feature it manages and the messenger it reports through.
     *
     * @param feature   the feature whose configuration the command reloads
     * @param messenger the messenger delivering the operator feedback
     */
    public ChatBotCommand(ChatBotFeature feature, Messenger messenger) {
        this.feature = Objects.requireNonNull(feature, "feature must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> createNode() {
        return Commands.literal(ChatBotCommand.LABEL)
                .requires(this::canUse)
                .executes(this::sendHelp)
                .then(Commands.literal("reload")
                        .executes(this::reloadEverything)
                        .then(Commands.literal("responses").executes(this::reloadResponses))
                        .then(Commands.literal("settings").executes(this::reloadSettings)));
    }

    @Override
    public String description() {
        return "Manage the Chat Bot of the GamingBytez Enhancements plugin";
    }

    @Override
    public List<String> aliases() {
        return List.of("gbcb");
    }

    @Override
    public String permission() {
        return ChatBotCommand.ADMIN_PERMISSION;
    }

    @Override
    public Messenger messenger() {
        return this.messenger;
    }

    private int reloadEverything(CommandContext<CommandSourceStack> context) {
        this.reloadSettings(context);

        return this.reloadResponses(context);
    }

    private int reloadResponses(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        this.feature.reloadStaticResponses(loaded -> {
            if (!loaded) {
                this.messenger.error(sender, ChatBotMessages.responsesReloadFailed());

                return;
            }

            this.messenger.success(
                    sender,
                    ChatBotMessages.responsesReloaded(this.feature.staticResponseCount())
            );
        });

        return CommandResults.SUCCESS;
    }

    private int reloadSettings(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        if (this.feature.reloadAiSettings()) {
            this.messenger.success(sender, ChatBotMessages.settingsReloadedAiEnabled());

            return CommandResults.SUCCESS;
        }

        this.messenger.success(sender, ChatBotMessages.settingsReloadedAiDisabled());

        return CommandResults.SUCCESS;
    }
}
