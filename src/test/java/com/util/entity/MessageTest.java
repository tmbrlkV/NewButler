package com.util.entity;

import com.util.json.JsonObjectFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMessage() throws Exception {
        String login = "kek";
        String content = "message";
        Message<String> message =  new Message<>(login, content);

        assertEquals(login, message.getLogin());
        assertEquals(content, message.getContent());

        Message json = JsonObjectFactory.getObjectFromJson(message.toString(), Message.class);
        assertEquals(message, json);
    }

}