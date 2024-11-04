/*-
 * =================================LICENSE_START==================================
 * just-json
 * ====================================SECTION=====================================
 * Copyright (C) 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.just.json;

import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>
 * Just JSON is a simple JSON parser/emitter implementation. It uses the following types to model
 * JSON data:
 * </p>
 *
 * <ul>
 * <li>{@link Map} for object values. Keys must be {@link String strings}, values must be one of
 * these values.</li>
 * <li>{@link List} for array values. Values must be one of these values.</li>
 * <li>{@link String} for string values</li>
 * <li>{@link Number} for number values</li>
 * <li>{@link Boolean} for boolean values</li>
 * <li>{@link Null} for the null value, always {@code ==} to {@link #NULL}</li>
 * </ul>
 * 
 * <p>
 * Parsing a JSON string will return data of these types. The emitter expects data of these types.
 * </p>
 */
public class JustJson {
  /**
   * Represents the JSON {@code null} value. JSON null values are represented as an instance of this
   * type.
   * 
   * @see #NULL
   */
  public static class Null {
    public static final Null INSTANCE = new Null();

    private Null() {}

    public int hashCode() {
      return 0;
    }

    public boolean equals(Object that) {
      return this == that;
    }
  }

  /**
   * Represents the JSON {@code null} value. JSON null values are always {@code ==} to this value,
   * {@link #NULL}.
   */
  public static final Null NULL = Null.INSTANCE;

  /**
   * <p>
   * Creates a new {@link #defaultParser() default parser} and uses it to parse the first value from
   * the given string. The string must start with a JSON value, optionally preceded by whitespace.
   * The value can be any valid JSON value, namely: an object, an array, a string, a number, a
   * boolean, or null.
   * </p>
   * 
   * <p>
   * Parsing stops as soon as the first value is parsed. Any trailing characters after the first
   * value will be ignored. The user may call {@link #getIndex()} to determine the position of the
   * first unparsed character to find the remaining input.
   * </p>
   * 
   * <p>
   * The returned value is mutable.
   * </p>
   * 
   * @param json the JSON string to parse
   * @return the parsed value
   * 
   * @throws IllegalArgumentException if the input does not start with a valid JSON value,
   *         optionally preceded by whitespace
   * @throws NullPointerException if the input is null
   * 
   * @see #parseDocument(String)
   * @see #defaultParser()
   * @see Parser#parseFragment(String)
   */
  public static Object parseFragment(String json) {
    return defaultParser().parseFragment(json);
  }

  /**
   * <p>
   * Creates a new {@link #defaultParser() default parser} and uses it to parse all of the fragments
   * from the given string, returning the ordered sequence as an {@link Iterator}. The string must
   * contain zero or more JSON values separated by whitespace. The values can be any valid JSON
   * value, namely: an object, an array, a string, a number, a boolean, or null.
   * </p>
   * 
   * <p>
   * The returned iterator does not support the {@link Iterator#remove()} operation.
   * </p>
   * 
   * <p>
   * The individual returned values are mutable.
   * </p>
   * 
   * <p>
   * This method can be used to parse data in [JSON Lines format](https://jsonlines.org/).
   * </p>
   * 
   * @param json The string to parse
   * @return The iterator of parsed JSON values
   *
   * @throws IllegalArgumentException if the input is not a sequence of zero or more valid JSON
   *         values separated by whitespace
   * @throws NullPointerException if the input is null
   * 
   * @see #defaultParser()
   * @see #parseFragments(Parser, String)
   */
  public static Iterator<Object> parseFragments(String json) {
    return parseFragments(defaultParser(), json);
  }

  /**
   * <p>
   * Creates a new {@link #defaultParser() default parser} and uses it to parse all of the fragments
   * from the given string, returning the ordered sequence as a {@link Stream}. The string must
   * contain zero or more JSON values separated by whitespace. The values can be any valid JSON
   * value, namely: an object, an array, a string, a number, a boolean, or null.
   * </p>
   * 
   * <p>
   * The individual values in the returned stream are mutable.
   * </p>
   * 
   * <p>
   * This method can be used to parse data in [JSON Lines format](https://jsonlines.org/).
   * </p>
   * 
   * @param parser the parser to use
   * @param json The string to parse. It must consist of either (a) all whitespace, in which case
   *        the return stream will be empty; or (b) one or more valid JSON values, each separated by
   *        one or more whitespace characters, with optional leading and trailing whitespace at the
   *        beginning and end of the string.
   * @return a stream of JSON fragments
   * 
   * @see #defaultParser()
   * @see #streamFragments(Parser, String)
   */
  public static Stream<Object> streamFragments(String json) {
    return streamFragments(defaultParser(), json);
  }

  /**
   * <p>
   * Creates a new {@link #defaultParser() default parser} and uses it to parse the given JSON
   * document. The document must contain exactly one JSON value of any type, namely: an object, an
   * array, a string, a number, a boolean, or null.
   * </p>
   * 
   * <p>
   * This method expects a JSON document, which is to say a single JSON value optionally preceded
   * and followed by whitespace. Any leading or trailing characters other than whitespace will cause
   * an exception to be thrown. Users may use {@link #parseFragment(String)} to parse multiple
   * values from a single string.
   * </p>
   * 
   * <p>
   * The returned value is mutable.
   * </p>
   * 
   * @param json the JSON string to parse
   * @return the parsed value
   * 
   * @throws IllegalArgumentException if the input is not a valid JSON value, or if the input
   *         contains non-whitespace leading or trailing characters
   * @throws NullPointerException if the input is null
   * 
   * @see #parseFragment(String)
   * @see #defaultParser()
   * @see Parser#parseDocument(String)
   */
  public static Object parseDocument(String json) {
    return defaultParser().parseDocument(json);
  }

  /**
   * <p>
   * Uses the given parser to parse all of the fragments from the given string, returning the
   * ordered sequence as an {@link Iterator}. The string must contain zero or more JSON values
   * separated by whitespace. The values can be any valid JSON value, namely: an object, an array, a
   * string, a number, a boolean, or null.
   * </p>
   * 
   * <p>
   * The returned iterator does not support the {@link Iterator#remove()} operation.
   * </p>
   * 
   * <p>
   * The individual returned values are mutable.
   * </p>
   * 
   * <p>
   * This method can be used to parse data in [JSON Lines format](https://jsonlines.org/).
   * </p>
   * 
   * @param json The string to parse
   * @return The iterator of parsed JSON values
   *
   * @throws IllegalArgumentException if the input is not a sequence of zero or more valid JSON
   *         values separated by whitespace
   * @throws NullPointerException if the input is null
   * 
   * @see #parseFragments(Parser, String)
   */
  public static Iterator<Object> parseFragments(Parser parser, String json) {
    if (json == null)
      throw new NullPointerException();

    final String firstText = json.trim();
    if (firstText.isEmpty())
      return Collections.emptyIterator();

    final Object firstValue = JustJson.parseFragment(firstText);
    final int firstValueEnd = parser.getIndex();

    return new Iterator<Object>() {
      private String remainingText = firstText.substring(firstValueEnd, firstText.length()).trim();
      private Object value = firstValue;

      @Override
      public boolean hasNext() {
        return value != null;
      }

      @Override
      public Object next() {
        if (!hasNext())
          throw new NoSuchElementException();

        final Object result = value;

        if (remainingText.isEmpty()) {
          remainingText = null;
          value = null;
          return result;
        }

        value = parser.parseFragment(remainingText);
        remainingText = remainingText.substring(parser.getIndex(), remainingText.length()).trim();

        return result;
      }
    };
  }

  /**
   * <p>
   * Uses the given parser to parse all of the fragments from the given string, returning the
   * ordered sequence as a {@link Stream}. The string must contain zero or more JSON values
   * separated by whitespace. The values can be any valid JSON value, namely: an object, an array, a
   * string, a number, a boolean, or null.
   * </p>
   * 
   * <p>
   * The individual values in the returned stream are mutable.
   * </p>
   * 
   * <p>
   * This method can be used to parse data in [JSON Lines format](https://jsonlines.org/).
   * </p>
   * 
   * @param parser the parser to use
   * @param json The string to parse. It must consist of either (a) all whitespace, in which case
   *        the return stream will be empty; or (b) one or more valid JSON values, each separated by
   *        one or more whitespace characters, with optional leading and trailing whitespace at the
   *        beginning and end of the string.
   * @return a stream of JSON fragments
   */
  public static Stream<Object> streamFragments(Parser parser, String json) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parseFragments(parser, json),
        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
  }

  /**
   * <p>
   * Creates a new {@link #defaultEmitter() default emitter} and uses it to create a valid JSON
   * string representation of the given object, then returns it. The result is guaranteed to be a
   * valid JSON document.
   * </p>
   * 
   * @param o the object to emit
   * @return the JSON string representation of the object
   * @throws IllegalArgumentException if the object is not a valid JSON value
   * 
   * @see #parseFragment(String)
   * @see Emitter#emitDocument(Object)
   */
  public static String emitDocument(Object o) {
    return new Emitter().emitDocument(o);
  }


  /**
   * <p>
   * Returns a new default {@link Parser parser}, which has a maximum depth of 1000 and default
   * factories:
   * </p>
   * 
   * <ul>
   * <li>{@link HashMap} for maps</li>
   * <li>{@link ArrayList} for lists</li>
   * <li>{@link StringBuilder#toString()} for strings</li>
   * <li>{@link BigDecimal} for numbers</li>
   * <li>{@link Boolean#parseBoolean(String)} for booleans</li>
   * </ul>
   * 
   * <p>
   * The returned data structure is mutable.
   * </p>
   * 
   * <p>
   * The returned parser is not thread-safe.
   * </p>
   */
  public static Parser defaultParser() {
    return new Parser(1000, StringBuilder::toString, HashMap::new, Function.identity(),
        ArrayList::new, Function.identity(), StringBuilder::toString, BigDecimal::new,
        Boolean::parseBoolean);
  }

  /**
   * <p>
   * Returns a new {@link Parser parser}, which has a maximum depth of 1000 and default factories:
   * </p>
   * 
   * <ul>
   * <li>{@link HashMap} for maps</li>
   * <li>{@link ArrayList} for lists</li>
   * <li>{@link StringBuilder#toString()} for strings</li>
   * <li>{@link BigDecimal} for numbers</li>
   * <li>{@link Boolean#parseBoolean(String)} for booleans</li>
   * </ul>
   * 
   * <p>
   * It is identical to the {@link #defaultParser() default parser}, except that the data structures
   * it returns are immutable.
   * </p>
   * 
   * <p>
   * The returned parser is not thread-safe.
   * </p>
   * 
   * @see #defaultParser()
   */
  public static Parser immutableParser() {
    return new Parser(1000, StringBuilder::toString, HashMap::new, Collections::unmodifiableMap,
        ArrayList::new, Collections::unmodifiableList, StringBuilder::toString, BigDecimal::new,
        Boolean::parseBoolean);
  }

  /**
   * A simple JSON parser. Not thread-safe.
   */
  public static class Parser {
    /**
     * The maximum nesting depth allowed by the parser. The parser will throw an exception if the
     * input exceeds this depth.
     */
    private final int maxDepth;

    /**
     * A factory for creating new {@link String} instances for use as object keys.
     */
    private final Function<StringBuilder, String> keyFactory;

    /**
     * A factory for creating new {@link Map} instances.
     */
    private final Supplier<Map<String, Object>> mapFactory;

    /**
     * A function for finishing a {@link Map} instance. This is used to apply any final processing
     * to the map before returning it, e.g., to make it unmodifiable.
     */
    private final Function<Map<String, Object>, Map<String, Object>> mapFinisher;

    /**
     * A factory for creating new {@link List} instances.
     */
    private final Supplier<List<Object>> listFactory;

    /**
     * A function for finishing a {@link List} instance. This is used to apply any final processing
     * to the list before returning it, e.g., to make it unmodifiable.
     */
    private final Function<List<Object>, List<Object>> listFinisher;

    /**
     * A factory for creating new {@link String} instances for use as values.
     */
    private final Function<StringBuilder, String> stringFactory;

    /**
     * A factory for creating new {@link Number} instances.
     */
    private final Function<String, Number> numberFactory;

    /**
     * A factory for creating new {@link Boolean} instances.
     */
    private final Function<String, Boolean> booleanFactory;

    /**
     * The input JSON string. This is set by {@link #parseFragment(String)}. As a result, this
     * instance is not thread-safe.
     */
    private String json;

    /**
     * The current index in the input JSON string.
     */
    private int index;

    /**
     * The current nesting depth of the parser.
     * 
     * @see #maxDepth
     */
    private int depth;


    /**
     * Creates a new parser with the given maximum depth and factories. Note that the {@code null}
     * value is not parameterized.
     * 
     * @param maxDepth the maximum depth allowed by the parser. Must be at least 1.
     * @param mapFactory a factory for creating new {@link Map} instances
     * @param listFactory a factory for creating new {@link List} instances
     * @param stringFactory a factory for creating new {@link String} instances
     * @param numberFactory a factory for creating new {@link Number} instances
     * @param booleanFactory a factory for creating new {@link Boolean} instances
     */
    public Parser(int maxDepth, Function<StringBuilder, String> keyFactory,
        Supplier<Map<String, Object>> mapFactory,
        Function<Map<String, Object>, Map<String, Object>> mapFinisher,
        Supplier<List<Object>> listFactory, Function<List<Object>, List<Object>> listFinisher,
        Function<StringBuilder, String> stringFactory, Function<String, Number> numberFactory,
        Function<String, Boolean> booleanFactory) {
      if (maxDepth < 1)
        throw new IllegalArgumentException("maxDepth must be at least 1");
      this.maxDepth = maxDepth;
      this.keyFactory = requireNonNull(keyFactory);
      this.mapFactory = requireNonNull(mapFactory);
      this.mapFinisher = requireNonNull(mapFinisher);
      this.listFactory = requireNonNull(listFactory);
      this.listFinisher = requireNonNull(listFinisher);
      this.stringFactory = requireNonNull(stringFactory);
      this.numberFactory = requireNonNull(numberFactory);
      this.booleanFactory = requireNonNull(booleanFactory);
    }

    /**
     * <p>
     * Parses the given JSON document. The document must contain exactly one JSON value of any type,
     * namely: an object, an array, a string, a number, a boolean, or null.
     * </p>
     * 
     * <p>
     * This method expects a JSON document, which is to say a single JSON value optionally preceded
     * and followed by whitespace. Any leading or trailing characters other than whitespace will
     * cause an exception to be thrown. Users may use {@link #parseFragment(String)} to parse
     * multiple values from a single string.
     * </p>
     * 
     * <p>
     * The returned value is mutable.
     * </p>
     * 
     * @param json the JSON string to parse
     * @return the parsed value
     * 
     * @throws IllegalArgumentException if the input is not a valid JSON value, or if the input
     *         contains non-whitespace leading or trailing characters
     * @throws NullPointerException if the input is null
     * 
     * @see #parseFragment(String)
     * @see Emitter#emitDocument(Object)
     */
    public Object parseDocument(String json) {
      Object fragment = parseFragment(json);

      skipWhitespace();

      if (index < json.length()) {
        throw new IllegalArgumentException("Unexpected trailing characters after first value");
      }

      return fragment;
    }

    /**
     * <p>
     * Parses the first value from the given string. The string must start with a JSON value,
     * optionally preceded by whitespace. The value can be any valid JSON value, namely: an object,
     * an array, a string, a number, a boolean, or null.
     * </p>
     * 
     * <p>
     * Parsing stops as soon as the first value is parsed. Any trailing characters after the first
     * value will be ignored. The user may call {@link #getIndex()} to determine the position of the
     * first unparsed character to find the remaining input.
     * </p>
     * 
     * <p>
     * The returned value is mutable.
     * </p>
     * 
     * @param json the JSON string to parse
     * @return the parsed value
     * 
     * @throws IllegalArgumentException if the input does not start with a valid JSON value,
     *         optionally preceded by whitespace
     * @throws NullPointerException if the input is null
     * 
     * @see #parseDocument(String)
     */
    public Object parseFragment(String json) {
      if (json == null)
        throw new NullPointerException();

      this.json = json;
      this.index = 0;
      this.depth = 0;

      return parseValue();
    }

    private Object parseValue() {
      skipWhitespace();
      if (index >= json.length()) {
        throw new IllegalArgumentException("Unexpected end of input");
      }
      char ch = json.charAt(index);
      if (ch == '{') {
        return parseObject();
      } else if (ch == '[') {
        return parseArray();
      } else if (ch == '"') {
        return parseString(stringFactory);
      } else if (Character.isDigit(ch) || ch == '-') {
        return parseNumber();
      } else if (json.startsWith("true", index)) {
        index += 4;
        return booleanFactory.apply("true");
      } else if (json.startsWith("false", index)) {
        index += 5;
        return booleanFactory.apply("false");
      } else if (json.startsWith("null", index)) {
        index += 4;
        return NULL;
      } else {
        throw new IllegalArgumentException("Unexpected character: " + ch);
      }
    }

    private Map<String, Object> parseObject() {
      depth = depth + 1;
      if (depth > maxDepth) {
        throw new IllegalArgumentException("Max depth exceeded");
      }

      Map<String, Object> map = mapFactory.get();
      index++; // skip '{'
      skipWhitespace();
      while (index < json.length() && json.charAt(index) != '}') {
        skipWhitespace();
        String key = parseString(keyFactory);
        skipWhitespace();
        if (index >= json.length()) {
          throw new IllegalArgumentException("Unexpected end of input in object");
        }
        if (json.charAt(index) != ':') {
          throw new IllegalArgumentException("Expected ':' after key");
        }
        index++; // skip ':'
        skipWhitespace();
        Object value = parseValue();
        map.put(key, value);
        skipWhitespace();
        if (index >= json.length()) {
          throw new IllegalArgumentException("Unexpected end of input in object");
        }
        if (json.charAt(index) == ',') {
          index++; // skip ','
          skipWhitespace();
          if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of input in object");
          }
          if (json.charAt(index) == '}') {
            throw new IllegalArgumentException("Unexpected trailing ',' at the end of object");
          }
        } else if (json.charAt(index) != '}') {
          throw new IllegalArgumentException("Expected ',' or '}' in object");
        }
      }
      if (index >= json.length()) {
        throw new IllegalArgumentException("Unexpected end of input in object");
      }
      if (json.charAt(index) != '}') {
        throw new IllegalArgumentException("Expected '}' at the end of object");
      }
      index++; // skip '}'
      return mapFinisher.apply(map);
    }

    private List<Object> parseArray() {
      depth = depth + 1;
      if (depth > maxDepth) {
        throw new IllegalArgumentException("Max depth exceeded");
      }

      List<Object> list = listFactory.get();
      index++; // skip '['
      skipWhitespace();
      while (index < json.length() && json.charAt(index) != ']') {
        list.add(parseValue());
        skipWhitespace();
        if (index >= json.length()) {
          throw new IllegalArgumentException("Unexpected end of input in array");
        }
        if (json.charAt(index) == ',') {
          index++; // skip ','
          skipWhitespace();
          if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of input in array");
          }
          if (json.charAt(index) == ']') {
            throw new IllegalArgumentException("Unexpected trailing ',' at the end of array");
          }
        } else if (json.charAt(index) != ']') {
          throw new IllegalArgumentException("Expected ',' or ']' in array");
        }
      }
      if (index >= json.length()) {
        throw new IllegalArgumentException("Unexpected end of input in array");
      }
      if (json.charAt(index) != ']') {
        throw new IllegalArgumentException("Expected ']' at the end of array");
      }
      index++; // skip ']'
      return listFinisher.apply(list);
    }

    private String parseString(Function<StringBuilder, String> factory) {
      index++; // skip opening '"'
      StringBuilder sb = new StringBuilder();
      while (index < json.length() && json.charAt(index) != '"') {
        char ch = json.charAt(index);
        if (ch == '\\') {
          index++;
          if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of input in string");
          }
          ch = json.charAt(index);
          if (ch == 'u') {
            if (index + 4 >= json.length()) {
              throw new IllegalArgumentException("Invalid Unicode escape sequence");
            }
            String hex = json.substring(index + 1, index + 5);
            sb.append((char) Integer.parseInt(hex, 16));
            index += 4;
          } else {
            switch (ch) {
              case '"':
                sb.append('"');
                break;
              case '\\':
                sb.append('\\');
                break;
              case '/':
                sb.append('/');
                break;
              case 'b':
                sb.append('\b');
                break;
              case 'f':
                sb.append('\f');
                break;
              case 'n':
                sb.append('\n');
                break;
              case 'r':
                sb.append('\r');
                break;
              case 't':
                sb.append('\t');
                break;
              default:
                throw new IllegalArgumentException("Invalid escape sequence: \\" + ch);
            }
          }
        } else if (ch >= 0x00 && ch <= 0x1F) {
          // Per RFC 8259, control characters must be escaped. These consist of U+0000 through
          // U+001F. Note this does not include U+007F (DEL).
          throw new IllegalArgumentException("Unescaped control character in string");
        } else {
          sb.append(ch);
        }
        index++;
      }
      if (index >= json.length() || json.charAt(index) != '"') {
        throw new IllegalArgumentException("Unterminated string");
      }
      index++; // skip closing '"'
      return factory.apply(sb);
    }

    private Number parseNumber() {
      int start = index;
      while (index < json.length() && (Character.isDigit(json.charAt(index))
          || json.charAt(index) == '.' || json.charAt(index) == '-' || json.charAt(index) == 'e'
          || json.charAt(index) == 'E' || json.charAt(index) == '+')) {
        index++;
      }
      return numberFactory.apply(json.substring(start, index));
    }

    private void skipWhitespace() {
      while (index < json.length()) {
        char ch = json.charAt(index);
        if (Character.isWhitespace(ch)) {
          if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
            // Per RFC 8259, only space, tab, line feed, and carriage return whitespace are allowed.
            throw new IllegalArgumentException(
                String.format("invalid whitespace character in whitespace (\\u%04x)", ch));
          }
          index++;
        } else {
          break;
        }
      }
    }

    /**
     * Returns the current index in the input JSON string. This can be used to determine the
     * position of a parsing error, and to find the remaining unparsed input after a successful
     * parse.
     */
    public int getIndex() {
      return index;
    }
  }

  /**
   * Returns a new default {@link Emitter emitter}.
   */
  public static Emitter defaultEmitter() {
    return new Emitter();
  }

  /**
   * A simple JSON emitter. Not thread-safe.
   */
  public static class Emitter {
    /**
     * <p>
     * Create a valid JSON string representation of the given object, then returns it. The result is
     * guaranteed to be a valid JSON document.
     * </p>
     * 
     * <p>
     * This method is a convenience method that delegates to {@link #emitDocument(Object)}.
     * </p>
     * 
     * @param o the object to emit
     * @return the JSON string representation of the object
     */
    public String emitDocument(Map<String, Object> o) {
      return emitDocument((Object) o);
    }

    /**
     * <p>
     * Create a valid JSON string representation of the given object, then returns it. The result is
     * guaranteed to be a valid JSON document.
     * </p>
     * 
     * @param o the object to emit
     * @return the JSON string representation of the object
     * @throws IllegalArgumentException if the object is not a valid JSON value
     * 
     * @see Parser#parseDocument(String)
     */
    public String emitDocument(Object o) {
      if (o == null) {
        throw new NullPointerException();
      } else if (o == NULL) {
        return "null";
      } else if (o instanceof String) {
        return "\"" + escapeString((String) o) + "\"";
      } else if (o instanceof Number) {
        return o.toString();
      } else if (o instanceof Boolean) {
        return o.toString();
      } else if (o instanceof Map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
          if (!first) {
            sb.append(",");
          }
          first = false;
          sb.append(emitDocument(entry.getKey().toString()));
          sb.append(":");
          sb.append(emitDocument(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
      } else if (o instanceof List) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Object item : (List<?>) o) {
          if (!first) {
            sb.append(",");
          }
          first = false;
          sb.append(emitDocument(item));
        }
        sb.append("]");
        return sb.toString();
      } else {
        throw new IllegalArgumentException("Unsupported type: " + o.getClass());
      }
    }

    private String escapeString(String s) {
      return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
          .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
  }
}
