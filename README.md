OneShot Sword — Fabric mod for Minecraft 1.21.11

Overpowered sword that deletes most mobs/players on hit and buffs the attacker. Single-player or controlled servers only.

Stack
- Fabric Loader 0.18.1, Fabric API 0.139.5+1.21.11
- Fabric Loom 1.14.6, Gradle 9.2.1 (wrapper included)
- Java 21 (Temurin 21.0.9 tested)

Build (PowerShell)
```powershell
./gradlew.bat clean build
```
Jar: build/libs/

Notes
- Pack format 42; assets under src/main/resources/assets/oneshot/
- Texture is a placeholder; replace textures/item/oneshot_sword.png
- Sword applies heavy status effects (Speed/Jump boosted, Dolphin’s Grace, Night Vision) and attempts an instant kill.

License
- MIT (see LICENSE).
