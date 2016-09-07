package com.butler.socket;

import com.chat.util.entity.User;
import com.chat.util.json.JsonProtocol;
import org.zeromq.ZMQ;

public class DatabaseSocketHandler implements AutoCloseable {
    private static final String BAD_REPLY = new JsonProtocol<>("", new User()).toString();
    private ZMQ.Socket requester;
    private ZMQ.Poller poller;

    public DatabaseSocketHandler() {
        requester = ZmqContextHolder.getContext().socket(ZMQ.REQ);
        requester.connect(ConnectionProperties.getProperties().getProperty("database_address"));
        poller = new ZMQ.Poller(0);
        poller.register(requester, ZMQ.Poller.POLLIN);
    }

    public void send(String message) {
        requester.send(message);
    }

    public String receive() {
        if (poller.poll(100) > 0) {
            return requester.recvStr();
        }
        return BAD_REPLY;
    }

    @Override
    public void close() {
        requester.close();
    }
}
