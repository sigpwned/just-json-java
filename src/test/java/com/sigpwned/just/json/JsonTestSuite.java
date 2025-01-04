/*-
 * =================================LICENSE_START==================================
 * just-json
 * ====================================SECTION=====================================
 * Copyright (C) 2024 Andy Boothe
 * ====================================SECTION=====================================
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <https://unlicense.org/>
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
