plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.11" // Check for new versions at https://plugins.gradle.org/plugin/io.papermc.paperweight.userdev
}

group = "com.readutf.inari.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }

    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }

    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.minigame"
            artifactId = "core"
            version = "1.1"

            artifactId = ""


            from(components["java"])
        }
    }
}

dependencies {

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    //add jackson
    implementation(platform("com.intellectualsites.bom:bom-newest:1.40")) // Ref: https://github.com/IntellectualSites/bom
    implementation ("org.reflections:reflections:0.10.2")
    implementation ("net.kyori:adventure-api:4.14.0")

    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation ("fr.mrmicky:fastboard:2.1.0")

}

tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        relocate("fr.mrmicky.fastboard", "com.readutf.inari.core")
    }

    compileJava {
        options.compilerArgs.add("-parameters")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
