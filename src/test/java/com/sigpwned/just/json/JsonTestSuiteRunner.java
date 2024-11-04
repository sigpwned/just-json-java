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

import java.io.File;
import java.nio.file.Files;

/**
 * <p>
 * This class runs the "y_" (valid JSON) and "n_" (invalid JSON) tests cases from JSONTestSuite test
 * cases. (It does not run the "i_", or implementation-defined, cases.) After running each test
 * case, it prints the test case name and result (PASS or FAIL). The specific output format is
 * significant because the generate-junit-tests.py script relies on it to generate JUnit tests based
 * on the rule outcomes.
 * </p>
 * 
 * <p>
 * The maintainer uses this script to choose the coverage profile of the parser. He runs this script
 * to determine which test cases pass and which fail, then reviews the failed test cases to decide
 * whether or not to modify the parser to pass them. Running this application is only necessary when
 * updating the coverage profile.
 * </p>
 * 
 * <p>
 * Once the maintainer has reviewed the failed test cases and made any necessary changes to the
 * parser, he runs the generate-junit-tests.py script against the output of this application to
 * generate JUnit tests based on the coverage profile, and copies the generated code into
 * {@link JustJsonTestSuite}. See the generate-junit-tests.py script for more information.
 * </p>
 */
public class JsonTestSuiteRunner {
  public static void main(String[] args) {
    // Run tests for both valid and invalid JSON files
    runTests(JsonTestSuite.TestCaseType.VALID, true);
    runTests(JsonTestSuite.TestCaseType.INVALID, false);

    // These are implementation-defined cases, run as desired
    // runTests(JsonTestSuite.TestCaseType.IMPLEMENTATION_DECIDED, true);
  }

  private static void runTests(JsonTestSuite.TestCaseType type, boolean shouldBeValid) {
    System.out
        .println("# DO NOT EDIT! This file is automatically generated by JsonTestSuiteRunner.");

    for (File file : JsonTestSuite.listTestCases(type)) {
      try {
        String json = new String(Files.readAllBytes(file.toPath()));

        // We just care whether parsing succeeds or fails, not the result
        @SuppressWarnings("unused")
        Object result = JustJson.parseValue(json);

        if (!shouldBeValid) {
          System.out.println("FAIL (expected failure, but succeeded): " + file.getName());
        } else {
          System.out.println("PASS: " + file.getName());
        }
      } catch (Exception e) {
        if (shouldBeValid) {
          System.out.println("FAIL (expected success, but failed): " + file.getName());
          e.printStackTrace();
        } else {
          System.out.println("PASS: " + file.getName());
        }
      } catch (Error e) {
        System.out.println("FAIL (error " + e.getClass().getSimpleName() + "): " + file.getName());
      }
    }
  }
}
