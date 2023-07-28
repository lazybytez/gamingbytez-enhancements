package de.lazybytez.gamingbytezenhancements.feature.chatbot;

/**
 * A response our chat bot can send into the chat.
 */
public class ChatBotResponse {
    private final boolean omitSender;
    private final String sender;
    private final String message;
    private final ChatBotTarget target;

    public ChatBotResponse(boolean omitSender, String sender, String message, ChatBotTarget target) {
        this.omitSender = omitSender;
        this.sender = sender;
        this.message = message;
        this.target = target;
    }

    public boolean isOmitSender() {
        return omitSender;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public ChatBotTarget getTarget() {
        return target;
    }
}
