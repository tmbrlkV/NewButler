package com.butler.command;

import com.butler.socket.DatabaseSocketHandler;
import com.butler.socket.RoomManagerSocketHandler;
import com.butler.socket.SenderSocketHandler;
import com.chat.util.entity.User;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private static Pattern pattern = Pattern.compile("([a-zA-Z]+)(:\\d+){0,2}");
    private static String DEFAULT_REPLY = new JsonProtocol<>("", new User()).toString();
    private SenderSocketHandler sender = new SenderSocketHandler();

    private Map<String, Command> commandMap = new ConcurrentHashMap<String, Command>() {
        {
            put(Command.DATABASE, request -> {
                try (DatabaseSocketHandler handler = new DatabaseSocketHandler()) {
                    handler.send(request);
                    logger.debug("Send to Database {}", request);
                    String reply = handler.receive();
                    logger.debug("Receive from Database {}", request);
                    JsonProtocol protocol = JsonObjectFactory.getObjectFromJson(reply, JsonProtocol.class);
                    return JsonObjectFactory.getJsonString(Optional.ofNullable(protocol).orElse(new JsonProtocol()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DEFAULT_REPLY;
            });


            put(Command.CHAT, request -> {
                sender.send(request);
                logger.debug("Send to Chat {}", request);
                return request;
            });

            put(Command.ROOM_MANAGER, request -> {
                try (RoomManagerSocketHandler handler = new RoomManagerSocketHandler()) {
                    handler.send(request);
                    logger.debug("Send to RoomManager {}", request);
                    String reply = handler.receive();
                    logger.debug("Receive from RoomManager {}", reply);
                    JsonProtocol protocol = JsonObjectFactory.getObjectFromJson(reply, JsonProtocol.class);
                    return JsonObjectFactory.getJsonString(Optional.ofNullable(protocol).orElse(new JsonProtocol()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DEFAULT_REPLY;
            });
        }
    };

    public String execute(String json) {
        JsonProtocol request = JsonObjectFactory.getObjectFromJson(json, JsonProtocol.class);
        Optional<JsonProtocol> protocolOptional = Optional.ofNullable(request);
        String keyTo = protocolOptional.map(JsonProtocol::getTo).orElse(DEFAULT_REPLY);
        Command command = commandMap.getOrDefault(getServiceName(keyTo), r -> DEFAULT_REPLY);

        return command.execute(json);
    }

    private String getServiceName(String keyTo) {
        Matcher matcher = pattern.matcher(keyTo);
        if (matcher.matches()) {
            logger.debug("matches {}", matcher.matches());
            logger.debug("Group {}", matcher.group(1));
            return matcher.group(1);
        }
        return "";
    }
}