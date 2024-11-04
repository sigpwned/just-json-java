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

import org.junit.Test;
import com.sigpwned.just.json.JustJson;

public class JustJsonBenchmarkTest extends JsonBenchmarkBase {
  public Object parseJsonDocument(String document) {
    try {
      return JustJson.parseValue(document);
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
