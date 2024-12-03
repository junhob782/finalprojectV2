package com.cookandroid.finalprojectv2;

public class ChatMessage {
    private String message;
    private boolean isUserMessage; // true: 사용자 메시지, false: GPT 메시지

    public ChatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}
