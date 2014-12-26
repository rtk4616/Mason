package com.hubspot.mason;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;

public class MasonModuleTest {
  private static final ObjectMapper MAPPER = Mason.getMapper();

  @Test
  public void testSimple() throws IOException {
    Example original = new Example.Builder().setId(123L).setName("name").build();

    String json = MAPPER.writeValueAsString(original);
    // Read the value as the interface, will fail unless the module is working
    Example parsed = MAPPER.readValue(json, Example.class);

    assertEquals(original.getId(), parsed.getId());
    assertEquals(original.getName(), parsed.getName());
    assertEquals(original.getEmail(), parsed.getEmail());
    assertEquals(original.getCreatedAt(), parsed.getCreatedAt());
  }

  @Test
  public void testComplex() throws IOException {
    Example e = new Example.Builder().setId(123L).setName("name").build();
    List<String> s = ImmutableList.of("a", "b");
    Complex original = new Complex.Builder().setInner(e).setStrings(s);

    String json = MAPPER.writeValueAsString(original);
    // Read the value as the interface, will fail unless the module is working
    Complex parsed = MAPPER.readValue(json, Complex.class);

    assertEquals(original.getInner().getId(), parsed.getInner().getId());
    assertEquals(original.getInner().getName(), parsed.getInner().getName());
    assertEquals(original.getInner().getEmail(), parsed.getInner().getEmail());
    assertEquals(original.getInner().getCreatedAt(), parsed.getInner().getCreatedAt());
    assertEquals(original.getStrings(), parsed.getStrings());
  }
}