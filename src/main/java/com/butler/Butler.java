package com.butler;

import com.butler.command.CommandManager;
import com.butler.socket.ConnectionProperties;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;
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
            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

            pull.bind("tcp://*:14000");
            publisher.bind("tcp://*:14001");

            subscriber.connect(ConnectionProperties.getProperties().getProperty("chat_receiver_address"));
            subscriber.subscribe("".getBytes());
            ZMQ.Poller poller = new ZMQ.Poller(0);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    int events = poller.poll();
                    if (events > 0) {
                        String reply = subscriber.recvStr();
                        JsonProtocol jsonMessage = JsonObjectFactory.getObjectFromJson(reply, JsonProtocol.class);
                        if (jsonMessage != null) {
                            publisher.sendMore(jsonMessage.getTo());
                            publisher.send(reply);
                        }
                    }
                }
            }).start();


            CommandManager manager = new CommandManager();

            while (!Thread.currentThread().isInterrupted()) {
                String message = pull.recvStr();
                logger.debug("Received {}", message);
                String execute = manager.execute(message);
                logger.debug("Executed {}", execute);
                JsonProtocol objectFromJson = JsonObjectFactory.getObjectFromJson(execute, JsonProtocol.class);
                String data = Optional.ofNullable(objectFromJson).map(JsonProtocol::getFrom).orElseGet(() -> "");
                logger.debug("Data {}", data);
                if (data.equals("database")) {
                    publisher.sendMore(data);
                    logger.debug("From database: {}", execute);
                    publisher.send(execute);
                } else {
                    publisher.sendMore("roomManager");
                    logger.debug("From roomManager: {}", execute);
                    publisher.send(execute);
                }
            }
        }
    }
}
