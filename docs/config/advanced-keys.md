## Keys (Advanced Usage)

As we said, bindings are relations of a type to a provider. Well, that's not true at all,
bindings are relations of **keys** to providers. A key is a compound of: a Java type and
an optional qualifier (annotation type or instance)

### Qualifiers

```java
public class MyModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        binder.bind(int.class).toInstance(1);
        binder.bind(int.class).markedWith(Two.class).toInstance(2);
    }
    
}
```

As you can see, there are two bindings, the first one just binds
`int` to `1`, and the second one binds `int` marked with `@Two`
to `2`, so we can request them using:

```java
public class MyClass {
    
    @Inject @Two private int two;   // value = 2
    @Inject private int one;        // value = 1
    
}
```

We can also use an annotation instance

```java
// here we create the qualifier annotation
@Qualifier
@interface Index {
    int value();
}

// bind objects using it
public class MyModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        binder.bind(String.class).qualified(newIndex(0)).toInstance("0");
        binder.bind(String.class).qualified(newIndex(1)).toInstance("1");
    }
    
    private Index newIndex(int value) {
        return new Index() {
          
            @Override
            public Class<Index> annotationType() {
                return Index.class;
            }
            
            @Override
            public int value() {
                return value;
            }
            
        };
    }
    
}

// and finally use it
public class MyClass {
    
    @Inject @Index(0) private String zero;
    @Inject @Index(1) private String one;
    
}
```

By default, we support the `@Named` annotation, we can use it like:

```java
binder.bind(Foo.class).named("name").toInstance(new Foo("Hello!"));
```

And then request it

```java
@Inject @Named("name") private Foo foo;
```

### Types

Keys do not only support classes, but complex Java types like parameterized
types (*generics*), we can bind them using the `TypeReference` class

```java
public class MyGenericsModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        // note the {} at the end, they are there because we must
        // instantiate a TypeReference subclass in order to obtain
        // the generic type in the parameter
        binder.bind(new TypeReference<List<String>>() {})
                .toInstance(Arrays.asList("Hello", "World"));
    }
    
}
```

And then request it normally

```java
public class MyClass {
    
    @Inject private List<String> words;
    
}
```