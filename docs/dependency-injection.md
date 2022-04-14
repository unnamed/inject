## Dependency Injection

Dependency injection ([Wikipedia](https://en.wikipedia.org/wiki/Dependency_injection)) is an 
OOP design pattern where classes declare their dependencies as arguments instead of directly
creating them.

This pattern lets us easily create flexible and testable objects, following the
[Liskov Substitution Principle](https://en.wikipedia.org/wiki/Liskov_substitution_principle)

### Without Dependency Injection

Here is an example where we **do not** use dependency injection:

```java
public class UserDao {
    
    private final Database database = new SQLiteDatabase("/myfile.dat");
    
    // ...
    
}
```

In this example, you can't replace the database implementation or file path, which
makes it hard to test and re-use


### With Dependency Injection

Here is an example where we **do** use dependency injection:

```java
public class UserDao {
    
    private final Database database;
    
    public FileReader(Database database) {
        this.database = database;
    }
    
    // ...
    
}
```

In this example, we can set **any** database implementation, so it is testable
and re-usable


### With unnamed/inject

Here is an example that uses dependency injection with `unnamed/inject`

```java
import javax.inject.Inject;

public class UserDao {
    
    @Inject private Database database;
    
}
```

In this example, we just declare a dependency using the `@Inject` annotation
instead of as an argument in a constructor. However, the `@Inject` annotation is
not magic and this will not work using `new UserDao()` since the object is
not processed. See [Basic Usage](basic-usage.md)