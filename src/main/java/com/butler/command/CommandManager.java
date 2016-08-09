package com.butler.command;

import com.butler.socket.DatabaseSocketHandler;
import com.util.entity.User;
import com.util.json.JsonMessage;
import com.util.json.JsonObject;
import com.util.json.JsonObjectFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private String DEFAULT_REPLY = "";

    private Map<String, Command> commandMap = new ConcurrentHashMap<String, Command>() {{
        Command databaseCommand = request -> {
            try (DatabaseSocketHandler handler = new DatabaseSocketHandler()) {
                handler.send(request);
                String reply = handler.receive();
                User user = JsonObjectFactory.getObjectFromJson(reply, User.class);
                return JsonObjectFactory.getJsonString(Optional.ofNullable(user).orElse(new User()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return DEFAULT_REPLY;
        };
        put(Command.GET_USER_BY_LOGIN_PASSWORD, databaseCommand);
        put(Command.GET_USER_BY_LOGIN, databaseCommand);
        put(Command.NEW_USER, databaseCommand);
        put(Command.MESSAGE, request -> request);
    }};

    public String execute(String json) {
        JsonObject databaseRequest = JsonObjectFactory.getObjectFromJson(json, JsonObject.class);
        JsonMessage message = JsonObjectFactory.getObjectFromJson(json, JsonMessage.class);

        Optional<JsonMessage> messageOptional = Optional.ofNullable(message);
        Optional<JsonObject> databaseRequestOptional = Optional.ofNullable(databaseRequest);

        String stringCommand = databaseRequestOptional.map(JsonObject::getCommand)
                .orElseGet(() -> messageOptional.map(JsonMessage::getCommand).orElse(Command.NO_COMMAND));

        Command command = commandMap.getOrDefault(stringCommand, request -> Command.NO_COMMAND);
        String jsonString = databaseRequestOptional.map(JsonObjectFactory::getJsonString)
                .orElseGet(() -> messageOptional.map(JsonObjectFactory::getJsonString).orElse(Command.NO_COMMAND));
        return command.execute(jsonString);
    }
}