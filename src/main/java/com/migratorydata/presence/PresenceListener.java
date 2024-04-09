package com.migratorydata.presence;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;
import com.migratorydata.presence.util.FCMPush;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an example of using MigratoryData's presence extension API.
 *
 * @ThreadSafe The methods of this class are always called from the same thread.
 */
public class PresenceListener implements MigratoryDataPresenceListener {
    private PresenceMemoryStorage presenceStorage = new PresenceMemoryStorage();

    public PresenceListener() {
        try {
            FCMPush.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClusterMessage(Message message) {
        System.out.println("Got onClusterMessage=" + message.toString());

        List<User> offlineUsers = presenceStorage.getOfflineUsers(message.getSubject());
        if (offlineUsers.size() > 0) {
            // tokens of offline users
            List<String> tokens = new ArrayList<>();
            for (User user : offlineUsers) {
                tokens.add(user.getExternalToken());
            }

            // title of the push notification (in this example, the tile is the subject)
            String title = message.getSubject();

            // body of the push notification
            String body = new String(message.getContent());

            // send push notification
            try {
                FCMPush.sendPushNotification(tokens, title, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUserPresence(User user) {
        if (user.isOffline()) {
            System.out.println("Got a user disconnection presence update: " + user.toString());
        } else {
            System.out.println("Got a user connection presence update: " + user.toString());
        }

        presenceStorage.update(user);
    }
}
