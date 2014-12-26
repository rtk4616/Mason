package com.hubspot.mason.builder;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.hubspot.mason.Mason;

public abstract class AbstractBuilder<T extends HasBuilder<V>, V extends Builder<? super V>> implements Builder<T>, HasBuilder<V> {
  @Override
  public T build() {
    return (T)clone();
  }

  @Override
  public V toBuilder() {
    return clone();
  }

  public V clone() {
    try {
      final ObjectMapper mapper = Mason.getMapper();
      // Similar to MAPPER.treeToValue(MAPPER.valueToTree(this), this.getClass()),
      // but skips tokens -> tree -> tokens. See ObjectMapper#valueToTree(Object).
      TokenBuffer buf = new TokenBuffer(Mason.getMapper(), false);
      mapper.writer().writeValue(buf, this);
      JsonParser json = buf.asParser();
      V result = mapper.reader().withType(this.getClass()).readValue(json);
      json.close();
      return result;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Merges the properties of this builder with another builder (other builder's properties take precedence). Tries to
   * follow the contract of Protobuf mergeFrom:
   *
   * 1. Primitive types will be overridden if populated on the incoming builder
   * 2. Collection types will be concatenated
   * 3. Complex types will be merged recursively
   * @param other The other builder to merge from
   * @return this
   */
  public V mergeFrom(T other) {
    JsonParser parser = merge(this, other);
    try {
      return Mason.getMapper().reader().withValueToUpdate(this).readValue(parser);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Returns a JSON text representation of this object.
   */
  @Override
  public String toString() {
    try {
      return Mason.getMapper().writer().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw Throwables.propagate(e);
    }
  }

  private static JsonParser merge(Object o1, Object o2) {
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);

    final ObjectMapper mapper = Mason.getMapper();

    ObjectNode oldObject = mapper.valueToTree(o1);
    ObjectNode newObject = mapper.valueToTree(o2);

    merge(oldObject, newObject);

    JsonParser parser = oldObject.traverse();
    parser.setCodec(mapper);
    return parser;
  }

  public static void merge(ObjectNode to, ObjectNode from) {
    Iterator<String> newFieldNames = from.fieldNames();

    while (newFieldNames.hasNext()) {
      String newFieldName = newFieldNames.next();
      JsonNode oldVal = to.get(newFieldName);
      JsonNode newVal = from.get(newFieldName);

      if (oldVal == null || oldVal.isNull()) {
        to.put(newFieldName, newVal);
      } else if (oldVal.isArray() && newVal.isArray()) {
        ((ArrayNode) oldVal).addAll((ArrayNode) newVal);
      } else if (oldVal.isObject() && newVal.isObject()) {
        merge((ObjectNode) oldVal, (ObjectNode) newVal);
      } else if (!(newVal == null || newVal.isNull())) {
        to.put(newFieldName, newVal);
      }
    }
  }
}
