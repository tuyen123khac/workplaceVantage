package com.tuyenvo.tmavantage.models;

import java.io.Serializable;

public class ConversationItem implements Serializable {
    private int image;
    private String conversationName, conversationContent;

    public ConversationItem(int image, String conversationName, String conversationContent) {
        this.image = image;
        this.conversationName = conversationName;
        this.conversationContent = conversationContent;
    }

    public int getImage() {
        return image;
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getConversationContent() {
        return conversationContent;
    }
}
