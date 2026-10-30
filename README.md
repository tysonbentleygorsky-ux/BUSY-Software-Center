# BUSY School Software Center

A local desktop software catalog and school technology inventory for Windows, macOS, and Linux.

## IT installer library

From the IT Administrator workspace, open **Installer Library** and choose **Add installer**. Enter the software name, publisher, version, platform, category, and description, then attach a vendor installer package. Save it as a draft or publish it to the catalog. Use **Edit** beside a package to change its details; replacing the installer file is optional. Published packages can be downloaded from the catalog by platform.

Accepted files are `.msi`, `.exe`, `.pkg`, `.dmg`, `.deb`, `.rpm`, `.appimage`, and `.zip`, up to 512 MB. The app stores the original file on this computer and does not execute it. Use Intune or another endpoint-management service to deploy packages across computers.

## Asset inventory

The IT Administrator workspace also tracks TVs, monitors, desktop computers, printers, and speakers, with type-based asset tags, assignments, locations, and repair status. Filter assets by equipment type or status and export the current filtered inventory to CSV. The Student Audit view lists equipment explicitly assigned to students and records student software installer downloads by package and time. Downloads cannot be attributed to an individual student until school sign-in is configured; the audit view labels those events as unverified.

## Request visibility

The Student workspace has no request list or school-wide activity feed, and its data response contains no request records. The Teacher workspace shows only requests attributed to that workspace; IT sees the full review queue. The current workspace chooser is not authentication, however, so it does not prevent a student from selecting IT Administrator.

## Local data and account limits

Each app installation stores its catalog, uploaded installer files, requests, and assets locally in the current OS user's app-data folder. Data does not sync to other computers. The role chooser is not school authentication; anyone with access to the app can open the IT Administrator workspace. Do not install this local build for student use or upload private school packages until the school has added authenticated IT access and centralized storage.

## Startup troubleshooting

If the desktop app cannot start, it shows a message with the path to `startup.log` in the app's user-data folder. The log records startup failures and renderer crashes.

## Build installers

Windows x64:

```powershell
npm ci
npm run dist -- --win nsis --x64
```

Windows ARM64 (optional):

```powershell
npm run dist -- --win nsis --arm64
```

macOS Apple Silicon:

```bash
npm ci
npm run dist -- --mac dmg --arm64
```


Artifacts are written to `dist/`. The Windows NSIS installer is machine-wide and supports silent installation with `/S`. Windows installers are unsigned, so SmartScreen may show a warning until the school signs them with its code-signing certificate. For fleet deployment, package the EXE using Microsoft's Win32 Content Prep Tool and distribute it with Intune. The macOS DMG is unsigned and not notarized; sign and notarize it with the school's Apple Developer ID for broad managed distribution.

## Run from source

```bash
npm ci
npm start
```

## Reset local data

Close the app and replace the `data.json` file in the app's user-data folder with `storage/data.example.json`. Installer files are stored in the sibling `installers` folder; delete that folder too if you intend to remove uploaded packages.
