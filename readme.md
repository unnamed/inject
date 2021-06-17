# Trew D.I. ![Build Status](https://img.shields.io/github/workflow/status/yusshu/trew/build/master) [![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)

Trew is a very lightweight and fast dependency injection library based on [Guice](https://github.com/google/guice) like
the old [Syringe DI](https://github.com/unnamed/syringe). Trew, unlike Syringe, supports circular dependencies, this is
an important difference between these D.I

## Usage

Read the [Wiki on GitHub](https://github.com/yusshu/trew/wiki) to learn about the usage of Trew

## Download

You can simply download the JAR from GitHub in the Releases section, use [Maven](https://maven.apache.org/)
or [Gradle](https://gradle.org/) (recommended)

## Repositories

### Releases repository

All versions that doesn't end with "-SNAPSHOT" will be here.

#### Maven (pom.xml)

```xml
<repository>
    <id>unnamed-releases</id>
    <url>https://repo.unnamed.team/repository/unnamed-releases/</url>
</repository>
```

#### Gradle (build.gradle)

```groovy
repositories {
  maven { url 'https://repo.unnamed.team/repository/unnamed-releases/' }
}
```

### Snapshots repository

All versions ending with "-SNAPSHOT" will be here

#### Maven (pom.xml)

```xml
<repository>
    <id>unnamed-snapshots</id>
    <url>https://repo.unnamed.team/repository/unnamed-snapshots/</url>
</repository>
```

#### Gradle (build.gradle)

```groovy
repositories {
  maven { url 'https://repo.unnamed.team/repository/unnamed-snapshots/' }
}
```

### Dependency

- Latest
  snapshot: [![Latest Snapshot](https://img.shields.io/nexus/s/me.yushust.inject/core.svg?server=https%3A%2F%2Frepo.unnamed.team)](https://repo.unnamed.team/repository/unnamed-snapshots)
- Latest
  release: [![Latest Release](https://img.shields.io/nexus/r/me.yushust.inject/core.svg?server=https%3A%2F%2Frepo.unnamed.team)](https://repo.unnamed.team/repository/unnamed-releases)

#### Maven (pom.xml)

```xml
<dependency>
    <groupId>me.yushust.inject</groupId>
    <artifactId>core</artifactId>
    <version>VERSION</version> <!--Check the latest version in the repositories-->
</dependency>
```

#### Gradle (build.gradle)

```groovy
dependencies {
  implementation 'me.yushust.inject:core:VERSION'
}
```

.
