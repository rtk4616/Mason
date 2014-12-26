package com.hubspot.mason.builder;

public interface HasBuilder<V extends Builder<?>> {
  V toBuilder();
}
