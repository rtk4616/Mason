# Mason [![Build Status](https://travis-ci.org/HubSpot/Mason.svg)](https://travis-ci.org/HubSpot/Mason)

**Less painful Java builders!**

The basic idea of the pattern is to have a read-only interface for the object with an inner builder class. The guiding principles are: 
- Objects should get builders with as little boilerplate as possible (it isn't DRY and makes the next goal harder)
- Have a consistent API for all builders so that anyone can hop around projects and know exactly what the builder offers and how it behaves
 
To this end, there is an `AbstractBuilder` class where all of the builder methods are centralized which goes a long way towards achieving both goals. To use, there are three classes involved:

## HasBuilder 

Your read-only interface should extend this interface.

```java
/**
 * @param <V> The builder type
 */
public interface HasBuilder<V extends Builder<?>> {
  public V toBuilder();
}
```

As long as your interface extends `HasBuilder`, it can be used as a parameter or return value to any JAX-RS method (including in collections, maps, inside other objects, etc. etc.) and `MasonModule` will handle deserialization.

## Builder

You won't need to worry about this interface if you extend AbstractBuilder.

```java
/**
 * @param <T> The interface type
 */
public interface Builder<T> {
  public T build();
}
```

## AbstractBuilder

When possible, your builder should extend this abstract class to avoid having to implement `HasBuilder` and `Builder` yourself. It gives you `toBuilder()` on your interface, and then on the builder you can do `build()`, `clone()`, or `mergeFrom()`, as well as any utility methods that are added in the future (without having to write any code yourself!).

## Examples

### Before

```java
public class Example {
  private Long id;
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

### After

```java
public interface Example extends HasBuilder<Example.Builder> {
  public Long getId();
  public String getName();

  public static class Builder extends AbstractBuilder<Example, Builder> implements Example {
    private Long id;
    private String name;

    @Override
    public Long getId() {
      return id;
    }

    public Builder setId(Long id) {
      this.id = id;
      return this;
    }

    @Override
    public String getName() {
      return name;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }
  }
}
```

### Usage

```java
Example example = new Example.Builder().setId(123L).setName("name").build();

Example newName = example.toBuilder().setName("new").build();

Example hasId = new Example.Builder().setId(123L).build();
Example hasName = new Example.Builder().setName("name").build();

Example hasBoth = new Example.Builder().mergeFrom(hasId).mergeFrom(hasName).build(); 
```
