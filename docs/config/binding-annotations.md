## Binding Annotations

Alternatively to using the `Binder` DSL, we can bind classes via annotations,
examples:

Using `@Targetted` instead of `bind(...).to(...)`:

```java
@Targetted(MySQLDatabase.class)
public interface Database {
    // ...
}
```

Using `@ProvidedBy` instead of `bind(...).toProvider(...)`:

```java
@ProvidedBy(DatabaseProvider.class)
public interface Database {
    
}

class DatabaseProvider implements Provider<Database> {
    
    @Override
    public Database get() {
        // ...
    }
    
}
```