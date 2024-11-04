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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import java.io.File;
import java.util.List;

public final class JsonTestSuite {
  public static final File TEST_DIR = new File("src/test/resources/test_parsing");

  static {
    if (!TEST_DIR.exists() || !TEST_DIR.isDirectory())
      throw new IllegalStateException(
          "Test directory does not exist: " + TEST_DIR.getAbsolutePath());
  }

  public static enum TestCaseType {
    VALID("y"), INVALID("n"), IMPLEMENTATION_DECIDED("i");

    private final String suffix;

    private TestCaseType(String suffix) {
      this.suffix = requireNonNull(suffix);
    }

    public String getPrefix() {
      return suffix;
    }
  }

  public static List<File> listTestCases(TestCaseType type) {
    return listTestCases(TEST_DIR, type);
  }

  private static List<File> listTestCases(File dir, TestCaseType type) {
    final String prefix = type != null ? type.getPrefix() + "_" : "";
    return asList(dir.listFiles((d, name) -> name.startsWith(prefix) && name.endsWith(".json")));
  }
}
