package de.lazybytez.gamingbytezenhancements.feature.chatbot;

/**
 * A response our chat bot can send into the chat.
 */
public record ChatBotResponse(boolean omitSender, String sender, String message, ChatBotTarget target) {
}
