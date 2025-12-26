Upgrade notes — Minecraft/Fabric/Loom environment

Summary:
- Target Minecraft: 1.21.11
- Fabric Loader: 0.18.1
- Fabric API: 0.139.5+1.21.11
- Fabric Loom plugin: 1.14.6
- Gradle wrapper: 9.2.1
- Java (build/runtime): Temurin 21 (21.0.9)
- Yarn mappings used: 1.21.11+build.1

Actions taken:
- Updated `build.gradle.kts` to use the above Minecraft and Fabric API versions and set the Loom plugin to 1.14.6.
- Configured Java toolchain to Java 21 and set `--release` to 21 for compilation.
- Updated `gradle.properties` to use Java 21 toolchain and increased `org.gradle.jvmargs` to `-Xmx3G`.
- Updated `src/main/resources/fabric.mod.json` to require `minecraft: 1.21.11` and `fabricloader: >=0.18.1`.
- Updated `pack.mcmeta` pack format to 42 for 1.21.x.
- Installed Temurin JDK 21 locally via `winget install EclipseAdoptium.Temurin.21.JDK` and set `JAVA_HOME` for the build session.
- Upgraded Gradle wrapper to 9.2.1 to match Loom plugin requirements.

Reproduce locally (PowerShell):
1. Install JDK 21 (Temurin):
   winget install EclipseAdoptium.Temurin.21.JDK
2. Set JAVA_HOME for the session:
   $env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot'
   $env:PATH="$env:JAVA_HOME\bin;$env:PATH"
3. Clean/rebuild with refreshed deps:
   ./gradlew.bat --refresh-dependencies clean build

Notes & rationale:
- Fabric recommends Loom 1.14 for Minecraft 1.21.11; using Loom 1.14.6 with Gradle 9.2.1 resolved plugin variant/API mismatches.
- JDK 21 needed because newer Loom/Gradle and mapping toolchains require Java 21 to run Gradle and compile with the correct release target.
- Fabric API chosen from Fabric Maven releases compatible with 1.21.11 (0.139.5+1.21.11).

If you want, I can also add a small `set-java21.ps1` helper script to the repo to make setting up the environment simpler for future builds.

Project-specific Java helper scripts (added):
- `scripts/set-java21.ps1` — sets `JAVA_HOME` and updates `PATH` for the current PowerShell session only (saves previous values in `PROJECT_OLD_PATH` / `PROJECT_OLD_JAVA_HOME`). Run via: `.
  scripts\set-java21.ps1` from project root.
- `scripts/unset-java21.ps1` — restores the session `PATH` / `JAVA_HOME` saved by the set script. Run via: `.
  scripts\unset-java21.ps1`.

How to make Java 21 project-local only (recommended):
1. Keep the JDK installed (no need to uninstall) but remove any system-wide `JAVA_HOME` or JDK `bin` entries from your global PATH — this prevents `java --version` from returning JDK 21 by default.
2. Use `scripts/set-java21.ps1` in a PowerShell session when you want to build or run the project; the change applies only to that shell session.

Commands to remove system-wide settings (optional; admin required):
- Remove `JAVA_HOME` (Machine):
  - Run PowerShell as Administrator:
    [Environment]::SetEnvironmentVariable('JAVA_HOME', $null, 'Machine')
- Remove JDK bin entries from Machine PATH (example):
  - Run PowerShell as Administrator:
    $m = [Environment]::GetEnvironmentVariable('Path','Machine')
    $new = ($m -split ';' | Where-Object { $_ -notmatch 'Eclipse Adoptium\\jdk-21' }) -join ';'
    [Environment]::SetEnvironmentVariable('Path',$new,'Machine')

Note: Modifying Machine environment variables requires admin privileges and restart/sign-out to take effect. I did not change any Machine-level vars automatically — the installer may have added the JDK to your PATH. If you'd like, I can propose and apply a safe script to remove the system PATH entry for the JDK for you (requires confirmation and admin consent).