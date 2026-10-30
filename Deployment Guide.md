# BUSY School Software Center 1.6.1

## Windows installers

- Most Intel or AMD PCs: `BUSY School Software Center-1.6.1-x64.exe`
- Windows on ARM PCs: `BUSY School Software Center-1.6.1-arm64.exe`

The installer runs machine-wide and supports silent installation with `/S`. For Intune, wrap the matching EXE with Microsoft’s Win32 Content Prep Tool, install with `/S`, and set a detection rule. Assign the x64 package to x64 Windows devices and the ARM64 package only to ARM64 devices. Pilot on a small device group before broad rollout.

These installers are unsigned. Windows may show SmartScreen or organization security warnings until the school signs them with its code-signing certificate.

## Startup troubleshooting

If the app fails during startup, it shows the path to `startup.log` in its error message. The log is saved under the current user’s app-data folder and records startup errors and renderer crashes.

## Data and sign-in limits

Each installation stores its catalog, installer files, requests, and asset inventory on that computer. Records do not sync between computers. The workspace chooser is not school sign-in and does not secure roles. Add authenticated sign-in and central storage before using this as a shared school system or relying on person-level student audits.
