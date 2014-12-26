package com.hubspot.mason;

import java.util.Collection;

import com.hubspot.mason.builder.AbstractBuilder;
import com.hubspot.mason.builder.HasBuilder;

public interface Complex extends HasBuilder<Complex.Builder> {
  public Example getInner();

  public Collection<String> getStrings();

  public static class Builder extends AbstractBuilder<Complex, Builder> implements Complex {
    private Example inner;
    private Collection<String> strings;

    @Override
    public Example getInner() {
      return inner;
    }

    public Builder setInner(Example inner) {
      this.inner = inner;
      return this;
    }

    @Override
    public Collection<String> getStrings() {
      return strings;
    }

    public Builder setStrings(Collection<String> strings) {
      this.strings = strings;
      return this;
    }
  }
}