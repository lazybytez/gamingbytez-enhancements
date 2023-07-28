package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.ChatBotAction;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.LolAction;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.event.ChatBotChatListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatBotFeature extends AbstractFeature {
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
        this.chatBotActions.add(new LolAction());
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
