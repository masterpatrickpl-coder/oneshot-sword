// Minimal Fabric Loom buildscript wiring (buildscript/classpath + apply)
plugins {
    id("fabric-loom") version "1.14.6"
    java
    `maven-publish`
}

val minecraftVersion = "1.21.11"
val yarnMappings = "1.21.11+build.1"
val loaderVersion = "0.18.2"
val fabricApiVersion = "0.139.5+1.21.11"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://libraries.minecraft.net/")
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}