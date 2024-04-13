package com.migratorydata.extension;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;

import com.migratorydata.presence.PresenceCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PresenceCacheTest {

    String subject1 = "/s/s";
    String subject2 = "/s/d";
    List<String> subjects = Arrays.asList(subject1, subject2);

    @Test
    public void test() {
        PresenceCache storage = new PresenceCache();

        MigratoryDataPresenceListener.Message message = new MessageStub(subject1, "data".getBytes());

        storage.update(new UserStub(subjects, "extToken", false));

        List<String> offlineUsers = storage.getOfflineUserTokens(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 0);

        storage.update(new UserStub(subjects, "extToken", true));

        offlineUsers = storage.getOfflineUserTokens(message.getSubject());
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_unsubscribe() {
        PresenceCache storage = new PresenceCache();

        MigratoryDataPresenceListener.Message message1 = new MessageStub(subject1, "data".getBytes());

        MigratoryDataPresenceListener.Message message2 = new MessageStub(subject2, "data".getBytes());

        storage.update(new UserStub(subjects, "extToken",  true));

        List<String> offlineUsers1 = storage.getOfflineUserTokens(message1.getSubject());
        List<String> offlineUsers2 = storage.getOfflineUserTokens(message2.getSubject());

        Assert.assertTrue(offlineUsers1.size() == 1);
        Assert.assertTrue(offlineUsers2.size() == 1);

        storage.update(new UserStub(Arrays.asList(subject1), "extToken",  true));

        offlineUsers1 = storage.getOfflineUserTokens(message1.getSubject());
        Assert.assertTrue(offlineUsers1.size() == 1);

        offlineUsers2 = storage.getOfflineUserTokens(message2.getSubject());
        Assert.assertTrue(offlineUsers2.size() == 0);
    }

    @Test
    public void test_unsubscribe_2() {
        PresenceCache storage = new PresenceCache();

        String s1 = "/s/1";
        String s2 = "/s/2";
        String s3 = "/s/3";
        String s4 = "/s/4";
        List<String> subjects = Arrays.asList(s1, s2, s3);

        MigratoryDataPresenceListener.Message message1 = new MessageStub(s1, "data".getBytes());
        MigratoryDataPresenceListener.Message message2 = new MessageStub(s2, "data".getBytes());
        MigratoryDataPresenceListener.Message message3 = new MessageStub(s3, "data".getBytes());
        MigratoryDataPresenceListener.Message message4 = new MessageStub(s4, "data".getBytes());

        storage.update(new UserStub(subjects, "extToken",  true));

        List<String> offlineUsers1 = storage.getOfflineUserTokens(message1.getSubject());
        List<String> offlineUsers2 = storage.getOfflineUserTokens(message2.getSubject());
        List<String> offlineUsers3 = storage.getOfflineUserTokens(message3.getSubject());
        List<String> offlineUsers4 = storage.getOfflineUserTokens(message4.getSubject());

        Assert.assertTrue(offlineUsers1.size() == 1);
        Assert.assertTrue(offlineUsers2.size() == 1);
        Assert.assertTrue(offlineUsers3.size() == 1);
        Assert.assertTrue(offlineUsers4.size() == 0);

        storage.update(new UserStub(Arrays.asList(s1,s2,s4), "extToken",  true));

        offlineUsers1 = storage.getOfflineUserTokens(message1.getSubject());
        offlineUsers2 = storage.getOfflineUserTokens(message2.getSubject());
        offlineUsers3 = storage.getOfflineUserTokens(message3.getSubject());
        offlineUsers4 = storage.getOfflineUserTokens(message4.getSubject());


        Assert.assertTrue(offlineUsers1.size() == 1);
        Assert.assertTrue(offlineUsers2.size() == 1);
        Assert.assertTrue(offlineUsers3.size() == 0);
        Assert.assertTrue(offlineUsers4.size() == 1);
    }


}
