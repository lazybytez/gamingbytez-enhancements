package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.*;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.event.ChatBotChatListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatBotFeature extends AbstractFeature {
    public static final String CONFIG_ENABLE_AI_BOT = "chatbot.enable_ai_answers";

    private final List<ChatBotAction> chatBotActions = new CopyOnWriteArrayList<>();

    public ChatBotFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerChatBotActions();
        this.registerEvents();
    }

    private void registerChatBotActions() {
        // Add common responses
        this.chatBotActions.add(new BuntstifteAction());
        this.chatBotActions.add(new EgalAction());
        this.chatBotActions.add(new GlaubenAction());
        this.chatBotActions.add(new HalloAction());
        this.chatBotActions.add(new KannstAction());
        this.chatBotActions.add(new KlaerenAction());
        this.chatBotActions.add(new LolAction());
        this.chatBotActions.add(new MeinungAction());

        // Add AI responses - only when enabled
        if (this.getPlugin().getConfig().getBoolean(ChatBotFeature.CONFIG_ENABLE_AI_BOT, false)) {
            this.chatBotActions.add(new ChatGPTAction(this.getPlugin()));
        }
    }

    private void registerEvents() {
        this.registerEvent(new ChatBotChatListener(this));
    }

    public List<ChatBotAction> getChatBotActions() {
        return this.chatBotActions;
    }

    @Override
    public String getName() {
        return "ChatBot";
    }
}
