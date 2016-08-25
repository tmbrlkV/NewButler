package com.butler.command;

@FunctionalInterface
interface Command {
    String NO_COMMAND = "";
    String CHAT = "chat";
    String DATABASE = "database";
    String ROOM_MANAGER = "roomManager";

    String execute(String request);
}
