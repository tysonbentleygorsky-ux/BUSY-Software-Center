# BUSY Native Desktop Migration

The native desktop client is in `desktop-javafx/`. It uses Java 21 and JavaFX controls rather than Electron, HTML, CSS, or a browser renderer.

Start the native app:

```bash
cd desktop-javafx
mvn clean javafx:run
```

Build a JAR with `mvn clean package`. Platform-native Windows EXE and macOS DMG packaging scripts are in `desktop-javafx/packaging/`. Build each installer on its target operating system.

The previous Electron app remains in the repository during migration so existing workflows are not discarded before their JavaFX replacements and backend endpoints are complete. It is not the new native client.
