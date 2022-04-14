## Bindings

Using the `Binder` we create **bindings**, that is, linking a type to a provider
(which can be a sub-class provider, an instance, a provider function, etc.)

An injector will always try to find an explicit binding for a type before trying
to directly instantiate it. If the injector finds a binding for a type, the
provider is called

**Important:** Note that we **can not** bind the same type to multiple providers

### Direct Binding

A direct binding is a binding of a type to a provider function
(`javax.inject.Provider<T>`)

```java
binder.bind(Database.class).toProvider(() -> {
    System.out.println("Database requested!");
    return new SQLiteDependency("file.db");
});
```

The injector will call the function whenever `Database` is
requested


### Linked Binding

A linked binding is a binding of a type to a sub-type

```java
binder.bind(Database.class).to(MySQLDatabase.class);
```

The injector will try to instantiate and supply `MySQLDatabase`
whenever `Database` is requested


### Instance Binding

An instance binding is a binding of a type to an instance

```java
binder.bind(Database.class).toInstance(new SQLiteDatabase("file.db"));
```

The injector will return the given instance whenever `Database` is
requested

See [Binding Annotations](binding-annotations.md) and [Scopes](scopes.md)