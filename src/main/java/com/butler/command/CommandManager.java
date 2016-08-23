package com.butler.command;

import com.butler.socket.DatabaseSocketHandler;
import com.butler.socket.SenderSocketHandler;
import com.util.entity.User;
import com.util.json.JsonObjectFactory;
import com.util.json.JsonProtocol;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private SenderSocketHandler sender = new SenderSocketHandler();
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
        put(Command.MESSAGE, request -> {
            sender.send(request);
            return request;
        });
    }};

    public String execute(String json) {
        JsonProtocol request = JsonObjectFactory.getObjectFromJson(json, JsonProtocol.class);
        Optional<JsonProtocol> protocolOptional = Optional.ofNullable(request);
        String commandName = protocolOptional.map(JsonProtocol::getCommand).orElse("");
        Command command = commandMap.getOrDefault(commandName, r -> Command.NO_COMMAND);
        json = protocolOptional.map(JsonObjectFactory::getJsonString).orElse("");

        return command.execute(json);
    }
}
