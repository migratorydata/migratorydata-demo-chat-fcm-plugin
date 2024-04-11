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

    //c1 ("/s/s", "ext", 1, false, 1)
    //c1_new_subjects ("/t/t", "ext", 1, false, 1)
    //d1 ("/s/s", "ext", 1, true, 1)
    //c2 ("/s/s", "ext", 2, false, 2)
    //d1 ("/s/s", "ext", 2, true, 2)

    MigratoryDataPresenceListener.User userC1 = new UserStub(subjects, "extToken", 1,false, 1);
    MigratoryDataPresenceListener.User userC1NewSubjects = new UserStub(Arrays.asList("/t/t"), "extToken", 1,false, 1);

    MigratoryDataPresenceListener.User userD1 = new UserStub(subjects, "extToken", 1,true, 1);
    MigratoryDataPresenceListener.User userC2 = new UserStub(subjects, "extToken", 2,false, 2);
    MigratoryDataPresenceListener.User userD2 = new UserStub(subjects, "extToken", 2,true, 2);

    @Test
    public void test_update_with_c1_d1_c2_d2() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userD1);
        storage.update(userC2);
        storage.update(userD2);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c1_c2_d1_d2() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userC2);
        storage.update(userD1);
        storage.update(userD2);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c1_c2_d2_d1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userC2);
        storage.update(userD2);
        storage.update(userD1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c2_c1_d2_d1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC2);
        storage.update(userC1);
        storage.update(userD2);
        storage.update(userD1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c1_c2_d1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userC2);
        storage.update(userD1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 0);
    }

    @Test
    public void test_update_with_c2_c1_d1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC2);
        storage.update(userC1);
        storage.update(userD1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 0);
    }

    @Test
    public void test_update_with_c2_c1_d1_d2() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC2);
        storage.update(userC1);
        storage.update(userD1);
        storage.update(userD2);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c2_c1_d2() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC2);
        storage.update(userC1);
        storage.update(userD2);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c1_d2_c2_d1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userD2);
        storage.update(userC2);
        storage.update(userD1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_d1_c1() {
        PresenceCache storage = new PresenceCache();

        storage.update(userD1);
        storage.update(userC1);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 1);
    }

    @Test
    public void test_update_with_c1_c1_new_subject() {
        PresenceCache storage = new PresenceCache();

        storage.update(userC1);
        storage.update(userC1NewSubjects);

        List<MigratoryDataPresenceListener.User> offlineUsers = storage.getOfflineUsersBySubject(subject);
        Assert.assertTrue(offlineUsers.size() == 0);

        MigratoryDataPresenceListener.User user = storage.getUserByToken(userC1NewSubjects.getExternalToken());
        Assert.assertTrue(user.getSubjects().get(0).equals(userC1NewSubjects.getSubjects().get(0)));
    }
}
