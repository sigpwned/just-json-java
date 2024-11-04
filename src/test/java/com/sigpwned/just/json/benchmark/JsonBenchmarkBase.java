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
package com.sigpwned.just.json.benchmark;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import com.sigpwned.just.json.JsonTestSuite;
import com.sigpwned.just.json.JsonTestSuite.TestCaseType;

@State(Scope.Benchmark)
public abstract class JsonBenchmarkBase {
  public List<String> documents;

  @Setup
  public void setupJsonBenchmarkBase() throws IOException {
    // Load our test cases
    documents = new ArrayList<>();
    for (File testCase : JsonTestSuite.listTestCases(TestCaseType.VALID)) {
      documents.add(new String(Files.readAllBytes(testCase.toPath()), StandardCharsets.UTF_8));
    }
  }


  public abstract Object parseJsonDocument(String document);

  @Benchmark
  public void benchmark(Blackhole blackhole) throws IOException {
    for (String document : documents) {
      blackhole.consume(parseJsonDocument(document));
    }
  }

  public void run() throws Exception {
    Options opt = new OptionsBuilder()
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        .include(getClass().getName() + ".*")
        // Set the following options as needed
        .mode(Mode.Throughput).timeUnit(TimeUnit.SECONDS).warmupTime(TimeValue.seconds(1))
        .warmupIterations(3).measurementTime(TimeValue.seconds(1)).measurementIterations(3).forks(3)
        .shouldFailOnError(true)
        // .jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
        // .addProfiler(WinPerfAsmProfiler.class)
        .build();
    new Runner(opt).run();
  }
}
