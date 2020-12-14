# Trew D.I. [![Build Status](https://travis-ci.com/yusshu/trew.svg?branch=master)](https://travis-ci.com/yusshu/trew)

Trew is a very lightweight and fast dependency injection library based on [Guice](https://github.com/google/guice) like the old [Syringe DI](https://github.com/unnamed/syringe). Trew, unlike Syringe, supports circular dependencies, this is an important difference between these D.I

## Why?
Trew helps you create a very scalable and orderly application in Java, check this examples with Trew and without Trew

### With Trew
```java
Injector injector = Injector.create(binder -> {
  binder.bind(Tea.class).toProvider(TeaProvider.class).singleton();
  binder.bind(Cookie.class).toInstance(new ChocolateCookie());
});
```
```java
public class TeaProvider implements Provider<Tea> {
  @Override
  public Tea get() {
    Season season = Season.current();
    if (season == Season.SUMMER || season == Season.SPRING) {
      return new IcedTea();
    } else {
      return new HotTea();
    }
  }
}
```
```java
public class Consumer {
  @Inject private Tea tea;
  @Inject private Cookie cookie;

  public void consumeYourStuff() {
    // do something with tea and cookie...
  }
}
```
### Without Trew
```java
public class TeaFactory {
  private static final Object lock = new Object();
  private static Tea tea;
  
  public Tea get() {
    if (tea == null) {
      // thread-safety
      synchronized (lock) {
        if (tea == null) {
          Season season = Season.current();
          if (season == Season.SUMMER || season == Season.SPRING) {
            tea = new IcedTea();
          } else {
            tea = new HotTea();
          }
        }
      }
    }
    return tea;
  }
}
```
```java
public class Consumer {
  private final Tea tea;
  private final Cookie cookie;
  
  public Consumer(Tea tea, Cookie cookie) {
    // trew checks nullability for you, if an injectable is annotated with
    // any @Nullable annotation, it's handled as an optional injection
    this.tea = Objects.requireNonNull(tea);
    this.cookie = Objects.requireNonNull(cookie);
  }
  
  public void consumeYourStuff() {
    // do something with tea and cookie...
  }
}
```
Instantiation without Trew is `new Consumer(TeaFactory.get(), new ChocolatCookie());`
Instantiation with Trew is `Injector#getInstance(Consumer.class)` or just `@Inject private Consumer consumer;`, with Trew the implementations are only known by the configuration `Module`s

## Usage
Read the [Wiki on GitHub](https://github.com/yusshu/trew/wiki) to learn about the usage of Trew
## Download
You can simply download the JAR from GitHub in the Releases section, or use [Maven](http://maven.apache.org/) (recommended)
### Maven Repositories
Releases repository, all versions that doesn't end with "-SNAPSHOT" will be here.
```xml
<repository>
    <id>unnamed-releases</id>
    <url>https://repo.unnamed.team/repository/unnamed-releases/</url>
</repository>
```
Snapshots repository, all versions ending with "-SNAPSHOT" will be here
```xml
<repository>
    <id>unnamed-snapshots</id>
    <url>https://repo.unnamed.team/repository/unnamed-snapshots/</url>
</repository>
```
### Maven Dependency
Put this in your POM.xml `<dependencies>` section
```xml
<dependency>
    <groupId>me.yushust.inject</groupId>
    <artifactId>trew</artifactId>
    <version>0.2.5-SNAPSHOT</version> <!--Check the latest version in the repositories-->
</dependency>
```
