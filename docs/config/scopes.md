## Scopes

Scopes are just wrappers for providers, we *scope* a binding using the `in`
method and providing a `Scope`, you can use the default scopes from the `Scopes`
class:

```java
binder.bind(Database.class).to(MySQLDatabase.class).in(Scopes.SINGLETON);
```

Or using the convenience method `.singleton()` for the singleton scope

```java
binder.bind(Database.class).to(MySQLDatabase.class).singleton();
```

### Via Annotations

We can also scope a class using the `@Singleton` annotation, e.g.

```java
@Singleton
public class MySQLDatabase implements Database {
    
}
```

Note that, in this case, it would be still required to *bind* the `Database`
interface to the `MySQLDatabase` implementation