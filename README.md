### migratorydata-demo-chat-fcm-plugin

### Requirements:

- MigratoryData server 6.0.16 or later
- Firebase account
- Java 1.8 or later

### Setup Instructions:

1. Create Firebase Application:
   - Go to the [Firebase dashboard](https://console.firebase.google.com) and create a new application.
   - Navigate to Project Settings -> Service accounts.
   - Generate a new private key, which will be used to send notifications to users via the Firebase SDK.
   - Save the private key as `fcm-credentials.json` in the root directory of your project.

2. Configuration for Docker Compose
   - If you have Docker Compose installed you can test the plugin utilizing the configuration found in the `docker` directory.
   - Copy the `fcm-credentials.json` file to the root directory of your project.
   - Go to `docker` directory and run command `run.sh`
   - Test the plugin with [flutter chat app demo](https://github.com/migratorydata/migratorydata-demo-chat-fcm-client-flutter) or [android chat app demo](https://github.com/migratorydata/migratorydata-demo-chat-fcm-client-android)

3. Installation of the Plugin
   - Copy the plugin JAR from `build/libs/presence.jar` to the `extensions` folder of your MigratoryData server installation.
   - Also, ensure that the `fcm-credentials.json` file is copied to the `extensions` directory of your MigratoryData server.
   - Configure the extension by editing the config file `migratorydata.conf` of the MigratoryData server and add the following lines:
   - Restart each MigratoryData server of the cluster
```bash
ClusterDeliveryMode = Guaranteed
Extension.Presence = true
```

### Compile the plugin

```bash
./gradlew clean build shadowJar
```

### Presence plugin documentation

For further details, please refer the documentation at:

TODO: add doc link