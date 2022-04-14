## Basic Usage

So now we know what the `@Inject` does:
- In a field: declares that the field is a dependency that must be set
- In constructors and methods: declares that the parameters are dependencies that must be provided

### Declaration

Using the `@Inject` annotation we can **declare dependencies**, e.g.

```java
public class ItemDao {
    
    @Inject private Database database;
    @Inject private Configuration configuration;
    
    // ...
    
}
```

However, the declaration does nothing, we must **process** it


### Processing

When you declare a dependency on some class, like `@Inject private Database database`,
the dependency is processed when the class is processed, so it is not necessary to manually
process them all.

As we said, the dependencies of an object will be processed together with it, but, we need to
manually process an initial object, the entry point

To do this, we must create an `Injector` and optionally configure it (we will explain this later)

```java
public class Main {
    
    @Inject private Server server;
    
    public void start() {
        server.start();
    }
    
    public static void main(String[] args) {
        Injector injector = Injector.create();
        // here we manually tell the injector to instantiate
        // our Main class that declares a dependency on the
        // Server class, our Injector is smart and will instantiate
        // and set it too
        Main main = injector.getInstance(Main.class);
        main.start();
        
        // We can alternatively do:
        Main main = new Main();
        injector.injectMembers(main);
        main.start();
        // however, the Injector does not know about the Main
        // constructor, and it will not process it so injections
        // there will not work. It will just inject fields and methods
    }
    
}
```

In this case, the injector will instantiate both `Main` and `Server`
using the default constructor, but what if we need more? what if Server
is an interface? how do we **bind** the interface to an implementation?
See [Injector Configuration](config/injector-configuration.md)