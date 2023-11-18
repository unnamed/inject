plugins {
    `java-library`
    `maven-publish`
    id("org.cadixdev.licenser") version "0.6.1"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

license {
    header.set(resources.text.fromFile("header.txt"))
    include("**/*.java")
    newLine.set(false)
}

tasks {
    test {
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes("Automatic-Module-Name" to "team.unnamed.inject")
        }
    }
}

val repositoryName: String by project
val snapshotRepository: String by project
val releaseRepository: String by project

publishing {
    repositories {
        maven {
            val snapshot = project.version.toString().endsWith("-SNAPSHOT")

            name = repositoryName
            url = if (snapshot) { uri(snapshotRepository) } else { uri(releaseRepository) }
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
