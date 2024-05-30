plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.11"
    id("xyz.jpenilla.run-paper") version "2.2.3" // Adds runServer and runMojangMappedServer tasks for testing
}

group = "com.readutf.inari.development"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }

    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }

    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }

    mavenLocal()

}

dependencies {
    implementation(project(":Core"))

    compileOnly(paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation ("org.junit.jupiter:junit-jupiter")

    compileOnly("net.kyori:adventure-api:4.14.0")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.minigame"
            artifactId = "development"
            version = "1.1"

            from(components["java"])
        }
    }
}





java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


tasks.assemble {
    dependsOn("reobfJar")
}

tasks.test {
    useJUnitPlatform()
}