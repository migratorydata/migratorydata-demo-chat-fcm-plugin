package com.migratorydata.presence;

import com.migratorydata.extension.MigratoryDataPresenceListener.User;

import java.util.*;

/**
 * In this example, presence information about users and subscriptions is stored in memory. You
 * might replace or supplement this with some persistent storage.
 *
 * @ThreadSafe The methods of this class are always called from the same thread
 */
public class PresenceMemoryStorage {
    private Map<String, User> users = new HashMap<>(); // fcmToken -> user
    private Map<String, Set<User>> subscriptions = new HashMap<>(); // subject -> list of users

    public void update(User user) {
        String token = user.getExternalToken();
        User currentUser = users.get(token);
        if (currentUser != null) {
            // if the user already exists, ignore a disconnect presence update corresponding to an older session
            if (user.isOffline() && user.getSessionId() != currentUser.getSessionId()) {
                return;
            }
        }
        users.put(token, user);

        // add received user to the list of users for each of its subscribed subjects
        for (String subject : user.getSubjects()) {
            Set<User> userlist = subscriptions.get(subject);
            if (userlist == null) {
                userlist = new HashSet<>();
                subscriptions.put(subject, userlist);
            }

            userlist.remove(user);
            userlist.add(user);
        }
    }

    public List<User> getOfflineUsers(String subject) {
        List<User> offlineUsers = new ArrayList<>();

        Set<User> users = subscriptions.get(subject);
        if (users != null) {
            Iterator<User> it = users.iterator();
            while (it.hasNext()) {
                User user = it.next();

                // remove the user entry from the list of users of the given subject if the user is no longer
                // subscribed to that subject (this situation can exist as a user might unsubscribe from a
                // subject after it has been added to the list of users of that subject)
                if (!user.getSubjects().contains(subject)) {
                    it.remove(); // lazy cleanup unsubscriptions
                    continue;
                }

                if (user.isOffline()) {
                    offlineUsers.add(user);
                }
            }
        }

        return offlineUsers;
    }
}
