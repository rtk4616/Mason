package com.hubspot.mason;

import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum Mason {
  INSTANCE;

  private final AtomicReference<ObjectMapper> mapper = new AtomicReference<>(cloneAndCustomize(new ObjectMapper()));

  private ObjectMapper cloneAndCustomize(ObjectMapper mapper) {
    return mapper.copy()
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new MasonModule());
  }

  private ObjectMapper get() {
    return mapper.get();
  }

  private void set(ObjectMapper mapper) {
    this.mapper.set(cloneAndCustomize(mapper));
  }

  public static ObjectMapper getMapper() {
    return INSTANCE.get();
  }

  public static void setMapper(ObjectMapper mapper) {
    INSTANCE.set(mapper);
  }
}
