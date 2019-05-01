package com.migratorydata.presence.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * This class provides an example of sending push notifications via Firebase Cloud Messaging (FCM).
 *
 * Please refer to Firebase documentation to learn more about sending push notifications.
 *
 * @ThreadSafe The methods of this class are always called from the same thread
 */
public class FCMPush {
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    public final static String AUTH_KEY_FCM = "your_fcm_key"; // TODO: update this with your key received from FCM

    public static void sendPushNotification(List<String> tokens, String title, String body) throws IOException {
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        String requestPayload = createRequestPayload(tokens, title, body);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(requestPayload);
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Got FCM update result=" + "\n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createRequestPayload(List<String> tokens, String title, String body) {
        JSONObject object = new JSONObject();

        JSONArray registrationIdsObject = new JSONArray();
        for (String token : tokens) {
            registrationIdsObject.add(token);
        }
        object.put("registration_ids", registrationIdsObject);

        JSONObject notificationObject = new JSONObject();
        notificationObject.put("title", title);
        notificationObject.put("body", body);
        object.put("notification", notificationObject);

        return object.toString();
    }
}
