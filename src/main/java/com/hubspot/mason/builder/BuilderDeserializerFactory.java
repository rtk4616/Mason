package com.hubspot.mason.builder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.common.base.Optional;

public class BuilderDeserializerFactory extends Deserializers.Base {
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
                                                  BeanDescription beanDesc) throws JsonMappingException {
    Class<?> clazz = type.getRawClass();

    if (clazz.isInterface() && HasBuilder.class.isAssignableFrom(clazz)) {
      return getDeserializer((Class) clazz);
    } else {
      return super.findBeanDeserializer(type, config, beanDesc);
    }
  }

  private static <T extends HasBuilder<V>, V extends Builder<T>> BuilderDeserializer<T, V> getDeserializer(Class<T> clazz) {
    return new BuilderDeserializer<T, V>(clazz);
  }

  private static class BuilderDeserializer<T extends HasBuilder<V>, V extends Builder<T>> extends JsonDeserializer<T> {
    private final Class<V> builderClazz;

    @SuppressWarnings("unchecked")
    public BuilderDeserializer(Class<T> clazz) {
      Optional<Class<V>> builderClazz = Optional.absent();

      for (Type t : clazz.getGenericInterfaces()) {
        if (t instanceof ParameterizedType) {
          ParameterizedType genericInterface = (ParameterizedType) t;
          if (HasBuilder.class.equals(genericInterface.getRawType())) {
            builderClazz = Optional.of((Class<V>) genericInterface.getActualTypeArguments()[0]);
          }
        }
      }

      if (builderClazz.isPresent()) {
        this.builderClazz = builderClazz.get();
      } else {
        throw new IllegalArgumentException(String.format("Couldn't find builder type for class %s", clazz.getCanonicalName()));
      }
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return jp.readValueAs(builderClazz).build();
    }
  }
}
