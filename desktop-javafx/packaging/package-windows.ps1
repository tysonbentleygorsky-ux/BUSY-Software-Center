$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    mvn clean package
    New-Item -ItemType Directory -Force -Path "dist" | Out-Null
    jpackage --type exe --name "BUSY School Software Center" --app-version "2.0.0" --input "target" --main-jar "busy-desktop-2.0.0.jar" --dest "dist" --vendor "BUSY School Software Center" --win-menu --win-shortcut
} finally { Pop-Location }
