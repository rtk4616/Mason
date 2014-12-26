package com.hubspot.mason;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hubspot.mason.builder.AbstractBuilder;
import com.hubspot.mason.builder.HasBuilder;

public class AbstractBuilderTest {
  @Test
  public void testBuild() {
    Example example = new Example.Builder().setId(123L).setName("test").setEmail("benstiller@verizon.net").setCreatedAt(0L).build();

    Assert.assertEquals((Long) 123L, example.getId());
    Assert.assertEquals("test", example.getName());
    Assert.assertEquals("benstiller@verizon.net", example.getEmail());
    Assert.assertEquals((Long) 0L, example.getCreatedAt());
  }

  @Test
  public void testMissingFields() {
    Example example = new Example.Builder().setId(123L).setName("test").build();

    Assert.assertEquals((Long) 123L, example.getId());
    Assert.assertEquals("test", example.getName());
    Assert.assertNull(example.getEmail());
    Assert.assertNull(example.getCreatedAt());
  }

  @Test
  public void testToBuilder() {
    Example example = new Example.Builder().setId(123L).setName("test").setEmail("benstiller@verizon.net").setCreatedAt(0L).build();

    Example newExample = example.toBuilder().setEmail("bob@aol.com").setCreatedAt(1L).build();

    Assert.assertEquals((Long) 123L, newExample.getId());
    Assert.assertEquals("test", newExample.getName());
    Assert.assertEquals("bob@aol.com", newExample.getEmail());
    Assert.assertEquals((Long) 1L, newExample.getCreatedAt());

    // Make sure we didn't modify the original object
    Assert.assertEquals((Long) 123L, example.getId());
    Assert.assertEquals("test", example.getName());
    Assert.assertEquals("benstiller@verizon.net", example.getEmail());
    Assert.assertEquals((Long) 0L, example.getCreatedAt());
  }

  @Test
  public void testToBuilderWithMissingFields() {
    Example example = new Example.Builder().setId(123L).setName("test").build();

    Example newExample = example.toBuilder().setName("new").setCreatedAt(1L).build();

    Assert.assertEquals((Long) 123L, newExample.getId());
    Assert.assertEquals("new", newExample.getName());
    Assert.assertNull(newExample.getEmail());
    Assert.assertEquals((Long) 1L, newExample.getCreatedAt());

    // Make sure we didn't modify the original object
    Assert.assertEquals((Long) 123L, example.getId());
    Assert.assertEquals("test", example.getName());
    Assert.assertNull(example.getEmail());
    Assert.assertNull(example.getCreatedAt());
  }

  @Test
  public void testClone() {
    Example.Builder builder = new Example.Builder().setId(123L).setName("test");

    Example clone = builder.clone().build();

    // Make sure modifying builder doesn't modify the clone
    builder.setCreatedAt(1L).build();

    Assert.assertEquals((Long) 123L, clone.getId());
    Assert.assertEquals("test", clone.getName());
    Assert.assertNull(clone.getEmail());
    Assert.assertNull(clone.getCreatedAt());
  }

  @Test
  public void testCloneComplex() {
    Example example = new Example.Builder().setId(123L).setName("test").build();
    List<String> strings = Lists.newArrayList("blah", "blah");
    Complex.Builder builder = new Complex.Builder().setInner(example).setStrings(strings);

    Complex clone = builder.clone().build();

    // Make sure modifying builder doesn't modify the clone
    builder.setInner(builder.getInner().toBuilder().setCreatedAt(1L).build()).setStrings(Lists.newArrayList("boo"));

    Assert.assertEquals((Long) 123L, clone.getInner().getId());
    Assert.assertEquals("test", clone.getInner().getName());
    Assert.assertNull(clone.getInner().getEmail());
    Assert.assertNull(clone.getInner().getCreatedAt());
    Assert.assertEquals(ImmutableList.of("blah", "blah"), clone.getStrings());
  }

  @Test
  public void testMergeFrom() {
    Example example = new Example.Builder().setId(123L).setName("test").build();

    Example newExample = new Example.Builder().setName("new").setCreatedAt(1L).build();

    Example merged = example.toBuilder().mergeFrom(newExample).build();

    Assert.assertEquals((Long) 123L, merged.getId());
    Assert.assertEquals("new", merged.getName());
    Assert.assertNull(merged.getEmail());
    Assert.assertEquals((Long) 1L, merged.getCreatedAt());

    // Make sure we didn't modify either of the original objects
    Assert.assertEquals((Long) 123L, example.getId());
    Assert.assertEquals("test", example.getName());
    Assert.assertNull(example.getEmail());
    Assert.assertNull(example.getCreatedAt());

    Assert.assertNull(newExample.getId());
    Assert.assertEquals("new", newExample.getName());
    Assert.assertNull(newExample.getEmail());
    Assert.assertEquals((Long) 1L, newExample.getCreatedAt());
  }

  @Test
  public void testMergeFromComplex() {
    Example example = new Example.Builder().setId(123L).setName("test").build();
    List<String> strings = Lists.newArrayList("blah", "blah");
    Complex complex = new Complex.Builder().setInner(example).setStrings(strings).build();

    Example newExample = new Example.Builder().setName("new").setCreatedAt(1L).build();
    List<String> newStrings = Lists.newArrayList("rick", "roll");
    Complex newComplex = new Complex.Builder().setInner(newExample).setStrings(newStrings).build();

    Complex merged = complex.toBuilder().mergeFrom(newComplex).build();

    Assert.assertEquals((Long) 123L, merged.getInner().getId());
    Assert.assertEquals("new", merged.getInner().getName());
    Assert.assertNull(merged.getInner().getEmail());
    Assert.assertEquals((Long) 1L, merged.getInner().getCreatedAt());
    Assert.assertEquals(ImmutableList.of("blah", "blah", "rick", "roll"), merged.getStrings());

    // Make sure we didn't modify either of the original objects
    Assert.assertEquals((Long) 123L, complex.getInner().getId());
    Assert.assertEquals("test", complex.getInner().getName());
    Assert.assertNull(complex.getInner().getEmail());
    Assert.assertNull(complex.getInner().getCreatedAt());
    Assert.assertEquals(ImmutableList.of("blah", "blah"), complex.getStrings());

    Assert.assertNull(newComplex.getInner().getId());
    Assert.assertEquals("new", newComplex.getInner().getName());
    Assert.assertNull(newComplex.getInner().getEmail());
    Assert.assertEquals((Long) 1L, newComplex.getInner().getCreatedAt());
    Assert.assertEquals(ImmutableList.of("rick", "roll"), newComplex.getStrings());
  }

  @Test
  public void testMergeWithAlwaysJsonInclude() throws Exception {
    AlwaysExampleBuilder alwaysExample = new AlwaysExampleBuilder()
            .setId(1)
            ;

    alwaysExample.mergeFrom(new AlwaysExampleBuilder().setName("Test"));

    Assert.assertEquals(1, (int)alwaysExample.getId());
    Assert.assertEquals("Test", alwaysExample.getName());
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  private interface AlwaysExample extends HasBuilder<AlwaysExampleBuilder> {
    Integer getId();
    String getName();
  }

  private static class AlwaysExampleBuilder extends AbstractBuilder<AlwaysExample, AlwaysExampleBuilder>
          implements AlwaysExample {
    private Integer id;
    private String name;

    @Override
    public Integer getId() {
      return id;
    }

    @Override
    public String getName() {
      return name;
    }

    public AlwaysExampleBuilder setId(Integer id) {
      this.id = id;
      return this;
    }

    public AlwaysExampleBuilder setName(String name) {
      this.name = name;
      return this;
    }
  }
}