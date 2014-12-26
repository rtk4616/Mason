package com.hubspot.mason;

import com.hubspot.mason.builder.AbstractBuilder;
import com.hubspot.mason.builder.HasBuilder;

public interface Example extends HasBuilder<Example.Builder> {
  public Long getId();
  public String getName();
  public String getEmail();
  public Long getCreatedAt();

  public static class Builder extends AbstractBuilder<Example, Builder> implements Example {
    private Long id;
    private String name;
    private String email;
    private Long createdAt;

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

    @Override
    public String getEmail() {
      return email;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    @Override
    public Long getCreatedAt() {
      return createdAt;
    }

    public Builder setCreatedAt(Long createdAt) {
      this.createdAt = createdAt;
      return this;
    }
  }
}