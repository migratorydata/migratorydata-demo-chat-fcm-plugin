# migratorydata-demo-chat-fcm-plugin

To compile the plugin code run:

`./gradlew buildWithDependencies`

To install the plugin copy the plugin JAR from `build/libs/presence-extension.jar` to the folder `extensions` of your MigratoryData server installation.

Configure the extension by editing the config file of the MigratoryData server and add the following lines:

`ClusterDeliveryMode = Guaranteed`

`PresenceExtension = true`

Then restart each MigratoryData server of the cluster.
