package com.butler.command;

import com.butler.socket.DatabaseSocketHandler;
import com.butler.socket.SenderSocketHandler;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandManager {
    private static Pattern pattern = Pattern.compile("([a-zA-Z]+)(:\\d+){0,2}");
    private static String DEFAULT_REPLY = "";
    private SenderSocketHandler sender = new SenderSocketHandler();

    private Map<String, Command> commandMap = new ConcurrentHashMap<String, Command>() {
        {
            put(Command.DATABASE, request -> {
                try (DatabaseSocketHandler handler = new DatabaseSocketHandler()) {
                    handler.send(request);
                    String reply = handler.receive();
                    JsonProtocol protocol = JsonObjectFactory.getObjectFromJson(reply, JsonProtocol.class);
                    return JsonObjectFactory.getJsonString(Optional.ofNullable(protocol).orElse(new JsonProtocol()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DEFAULT_REPLY;
            });


            put(Command.CHAT, request -> {
                sender.send(request);
                return request;
            });
        }
    };

    public String execute(String json) {
        JsonProtocol request = JsonObjectFactory.getObjectFromJson(json, JsonProtocol.class);
        Optional<JsonProtocol> protocolOptional = Optional.ofNullable(request);
        String keyTo = protocolOptional.map(JsonProtocol::getTo).orElse(DEFAULT_REPLY);
        Command command = commandMap.getOrDefault(getServiceName(keyTo), r -> Command.NO_COMMAND);

        return command.execute(json);
    }

    private String getServiceName(String keyTo) {
        Matcher matcher = pattern.matcher(keyTo);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }
}