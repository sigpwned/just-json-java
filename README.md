# Just JSON for Java [![tests](https://github.com/sigpwned/just-json-java/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/just-json-java/actions/workflows/tests.yml) [![Maven Central Version](https://img.shields.io/maven-central/v/com.sigpwned/just-json)](https://central.sonatype.com/artifact/com.sigpwned/just-json) [![javadoc](https://javadoc.io/badge2/com.sigpwned/just-json/javadoc.svg)](https://javadoc.io/doc/com.sigpwned/just-json)

Just JSON is a small, simple library for Java that provides JSON parsing and emitting support and nothing else.

## Goals

Just JSON should...

* **Parse JSON**. The library parses correct JSON and returns a useful model of the data.
* **Emit JSON**. The library emits correct, compact JSON for model data.
* **Be very small**. The JAR file is currently 13KB compressed, 32KB uncompressed.
* **Be very simple**. Users only need 2 methods: one to parse JSON, and one to emit JSON.
* **Be fast enough**. It should be fast enough to let users do what they need to do without getting in the way.

## Non Goals

Just JSON should not...

* **Validate JSON**. The library rejects invalid JSON in most cases, but it is not designed as a strict JSON validating parser.
* **Support more JSON features**. The library intentionally does not provide bean mapping, streaming parsers, and so on.

## Installation

Just JSON is in Maven Central, so you can simply add a dependency.

    <dependency>
        <groupId>com.sigpwned</groupId>
        <artifactId>just-json</artifactId>
        <version>0.0.0</version>
    </dependency>

Just JSON is a single Java file with no dependencies, so you can also just copy/paste it into your project, in a pinch.

## Quickstart

Just JSON models JSON data using the following types:

* Object (`{}`): `Map<String, Object>`, by default a `HashMap`
* Array (`[]`): `List<Object>`, by default an `ArrayList`
* Number: `Number`, by default a `BigDecimal`
* String: `String`
* Boolean: `Boolean`
* null: `JustJson.NULL`, of the special type `JustJson.Null`

To parse a JSON string into the above model:

    Object value;
    try {
        value = JustJson.parseDocument("{\"hello\":\"world\"}");
        // The JSON is valid!
    } catch(IllegalArgumentException e) {
        // The JSON is not valid, and the problem is described in the exception's message.
    }
    
To emit a JSON string from data of the above model:

    Map<String, Object> value = new HashMap<>();
    value.put("hello", "world");
    
    String json;
    try {
        json = JustJson.emitDocument(value);
    } catch(IllegalArgumentException e) {
        // The JSON value was not valid. This means some value was not a Map, List, Number,
        // String, Boolean, or JustJson.NULL. This example will not throw an exception, but 
        // his is how you would handle an exception if one were thrown.
    }

## Advanced Usage

Many users will not need to use anything other than the above methods. Indeed, Just JSON doesn't support any other features by design! However, users looking for customization or curiosity can find additional information on usage here.

### Documents versus Fragments

Most users will be primarily concerned with parsing JSON "documents," i.e., strings that contain only a single JSON value, perhaps surrounded by whitespace. However, Just JSON also supports parsing "fragments," or individual JSON values in a string.

If a user just wants to parse a JSON value at the front of a string with no regard for what comes after, then parsing a fragment will serve that use case nicely:

    Object fragment = JustJson.parseFragment(text);

Another common use case for JSON fragments is text in [JSON Lines format](https://jsonlines.org/), or JSON values separated by newlines. Just JSON provides two ways to process JSON values separated by whitespace, via [`Iterator`](https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html) or via [`Stream`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).

    // Process by iterator
    Iterator<Object> iterator=JustJson.parseFragments(text);
    while(iterator.hasNext()) {
        Object fragment = iterator.next();
        // TODO Process fragment here
    }
    
    // Process by stream
    JustJson.streamFragments(text)
        .filter(fragment -> someFilter(fragment))
        .forEach(fragment -> {
            // TODO Process fragment here
        });


### JustJson.Parser

The `JustJson.Parser` class performs all parsing functions. There are two factory methods for creating `Parser` instances with reasonable default configurations:

    // This parser will return mutable data structures with the default factories
    JustJson.Parser defaultParser = JustJson.defaultParser();
    
    // This parser will return immutable data structures with the default factories
    JustJson.Parser immutableParser = JustJson.immutableParser();
    
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

Users could also choose to [`intern`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#intern--) map keys, use `LinkedHashMap` for maps to retain map key order, and so on.

The default immutable parser's implementation is:

    public static Parser defaultImmutableParser() {
        return new Parser(
            1000,                          // Maximum nesting depth
            StringBuilder::toString,       // String factory for map keys
            HashMap::new,                  // Map factory for objects
            Collections::unmodifiableMap,  // Map finisher for objects
            ArrayList::new,                // List factory for arrays
            Collections::unmodifiableList, // List finisher for arrays
            StringBuilder::toString,       // String factory for string values
            BigDecimal::new,               // Number factory for number values
            Boolean::parseBoolean);        // Boolean factory for boolean values
    }

It's the same as the default mutable parser's configuration, except that it uses finishers to make the returned data structures unmodifiable.

### Emitter

The `Emitter` class is substantially simpler. It has no configuration options. Users can create a default instance like this:

    JustJson.Emitter emitter = JustJson.defaultEmitter();
    
## FAQ

### Why Yet Another JSON Library?

In short, I couldn't find an existing library that served all my use cases, [so I made a new one](https://xkcd.com/927/).

In a little more detail, I ran across a tiny use case in a much larger project that required me to parse JSON. It hardly seemed worth it to add Yet Another Dependency to the project, and the available libraries  either (a) were too large, (b) had too many dependencies, or (c) were too unsupported to shade in. Even the JSON reference implementation for Java, [org.json](https://github.com/stleary/JSON-java), has bean mapping in it! I didn't want any of that. I just wanted JSON parsing.

And so, Just JSON was born.

### Can you add feature X?

Feel free to ask, but probably not. Just JSON is for simple use cases where all you need is to parse and/or emit JSON. If you need more than that, then you should consider another more fully-featured implementation, like Jackson or GSON.

### Are you sure that Just JSON works?

Pretty sure. Just JSON passes 100% of the "y" cases (i.e., test cases for accepting valid JSON) and 94% of the "n" cases (i.e., test cases for rejecting invalid JSON) in [JsonTestSuite](https://github.com/nst/JSONTestSuite).

The specific tests it currently fails are:

* n_number_-2..json
* n_number_with_leading_zero.json
* n_number_2.e3.json
* n_number_U+FF11_fullwidth_digit_one.json
* n_number_-01.json
* n_number_2.e+3.json
* n_number_neg_int_starting_with_zero.json
* n_number_2.e-3.json
* n_number_neg_real_without_int_part.json
* n_number_real_without_fractional_part.json
* n_number_0.e1.json

In other words, Just JSON does not reject JSON with numbers that can be parsed, but do not match the JSON spec, e.g., numbers `1.`. These tests may be fixed in the future, but for the moment they should not interfere with users who just need to work with JSON, not validate it.

### How fast is Just JSON?

Faster than [org.json](https://github.com/stleary/JSON-java), but not as fast as [Jackson](https://github.com/FasterXML/jackson). Here are the results of Just JSON's benchmarks, as run on my humble little laptop:

    Benchmark                     Mode  Cnt   Score   Error  Units
    OrgJsonBenchmark.benchmark   thrpt    9  11.679 ± 0.665  ops/s
    JustJsonBenchmark.benchmark  thrpt    9  18.663 ± 0.835  ops/s
    JacksonBenchmark.benchmark   thrpt    9  27.320 ± 0.719  ops/s
    
Each iteration of the benchmark represents parsing 1MB of JSON spread across multiple fragments. Therefore, the benchmark results can loosely be interpreted each library's performance at parsing JSON in units of MB/s. (So, org.json parses JSON at a rate of 11.679 MB/s.)

Per the benchmarks, Just JSON is about 60% faster than org.json, and Jackson is about 46% faster than Just JSON.

## A Note on Development

Believe it or not, [ChatGPT](https://chatgpt.com/) wrote most of this library.

Writing a JSON parser isn't hard -- like most software engineers, I've taken a swing at it [at least once](https://github.com/sigpwned/jsonification) -- but at the point where I was considering writing Yet Another JSON Library, I knew that I needed short, simple code that I didn't have to think too much about. So I asked ChatGPT to write me a JSON parser, because why not? You can find the conversation [here](https://chatgpt.com/share/67294554-6200-8000-9bbf-ee451d304a79), if you're curious to read it.

To my mild surprise, with a little prompt engineering, [the ChatGPT 4o model](https://platform.openai.com/docs/models#gpt-4o) happily generated both the parser and the basic JsonTestSuite harness. It took an afternoon or so of massaging to get the tests working, write the docs, set up benchmarks, and so on for release. But ChatGPT made a great start!
