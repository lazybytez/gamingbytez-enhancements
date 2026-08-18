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
package de.lazybytez.gamingbytezenhancements.lib.command;

import com.mojang.brigadier.tree.CommandNode;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;

/**
 * The help listing of a command, read from the command tree itself.
 *
 * Rendering from the live tree is what keeps help and grammar from drifting
 * apart, and taking the label from the invoked node means an operator who typed
 * an alias reads that alias back rather than the canonical name.
 *
 * Nodes are compared by identity throughout, because Brigadier compares command
 * nodes structurally and two distinct branches can therefore look equal.
 */
public final class CommandHelp {
    private CommandHelp() {
    }

    /**
     * Send a heading naming the invoked command, then one line per usable
     * executable branch below it. A command without a single usable branch
     * sends nothing, because a heading over an empty list reads as a glitch.
     * <p>
     * The heading carries the label the operator actually typed, so an alias
     * reads back as itself. It is the label rather than prose, because this
     * class lives in the lib and the lib carries no wording.
     *
     * @param source      the source the command was invoked from
     * @param messenger   the vocabulary the lines are rendered in
     * @param invokedNode the node the operator invoked, alias included
     */
    public static void send(
            CommandSourceStack source,
            Messenger messenger,
            CommandNode<CommandSourceStack> invokedNode
    ) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(messenger, "messenger must not be null");
        Objects.requireNonNull(invokedNode, "invokedNode must not be null");

        List<String> branches = new ArrayList<>();
        collectBranches(source, followRedirects(invokedNode), "/" + invokedNode.getName(), branches, newNodeSet());

        if (branches.isEmpty()) {
            return;
        }

        messenger.heading(source.getSender(), "/" + invokedNode.getName());

        for (String branch : branches) {
            messenger.detail(source.getSender(), Component.text(branch));
        }
    }

    private static void collectBranches(
            CommandSourceStack source,
            CommandNode<CommandSourceStack> node,
            String path,
            List<String> branches,
            Set<CommandNode<CommandSourceStack>> walked
    ) {
        if (!walked.add(node)) {
            return;
        }

        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            if (!child.canUse(source)) {
                continue;
            }

            String childPath = path + " " + child.getUsageText();

            if (child.getCommand() != null) {
                branches.add(childPath);
            }

            collectBranches(source, followRedirects(child), childPath, branches, walked);
        }

        walked.remove(node);
    }

    private static CommandNode<CommandSourceStack> followRedirects(CommandNode<CommandSourceStack> node) {
        Set<CommandNode<CommandSourceStack>> seen = newNodeSet();
        CommandNode<CommandSourceStack> current = node;

        while (current.getRedirect() != null && seen.add(current)) {
            current = current.getRedirect();
        }

        return current;
    }

    private static Set<CommandNode<CommandSourceStack>> newNodeSet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
