package com.hubspot.mason;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.hubspot.mason.builder.BuilderDeserializerFactory;

public class MasonModule extends Module {
  @Override
  public String getModuleName() {
    return "BuilderModule";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    context.addDeserializers(new BuilderDeserializerFactory());
  }
}
