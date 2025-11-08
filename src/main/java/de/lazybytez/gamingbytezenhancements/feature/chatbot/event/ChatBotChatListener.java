package de.lazybytez.gamingbytezenhancements.feature.chatbot.event;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.ChatBotAction;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.RandomNameUtility;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Listener that listens for chat messages and runs our custom reactions.
 */
public class ChatBotChatListener implements Listener {
    private final ChatBotFeature chatBotFeature;
    private final ExecutorService executorService;

    public ChatBotChatListener(ChatBotFeature chatBotFeature) {
        this.chatBotFeature = chatBotFeature;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Handle if our chatbot should answer to an incoming message.
     * <p>
     * ATTENTION!
     * We force this event to run asynchronously by enforcing an async execution
     * if the event was not fired asynchronous natively!
     * This means special care should be taken when working with the Bukkit API.
     */
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        // We do not know from which thread this was called exactly.
        // We can check if it was from the main thread using event.isAsynchronous,
        // however this still won't ensure that the event wasn't fired from some thread
        // that doesn't like heavy processing actions.
        // For this reason, we just start our own async thread where we can do what we want
        // (like firing HTTP requests) without interrupting the server's performance.
        this.executorService.execute(() -> this.runChatBotAsync(event));
    }

    private void runChatBotAsync(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Set<Audience> receivers = event.viewers();

        if (!(event.message() instanceof TextComponent messageComponent)) {
            return;
        }

        String message = messageComponent.content();

        ChatBotAction action = this.chooseRandomAction(findSupportedActions(message, sender, receivers));

        if (action == null || !action.chance()) {
            return;
        }

        ChatBotResponse response = action.getChatBotMessage(message, sender, receivers);
        if (response == null) {
            return;
        }

        Component component = this.buildMessage(response);

        this.sendMessage(component, response.target(), sender, receivers);
    }

    private Component buildMessage(ChatBotResponse response) {
        StringBuilder msg = new StringBuilder();

        if (!response.omitSender()) {
            String sender = response.sender() == null || response.sender().isEmpty()
                    ? RandomNameUtility.getRandomName()
                    : response.sender();

            msg.append("<");
            msg.append(sender);
            msg.append("> ");
        }

        msg.append(response.message());

        return Component.text(msg.toString());
    }

    private void sendMessage(Component component, ChatBotTarget target, Player sender, Set<Audience> receivers) {
        // Re-sync just to be sure
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.chatBotFeature.getPlugin(), () -> {
            switch (target) {
                case BROADCAST -> Bukkit.broadcast(component);
                case RECEIVERS -> receivers.forEach(audience -> audience.sendMessage(component));
                case SENDER -> sender.sendMessage(component);
            }
        });
    }

    private List<ChatBotAction> findSupportedActions(String message, Player sender, Set<Audience> receivers) {
        return this.chatBotFeature
                .getChatBotActions()
                .stream()
                .filter(chatBotAction -> chatBotAction.supports(message, sender, receivers))
                .flatMap(chatBotAction -> Collections.nCopies(chatBotAction.weight(), chatBotAction).stream())
                .toList();
    }

    private ChatBotAction chooseRandomAction(List<ChatBotAction> actions) {
        int actionsSize = actions.size();

        if (actionsSize == 0) {
            return null;
        }

        if (actions.size() < 2) {
            return actions.getFirst();
        }

        return actions.get(new Random().nextInt(0, actions.size() - 1));
    }
}
