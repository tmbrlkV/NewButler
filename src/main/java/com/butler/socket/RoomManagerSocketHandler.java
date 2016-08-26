package com.butler.socket;

import org.zeromq.ZMQ;

public class RoomManagerSocketHandler implements AutoCloseable {
    private ZMQ.Socket requester;

    public RoomManagerSocketHandler() {
        requester = ZmqContextHolder.getContext().socket(ZMQ.REQ);
        requester.connect(ConnectionProperties.getProperties().getProperty("room_manager_address"));
    }

    public void send(String message) {
        requester.send(message);
    }

    public String receive() {
        return requester.recvStr();
    }

    @Override
    public void close() {
        requester.close();
    }
}
