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
 * JustJsonTest. See the generate-junit-tests.py script for more information.
 * </p>
 */
public class JsonTestSuiteRunner {
  public static void main(String[] args) {
    // Path to the directory containing JSONTestSuite test files
    File testDir = new File("src/test/resources/test_parsing");

    // Run tests for both valid and invalid JSON files
    runTests(testDir, "y_", true);
    runTests(testDir, "n_", false);
    // runTests(testDir, "i_", true); // "i_" are implementation-defined cases, run as desired
  }

  private static void runTests(File dir, String prefix, boolean shouldBeValid) {
    if (!dir.exists() || !dir.isDirectory()) {
      System.err.println("Directory not found: " + dir.getAbsolutePath());
      return;
    }

    for (File file : dir
        .listFiles((d, name) -> name.startsWith(prefix) && name.endsWith(".json"))) {
      try {
        String json = new String(Files.readAllBytes(file.toPath()));
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
