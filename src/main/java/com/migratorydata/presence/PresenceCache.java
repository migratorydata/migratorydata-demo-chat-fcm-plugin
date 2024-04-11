package com.migratorydata.presence;

import com.migratorydata.extensions.presence.MigratoryDataPresenceListener.User;

import java.util.*;

/**
 * In this example, presence information about users and subscriptions is stored in memory. You
 * might replace or supplement this with some persistent storage.
 *
 * @ThreadSafe The methods of this class are always called from the same thread
 */
public class PresenceCache {

    private Map<String, User> userByToken = new HashMap<>(); // fcmToken -> user
    private Map<String, Set<User>> usersBySubject = new HashMap<>(); // subject -> list of users

    /**
     * Updates the presence information for a user.
     *
     * @param user The user object containing updated presence information.
     */
    public void update(User user) {

        userByToken.put(user.getExternalToken(), user);

        // add received user to the list of users for each of its subscribed subjects
        for (String subject : user.getSubjects()) {
            Set<User> userlist = usersBySubject.get(subject);
            if (userlist == null) {
                userlist = new HashSet<>();
                usersBySubject.put(subject, userlist);
            }
            userlist.remove(user);
            userlist.add(user);
        }
    }

    /**
     * Retrieves the list of offline users subscribed to the given subject.
     *
     * @param subject The subject for which offline users are requested.
     * @return The list of offline users subscribed to the given subject.
     */
    public List<User> getOfflineUsersBySubject(String subject) {
        List<User> offlineUsers = new ArrayList<>();

        Set<User> users = usersBySubject.get(subject);
        if (users != null) {
            Iterator<User> it = users.iterator();
            while (it.hasNext()) {
                User user = it.next();

                User currentUser = userByToken.get(user.getExternalToken());
                // Since the presence API doesn't notify when users subscribe or unsubscribe from subjects, we rely on
                // the list of subjects subscribed by each user provided in their latest presence event. If a user is listed
                // for a subject they're no longer subscribed to (due to unsubscribing), we remove their entry from the
                // list associated with that subject.
                if (!currentUser.getSubjects().contains(subject)) {
                    it.remove(); // lazy cleanup for unsubscriptions
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
