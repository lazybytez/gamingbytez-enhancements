package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.*;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.event.ChatBotChatListener;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatBotFeature extends AbstractFeature {
    public static final String CONFIG_ENABLE_AI_BOT = "chatbot.enable_ai_answers";
    public static final String CONFIG_AI_BOT_PROMPT = "chatbot.prompt";

    private final List<ChatBotAction> chatBotActions = new CopyOnWriteArrayList<>();

    public ChatBotFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerStaticResponseActions();
        this.registerLLMAction();
        this.registerEvents();
    }

    /**
     * Register configured static-response actions
     */
    private void registerStaticResponseActions() {
        try {
            StaticResponseConfiguration configuration = new StaticResponseConfiguration(this.getPlugin());
            configuration.load();
            this.chatBotActions.addAll(configuration.getActions());
        } catch (IOException | InvalidConfigurationException e) {
            this.getPlugin().getLogger().warning("Error while registering Static-Response Chat Bot Actions: " + e.getMessage());
        }
    }

    /**
     * Register LLM Action
     */
    private void registerLLMAction() {
        // Add AI responses - only when enabled
        if (this.getPlugin().getConfig().getBoolean(ChatBotFeature.CONFIG_ENABLE_AI_BOT, false)) {
            String prompt = this.getPlugin().getConfig().getString(ChatBotFeature.CONFIG_AI_BOT_PROMPT);
            if (prompt == null) {
                this.getPlugin().getLogger().warning("AI ChatBot feature is enabled, but no prompt is configured. Prompt will be empty!");
                prompt = "";
            }

            this.chatBotActions.add(new ChatGPTAction(this.getPlugin(), prompt));
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
