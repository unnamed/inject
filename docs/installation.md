## Installation

You can add `inject` to your project using [Gradle](https://gradle.org/) *(recommended)*,
[Maven](https://maven.apache.org/) or manually downloading the JAR files from
[GitHub Releases](https://github.com/unnamed/inject/releases).

Also note that `inject` is available from the [Maven Central Repository](https://search.maven.org/artifact/team.unnamed/inject)
so you can use it in your project without adding any repository!


### Gradle

Add the dependency

```kotlin
dependencies {
    implementation("team.unnamed:inject:%%REPLACE_latestRelease{team.unnamed:inject}%%")
}
```

### Maven

Add the dependency

```xml
<dependency>
    <groupId>team.unnamed</groupId>
    <artifactId>inject</artifactId>
    <version>%%REPLACE_latestRelease{team.unnamed:inject}%%</version>
</dependency>
```