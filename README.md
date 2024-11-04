# Just JSON for Java

Just JSON for Java is a small, simple JSON library for Java that provides JSON parsing and generating support and nothing else.

## Goals

Just JSON should...

* **Parse JSON**. The library parses correct JSON and returns a useful model of the data.
* **Generate JSON**. The library emits correct, compact JSON for model data.
* **Be very small**. The JAR file is currently 11KB compressed, 28KB uncompressed.
* **Be very simple**. Users only need 2 methods: one to parse JSON, and one to emit JSON.
* **Be fast enough**. It should be fast enough to let users do what they need to do without getting in the way.

## Non Goals

Just JSON should not...

* **Validate JSON**. The library rejects most invalid JSON, but it is not intended to be a validating parser.
* **Support more JSON features**. The library intentionally does not provide bean mapping, streaming parsers, and so on.

## Quickstart

Just JSON models JSON data using the following types:

* Object (`{}`): `Map<String, Object>`, by default a `LinkedHashMap`
* Array (`[]`): `List<Object>`, by default an `ArrayList`
* Number: `Number`, by default a `BigDecimal`
* String: `String`
* Boolean: `Boolean`
* null: `JustJson.NULL`, of the special type `JustJson.Null`

To parse a JSON string into the above model:

    Object value = JustJson.parseDocument("{\"hello\":\"world\"}");
    
To emit a JSON string from data of the above model:

    Map<String, Object> value = new HashMap<>();
    value.put("hello", "world");
    JustJson.emitDocument(value);

## Usage

### JustJson.Parser

The `JustJson.Parser` class performs all parsing functions. There are two factory methods for creating `Parser` instances with reasonable default configurations:

    // This parser will return mutable data structures with the default factories
    JustJson.Parser mutableParser = JustJson.defaultParser();
    
    // This parser will return immutable data structures with the default factories
    JustJson.Parser immutableParser = JustJson.defaultImmutableParser();
    
The `Parser` class uses "factories" and "finishers" for creating the data it returns. Users can use custom factories and finishers to achieve different effects when parsing.

The default mutable parser's implementation is:

    public static Parser defaultParser() {
        return new Parser(
            1000,                    // Maximum nesting depth
            StringBuilder::toString, // String factory for map keys
            HashMap::new,            // Map factory for objects
            Function.identity(),     // Map finisher for objects
            ArrayList::new,          // List factory for arrays
            Function.identity(),     // List finisher for arrays
            StringBuilder::toString, // String factory for string values
            BigDecimal::new,         // Number factory for number values
            Boolean::parseBoolean);  // Boolean factory for boolean values
    }

Users cannot change the underlying types of data Just JSON returns using factories and finishers, but they can change implementations. For example, some users may prefer to use `Double` for numbers instead of `BigDecimal`, which could be achieved like this:

    public static Parser defaultParser() {
        return new Parser(
            1000,                    
            StringBuilder::toString, 
            HashMap::new,            
            Function.identity(),     
            ArrayList::new,          
            Function.identity(),     
            StringBuilder::toString, 
            Double::new,             // Use Double instead of BigDecimal to represent numbers
            Boolean::parseBoolean);
    }

Users could also choose to [`intern`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#intern--) map keys, use
