package com.migratorydata.presence;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener;
import com.migratorydata.presence.util.FCMPushNotificationSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates how to use MigratoryData's presence extension API to send push notifications to users who
 * are offline, using Firebase Cloud Messaging (FCM).
 *
 * <p>For more information on MigratoryData's presence extension API, refer to the MigratoryData documentation.
 *
 * @ThreadSafe The methods of this class are always called from the same thread.
 */
public class PresenceListener implements MigratoryDataPresenceListener {
    private PresenceCache presenceCache = new PresenceCache(); // a cache to store presence information

    /**
     * Initializes the FCM client for sending push notifications.
     *
     * @throws RuntimeException If an error occurs during the FCM client initialization.
     */
    public PresenceListener() {
        try {
            FCMPushNotificationSender.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback function triggered whenever a user connects, disconnects, or updates its subscribed subjects (by
     * subscribing to or unsubscribing from them).
     *
     * <p>
     * <b>Important note:</b> This method is called by all members of the MigratoryData cluster, not just by the cluster
     * member to which the user is connected.
     *
     * @param user The user whose presence status has changed
     */
    @Override
    public void onUserPresence(User user) {
        System.out.println("User presence event detected: " + user);

        presenceCache.update(user);
    }

    /**
     * Callback method invoked whenever a message is received by the MigratoryData cluster.
     * <p>
     * <b>Important note:</b> This method is invoked by exactly one member within the MigratoryData cluster; consider
     * this member as randomly chosen (even if the selection is based on the message's subject).
     *
     * @param message The received message.
     */
    @Override
    public void onClusterMessage(Message message) {
        System.out.println("Cluster received message: " + message);

        String subject = message.getSubject();
        List<String> offlineUserTokens = presenceCache.getOfflineUserTokens(subject);
        if (!offlineUserTokens.isEmpty()) {
            String title = message.getSubject(); // title of the push notification, using the subject as the title
            String body = new String(message.getContent()); // body of the push notification
            try {
                FCMPushNotificationSender.sendPushNotification(offlineUserTokens, title, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
