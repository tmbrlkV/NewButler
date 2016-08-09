package com.butler;

import com.butler.command.CommandManager;
import com.util.json.JsonMessage;
import com.util.json.JsonObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.util.Optional;

public class Butler {

    private static Logger logger = LoggerFactory.getLogger(Butler.class);

    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket pull = context.socket(ZMQ.PULL);
            ZMQ.Socket publisher = context.socket(ZMQ.PUB);

            pull.bind("tcp://*:14000");
            publisher.bind("tcp://*:14001");

            CommandManager manager = new CommandManager();

            while (!Thread.currentThread().isInterrupted()) {
                String message = pull.recvStr();
                logger.debug(message);
                String execute = manager.execute(message);
                logger.debug(execute);
                JsonMessage objectFromJson = JsonObjectFactory.getObjectFromJson(message, JsonMessage.class);
                String data = Optional.ofNullable(objectFromJson).map(JsonMessage::getFrom).orElseGet(() -> "");
                logger.debug(data);
                publisher.sendMore("1");
                publisher.send(execute);
            }
        }
    }
}