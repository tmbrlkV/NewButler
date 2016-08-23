package com.util.json;

import com.chat.util.entity.User;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonProtocolTest {

    @Test
    public void jsonProtocolTest() throws Exception {
        User user = new User(1, "kek", "kek");
        JsonProtocol<User> userJsonProtocol = new JsonProtocol<>("newUser", user);
        userJsonProtocol.setTo("database");
        userJsonProtocol.setFrom(String.valueOf(user.getId()));

        assertEquals(user.getLogin(), userJsonProtocol.getAttachment().getLogin());
        assertEquals(String.valueOf(user.getId()), userJsonProtocol.getFrom());
        assertEquals("database", userJsonProtocol.getTo());

        JsonProtocol<User> protocol = JsonObjectFactory.getObjectFromJson(userJsonProtocol.toString(), JsonProtocol.class);
        assertNotNull(protocol);
        assertTrue(protocol.equals(userJsonProtocol));
    }
}