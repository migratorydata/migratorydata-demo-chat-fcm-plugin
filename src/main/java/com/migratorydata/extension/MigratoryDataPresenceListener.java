/// @cond
package com.migratorydata.extension;
/// @endcond

import java.util.List;
import java.util.Map;

/** \mainpage Developer's Guide
 *
 * This guide includes the following sections:
 *
 * \li \ref overview
 * \li \ref howto
 * \li \ref examples
 *
 * \section overview Overview
 *
 * This Application Programming Interface (API) contains all the necessary operations for building presence extensions
 * for MigratoryData Server.
 *
 * Before reading this manual, it is recommended to read
 * <em>MigratoryData Architecture Guide</em>
 * (<a target="_blank" href="http://migratorydata.com/documentation/pdf/Architecture.pdf">PDF</a>,
 * <a target="_blank" href="http://migratorydata.com/documentation/html/Architecture/index.html">HTML</a>).
 *
 * \section howto Creating a Presence Extension for MigratoryData Server
 *
 * A typical API usage is as follows:
 *
 * \subsection step1 Step 1 - Import in your application the API as follows:
 *
 * <code>import com.migratorydata.extension.MigratoryDataPresenceListener;</code>
 *
 * \subsection step2 Step 2 - Implement the interface MigratoryDataPresenceListener
 *
 * The interface MigratoryDataPresenceListener is available under the folder <code>doc/Extensions/presence/api</code>
 * of the MigratoryData server installation.
 *
 * \subsection step4 Step 4 - Building the jar of the extension
 *
 * Build a jar consisting in the object code of the class defined at Step 2. In addition, the jar must also include a
 * folder named <code>services</code> in its <code>META-INF</code> folder. Moreover, the folder <code>services</code>
 * must include a file named <code>com.migratorydata.extension.MigratoryDataPresenceListener</code> which must have as
 * content the fully qualified name of the class defined at Step 2.
 *
 * \subsection step5 Step 5 - Install the extension
 *
 * Rename the JAR built at Step 4 to <code>presence-extension.jar</code> and deploy it to the folder
 * <code>extensions</code> of your MigratoryData server installation.
 *
 * \subsection step6 Step 6 - Configure the MigratoryData server to load the presence extension
 *
 * In the configuration file of the MigratoryData server, configure the parameter
 * <code>Extension.Presence</code> as follows:
 *
 * <pre>Extension.Presence = true</pre>
 *
 * For deployments using Custom entitlement rules, there are two additional optional parameters that could be configured
 * <code>Extension.Presence.Subject</code> and <code>Extension.Presence.EntitlementToken</code> to enable user presence
 * replication across the cluster. The default value of the parameter <code>Extension.Presence.Subject</code> is
 * <code>/__migratorydata__/presence</code>, and the default value of the parameter <code>Extension.Presence.EntitlementToken</code>
 * is the values of the parameter <code>EntitlementAllowToken</code>. Your custom entitlement rules should allow
 * subscriptions and publications on the subject and entitlement token defined by these two parameters. See MigratoryData
 * Configuration Guide for further details.
 *
 * \subsection step7 Step 7 - Restart the MigratyData server
 *
 * \subsection step8 Step 8 - Assign the external token to a mobile client
 *
 * In order for a user to be taken into account by the presence extension, the user needs an external token which is
 * typically used by the presence extension to send messages through an external service such as Firebase Cloud Messaging
 * (FCM) or Apple Push Notification Service (APNS) when the user goes offline (e.g. its mobile app goes to background or
 * its mobile devices enters into sleep mode).
 *
 * The API method <code>MigratoryDataClient.setExternalToken()</code> should be used to attach an external token to a mobile
 * client of the MigratoryData server. This API method is available for the client libraries for Android and iOS.
 *
 * \section examples Examples
 *
 * An example built with this API is available in the folder <code>doc/Extensions/presence/examples</code> of your
 * MigratoryData server installation. Start with the README file which explains how to compile and run the example.
 * \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n
 */

/**
 * \brief The implementation of this interface can handle user presence updates such as user connections and user
 * disconnections, and messages received from publishers and accepted by the cluster to be distributed to subscribers.
 *
 * It is guaranteed that each message received from a publisher is accepted by exactly one server of the cluster, which
 * is the coordinator of the subject of the message. Publishers may connect to any server of the cluster. However, each
 * message must pass through the coordinator of the subject of the message to be accepted by the cluster.
 *
 * <h3>Thread safety</h3>
 * The methods exposed by this interface are always called from by same thread.
 */
public interface MigratoryDataPresenceListener {
    /**
     * This method is called whenever a MigratoryData cluster accepts a message received from a MigratoryData client.
     * The message is always accepted by the cluster member which is the coordinator of the subject of the message. So,
     * this method is called once, only for the coordinator of the subject of the accepted message.
     *
     * A MigratoryData client may publish a message to any cluster member, however, the MigratoryData cluster will
     * forward the message to the cluster member which is the coordinator of the subject of the message in order to
     * accept the message.
     *
     * Each cluster member coordinates a distinct subset of subjects. In this way, at any given time, a subject is
     * coordinated by at most one cluster member, always the same until that cluster member fails or is stopped.
     * When a cluster member fails or is stopped, its subjects are automatically redistributed the remaining cluster
     * members such that there are no two cluster members which coordinate simultaneously any given subject.
     *
     * @param message the accepted message
     */
    public void onClusterMessage(Message message);

    /**
     * This method is called whenever a user connects to or disconnects from a cluster member. This method is also
     * called whenever a connected user changes its list of subscribed subjects (by subscribing to new subjects or
     * unsubscribing from existing subjects). MigratoryData replicates this user presence update across the cluster
     * and calls this method for each cluster member, not only for the cluster member to which the user connected to
     * or disconnected from.
     *
     * @param user the details about the user
     */
    public void onUserPresence(User user);

    /**
     * Interface to handle messages accepted by the cluster.
     */
    interface Message {
        /**
         * Get the subject of the message.
         *
         * @return the subject of the message
         */
        public String getSubject();

        /**
         * Get the content of the message.
         *
         * @return the content of the message
         */
        public byte[] getContent();

        /**
         * Get additional attributes of the message as key/value pairs.
         *
         * Currently the following attributes are available:
         * \li seq - the sequence number assigned by the cluster member which is the coordinator of the subject of message
         * \li epoch - the epoch number assigned by the cluster member which is the coordinator of the subject of message
         * \li closure - the closure of the message assigned by the publisher of the message
         * \li publisherAddress - the Internet address of the publisher of the message
         *
         * @return a map with additional attributes of the message
         */
        public Map<String, Object> getAdditionalInfo();
    }

    /**
     * Interface to handle user presence updates.
     */
    interface User {
        /**
         * Get the external token of the user.
         *
         * @return the external token of the user.
         */
        public String getExternalToken();

        /**
         * Get the session id assigned by the MigratoryData server to the user.
         *
         * @return the session id of the user
         */
        public long getSessionId();

        /**
         * Get the list of subscribed subjects by the user.
         *
         * @return the list of subscribed subjects of the user
         */
        public List<String> getSubjects();

        /**
         * Determine if the user is offline.
         *
         * @return true if the user is offline, and false otherwise
         */
        public boolean isOffline();

        /**
         * Get additional attributes of the user presence update as key/value pairs.
         *
         * Currently the following attributes are available:
         * \li address - the Internet address of the user
         * \li entitlementToken - the entitlement token of the user assigned by the client library
         * \li local - inform whether the user presence update has been generated by the local cluster member or
         * has been received from another cluster member as part of presence replication across the cluster
         *
         * @return a map with additional attributes of the user presence update
         */
        public Map<String, Object> getAdditionalInfo();
    }
}