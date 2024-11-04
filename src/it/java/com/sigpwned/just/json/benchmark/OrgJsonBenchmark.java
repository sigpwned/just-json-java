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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class OrgJsonBenchmark extends JsonBenchmarkBase {
  /**
   * Adapted from
   * https://github.com/nst/JSONTestSuite/blob/ce29be082120577cc73c137ddca62eab894cef93/parsers/test_java_org_json_2016_08/TestJSONParsing.java
   */
  public Object parseJsonDocument(String s) {
    try {
      int idxArray = s.indexOf('[');
      int idxObject = s.indexOf('{');

      if (idxArray < 0 && idxObject < 0) {
        // Give them a freebie, since there is no way to parse this
        return null;
      } else if (idxObject >= 0 && (idxArray < 0 || idxObject < idxArray)) {
        return new JSONObject(s);
      } else {
        return new JSONArray(s);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Test
  public void test() throws Exception {
    run();
  }
}
