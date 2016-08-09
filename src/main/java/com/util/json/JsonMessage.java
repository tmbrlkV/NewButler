package com.util.json;

public class JsonMessage {
    private String command;
    private String username;
    private String content;

    private String to;
    private String from;

    public JsonMessage() {
    }

    public JsonMessage(String command, String username, String content) {
        this.command = command;
        this.username = username;
        this.content = content;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCommand() {
        return command;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }


}
