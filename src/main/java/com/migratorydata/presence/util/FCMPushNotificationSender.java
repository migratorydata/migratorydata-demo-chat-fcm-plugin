package com.migratorydata.presence.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;

import java.io.*;
import java.util.List;

/**
 * This class provides utilities for sending push notifications via Firebase Cloud Messaging (FCM).
 * It includes methods for initializing the FCM client with FCM credentials and sending push notifications.
 *
 * <p>For more information on sending push notifications, please refer to the FCM documentation.
 *
 * @ThreadSafe The methods of this class are always called from the same thread.
 */
public class FCMPushNotificationSender {
    public final static String FCM_JSON_KEY_PATH = "./extensions/fcm-credentials.json"; // path to your FCM credentials file
    public final static int MAX_CHUNK_SIZE = 500; // FCM's API has a cap of 500 devices per MulticastMessage

    /**
     * Initializes the FCM client with the FCM credentials.
     * This method should be called before sending any push notifications.
     *
     * @throws IOException If an I/O error occurs while reading the FCM credentials file.
     */
    public static void initialize() throws IOException {
        FileInputStream credentials = new FileInputStream(FCM_JSON_KEY_PATH);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(credentials))
                .build();

        FirebaseApp.initializeApp(options);
    }

    /**
     * Sends a push notification to the specified FCM tokens with the given title and body.
     *
     * @param fcmTokens The FCM registration tokens of the devices to receive the notification.
     * @param title The title of the notification.
     * @param body The body/content of the notification.
     * @throws FirebaseMessagingException If an error occurs while sending the notification.
     */
    public static void sendPushNotification(List<String> fcmTokens, String title, String body) throws FirebaseMessagingException {
        for (int i = 0; i < fcmTokens.size(); i += MAX_CHUNK_SIZE) {
            int endIndex = Math.min(i + MAX_CHUNK_SIZE, fcmTokens.size());
            List<String> fcmTokensSublist = fcmTokens.subList(i, endIndex);

            MulticastMessage message = MulticastMessage.builder()
                    .putData("title", title)
                    .putData("body", body)
                    .setNotification(Notification.builder().setBody("New message received!").setTitle(title).build())
                    .addAllTokens(fcmTokensSublist)
                    .build();

            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println("Message sent successfully to " + batchResponse.getSuccessCount() + " users.");
            System.out.println("Message sent failure to " + batchResponse.getFailureCount() + " users.");
            for (SendResponse sendResponse : batchResponse.getResponses()) {
                String messageId = sendResponse.getMessageId();
                if (sendResponse.isSuccessful()) {
                    System.out.println("Message sent successfully. Message ID: " + messageId);
                } else {
                    System.out.println("Message sending failed for message ID: " + messageId);
                    System.out.println("Error: " + sendResponse.getException());
                }
            }
        }
    }
}
