# BUSY Native Desktop (JavaFX)

This is the native desktop replacement for the legacy Electron renderer. The interface is built from JavaFX controls and layouts. It does not embed Chromium, HTML, or a web view.

## Requirements

- JDK 21
- Maven 3.9+
- Windows 10/11 x64 or macOS 13+ (Apple Silicon or Intel)

## Run

From this directory:

```bash
mvn clean javafx:run
```

The app opens in local-demo mode and attempts to connect to the BUSY API at `http://localhost:8080`. Change the address in Settings to your school's HTTPS API address. The current JavaFX client connects to the existing `/api/health` and `/api/catalog` endpoints.

## Build

```bash
mvn clean package
```

The shaded application JAR is written to `target/busy-desktop-2.0.0.jar`.

## Create native installers

Run the packaging script on the operating system you are targeting. JDK 21's `jpackage` creates a platform-native package and must run on the target operating system.

Windows PowerShell:

```powershell
.\packaging\package-windows.ps1
```

macOS:

```bash
chmod +x packaging/package-macos.sh
./packaging/package-macos.sh
```

Outputs are placed in `dist/`. For macOS distribution outside a development team, sign and notarize the app with the school's Apple Developer ID. Windows installers should be code-signed before broad school deployment.

## Current implementation boundary

The JavaFX shell and live catalog/health integration are implemented. Requests, device inventory editing, reporting dashboards, school identity-provider login, installer download/deployment, and role-protected administration still require corresponding backend APIs and authentication. The existing Spring API's demo security configuration is not production-ready. Do not use the current platform foundation for real student data until school authentication, authorization, storage, audit coverage, and security review are complete.
