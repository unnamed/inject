## Injector Configuration

To tell the `Injector` how to work, we can configure it during its creating,
to do this we use `Module`'s, e.g.

```Java
Injector injector = Injector.create(
        new DatabaseModule(),
        new WebServerModule()
);

// ...
```

### Modules

Modules are a simple object responsible for the configuration for an `Injector`,
we configure the injector via `Binder`, which provides a verbose and easy to use
DSL

```java
public class DatabaseModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        binder.bind(Database.class)
                .toInstance(new SQLiteDependency("file.db"));
    }
    
}
```

See [Bindings](bindings.md)