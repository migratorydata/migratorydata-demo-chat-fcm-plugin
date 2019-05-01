package com.migratorydata.extension;

import com.migratorydata.presence.PresenceListener;
import org.junit.Test;

import java.util.Arrays;

public class PresenceListenerTest {

    @Test
    public void test() {
        PresenceListener listener = new PresenceListener();

        MigratoryDataPresenceListener.Message message = new MessageStub("/s/s", "data".getBytes());

        listener.onUserPresence(new UserStub(Arrays.asList("/s/s"), "extToken", false));
        listener.onClusterMessage(message);

        listener.onUserPresence(new UserStub(Arrays.asList("/s/s"), "extToken", true));
        listener.onClusterMessage(message);
    }

}
