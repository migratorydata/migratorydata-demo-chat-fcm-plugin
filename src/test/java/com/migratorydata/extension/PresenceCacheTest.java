package com.migratorydata.extension;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;

import com.migratorydata.presence.PresenceCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PresenceCacheTest {

    String subject = "/s/s";
    List<String> subjects = Arrays.asList(subject);

    @Test
    public void test() {
        PresenceCache storage = new PresenceCache();

        MigratoryDataPresenceListener.Message message = new MessageStub(subject, "data".getBytes());

        storage.update(new UserStub(subjects, "extToken", 1, false, 1));

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 0);

        storage.update(new UserStub(subjects, "extToken", 1, true, 1));

        offlineUsers = storage.getOfflineUsersBySubject(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 1);
    }

}
