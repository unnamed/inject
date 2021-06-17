plugins {
	id("java-library")
	id("maven-publish")
}

repositories {
	mavenCentral()
}

dependencies {
	api("javax.inject:javax.inject:1")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
	test {
		useJUnitPlatform()
	}
}

val snapshotRepository: String by project
val releaseRepository: String by project

publishing {
	repositories {
		maven {
			url = if (project.version.toString().endsWith("-SNAPSHOT")) {
				uri(snapshotRepository)
			} else {
				uri(releaseRepository)
			}
			credentials {
				username = project.properties["UNNAMED_REPO_USER"] as String?
						?: System.getenv("REPO_USER")
				password = project.properties["UNNAMED_REPO_PASSWORD"] as String?
						?: System.getenv("REPO_PASSWORD")
			}
		}
	}
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}
