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

import static java.util.stream.Collectors.toList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class TwitterTestSuite {
  /**
   * Read the Twitter 1MB dataset from resources. These tweets were selected more or less at random
   * from <a href="https://archive.org/details/twitterstream">the Internet Archive Twitter Stream
   * Grab dataset</a>. The dataset is as close to 1MB as possible, so each iteration of parsing
   * every tweet in the dataset can loosely be interpreted as 1MB of throughput.
   * 
   * @see <a href="https://archive.org/details/archiveteam-twitter-stream-2021-01">Jan 2021</a>
   */
  public static List<String> readTwets1MbDataset() {
    try {
      try (BufferedReader lines =
          new BufferedReader(new InputStreamReader(new GZIPInputStream(Thread.currentThread()
              .getContextClassLoader().getResourceAsStream("tweets.1MB.jsonl.gz"))))) {
        return lines.lines().map(String::trim).filter(s -> !s.isEmpty()).collect(toList());
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to load tweet dataset", e);
    }
  }
}
