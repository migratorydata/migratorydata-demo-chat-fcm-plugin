package com.migratorydata.presence.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;

import java.io.*;
import java.util.List;

/**
 * This class provides an example of sending push notifications via Firebase Cloud Messaging (FCM).
 *
 * Please refer to Firebase documentation to learn more about sending push notifications.
 *
 * @ThreadSafe The methods of this class are always called from the same thread
 */
public class FCMPush {

    public final static String FCM_JSON_KEY_PATH = "./extensions/fcm-credentials.json";

    //
    public static void sendPushNotification(List<String> registrationTokens, String title, String body) throws FirebaseMessagingException {

        // Use a list containing up to 500 registration tokens.
        MulticastMessage message = MulticastMessage.builder()
                .putData("title", title)
                .putData("body", body)
                .setNotification(Notification.builder().setBody("New message received!").setTitle(title).build())
                .addAllTokens(registrationTokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        List<SendResponse> rr = response.getResponses();

        for (SendResponse s : rr) {
            System.out.println(s.getException());
        }

        System.out.println(response.getSuccessCount() + " messages were sent successfully");
    }

    public static void initialize() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(FCM_JSON_KEY_PATH);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
