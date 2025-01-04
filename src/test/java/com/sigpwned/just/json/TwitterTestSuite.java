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
