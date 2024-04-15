### migratorydata-demo-chat-fcm-plugin

### Requirements:

- MigratoryData server 6.0.16 or later
- Firebase account
- Java 1.8 or later

### Build the extension:

```bash
./gradlew clean build shadowJar
```

### Setup Instructions:

1. Create Firebase Application:
   - Go to the [Firebase dashboard](https://console.firebase.google.com) and create a new application.
   - Navigate to Project Settings -> Service accounts.
   - Generate a new private key, which will be used to send notifications to users via the Firebase SDK.
   - Save the private key as `fcm-credentials.json` in the root directory of your project.

2. Configuration for Docker Compose:
   - If Docker Compose is installed, you can evaluate the plugin using the configuration found in the `docker` directory.
   - Make sure the `fcm-credentials.json` file is present in the root directory of your project.
   - Navigate to the `docker` directory and execute the `run.sh` command.
   - Validate the plugin with the [flutter chat app demo](https://github.com/migratorydata/migratorydata-demo-chat-fcm-client-flutter) or the [android chat app demo](https://github.com/migratorydata/migratorydata-demo-chat-fcm-client-android)

3. Extension Installation:
   - Copy the extension JAR from `build/libs/presence.jar` to the `extensions` folder of your MigratoryData server installation.
   - Make sure to also move the `fcm-credentials.json` file to the `extensions` directory of your MigratoryData server.
   - Configure the extension by editing the config file `migratorydata.conf` of the MigratoryData server and add the following lines. Restart each MigratoryData server of the cluster.
```bash
ClusterDeliveryMode = Guaranteed
Extension.Presence = true
```

### Presence extension documentation

For further details, please refer the documentation at:

https://migratorydata.com/docs/extension-api/presence/