package com.util;

import com.butler.command.CommandManager;
import com.chat.util.entity.Message;
import com.chat.util.entity.User;
import com.chat.util.json.JsonProtocol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommandTest {

    @Test
    public void commandTestDatabase() throws Exception {
        User user = new User("kek", "kek");
        JsonProtocol<User> jsonProtocol = new JsonProtocol<>("getUserByLoginPassword", user);
        jsonProtocol.setFrom("");
        jsonProtocol.setTo("database");
        CommandManager manager = new CommandManager();

        String execute = manager.execute(jsonProtocol.toString());
        System.out.println("Reply:" + execute);
        assertNotNull(execute);
    }

    @Test
    public void commandTestMessage() throws Exception {
        Message<String> message = new Message<>("kek", "content");
        JsonProtocol<Message<String>> jsonProtocol = new JsonProtocol<>("message", message);
        jsonProtocol.setFrom("1");
        jsonProtocol.setTo("chat:12");
        CommandManager manager = new CommandManager();

        String execute = manager.execute(jsonProtocol.toString());
        System.out.println("Reply:" + execute);
        assertEquals(jsonProtocol.toString(), execute);

        jsonProtocol.setTo("chat:2:48");
        execute = manager.execute(jsonProtocol.toString());
        assertEquals(jsonProtocol.toString(), execute);
    }

    @Test
    public void commandTestPoop() throws Exception {
        User user = new User("poop", "poop");
        JsonProtocol<User> jsonProtocol = new JsonProtocol<>("poop", user);
        jsonProtocol.setFrom("poop");
        jsonProtocol.setTo("poop");
        CommandManager manager = new CommandManager();

        String execute = manager.execute(jsonProtocol.toString());
        System.out.println("Poop:" + execute);
        assertNotNull(execute);

        jsonProtocol.setTo("chat:2:43poop");
        execute = manager.execute(jsonProtocol.toString());
        assertEquals("", execute);
    }
}
