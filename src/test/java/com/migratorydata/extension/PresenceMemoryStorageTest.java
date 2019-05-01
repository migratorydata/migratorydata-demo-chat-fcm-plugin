package com.migratorydata.extension;

import com.migratorydata.presence.PresenceMemoryStorage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PresenceMemoryStorageTest {

    @Test
    public void test() {
        PresenceMemoryStorage storage = new PresenceMemoryStorage();

        MigratoryDataPresenceListener.Message message = new MessageStub("/s/s", "data".getBytes());

        storage.update(new UserStub(Arrays.asList("/s/s"), "extToken", false));

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsers(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 0);

        storage.update(new UserStub(Arrays.asList("/s/s"), "extToken", true));

        offlineUsers = storage.getOfflineUsers(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 1);
    }
}
