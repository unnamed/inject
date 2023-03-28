## Installation

You can add `inject` to your project using [Gradle](https://gradle.org/) *(recommended)*,
[Maven](https://maven.apache.org/) or manually downloading the JAR files


### Gradle

Add our repository

```kotlin
repositories {
    maven("https://repo.unnamed.team/repository/unnamed-public/")
}
```

Add the dependency

```kotlin
dependencies {
    implementation("team.unnamed:inject:2.0.0")
}
```

### Maven

Add our repository

```xml
<repository>
    <id>unnamed-public</id>
    <url>https://repo.unnamed.team/repository/unnamed-public/</url>
</repository>
```

Add the dependency

```xml
<dependency>
    <groupId>team.unnamed</groupId>
    <artifactId>inject</artifactId>
    <version>2.0.0</version>
</dependency>
```