/**
 *   Copyright 2014 Prasanth Jayachandran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.prasanthj.cmsketch;

/**
 * Count Min sketch is a probabilistic data structure for finding the frequency of events in a
 * stream of data.
 */
public class CountMinSketch {
  // 3% estimation error with 3% probability that the estimation breaks this limit
  private static final float DEFAULT_DELTA = 0.03f;
  private static final float DEFAULT_EPSILON = 0.03f;
  private final int w;
  private final int d;
  private final int[][] multiset;

  public CountMinSketch() {
    this(DEFAULT_DELTA, DEFAULT_EPSILON);
  }

  public CountMinSketch(float delta, float epsilon) {
    this.w = (int) Math.ceil(Math.exp(1.0) / epsilon);
    this.d = (int) Math.ceil(Math.log(1.0 / delta));
    this.multiset = new int[d][w];
  }

  public CountMinSketch(int width, int depth) {
    this.w = width;
    this.d = depth;
    this.multiset = new int[d][w];
  }

  public int getWidth() {
    return w;
  }

  public int getDepth() {
    return d;
  }

  public long getSizeInBytes() {
    return w * d * (Integer.SIZE / 8);
  }

  public void set(byte[] key) {
    // We use the trick mentioned in "Less Hashing, Same Performance: Building a Better Bloom Filter"
    // by Kirsch et.al. From abstract 'only two hash functions are necessary to effectively
    // implement a Bloom filter without any loss in the asymptotic false positive probability'

    // Lets split up 64-bit hashcode into two 32-bit hashcodes and employ the technique mentioned
    // in the above paper
    long hash64 = Murmur3.hash64(key);
    int hash1 = (int) hash64;
    int hash2 = (int) (hash64 >>> 32);
    for (int i = 1; i <= d; i++) {
      int combinedHash = hash1 + (i * hash2);
      // hashcode should be positive, flip all the bits if it's negative
      if (combinedHash < 0) {
        combinedHash = ~combinedHash;
      }
      int pos = combinedHash % w;
      multiset[i - 1][pos] += 1;
    }
  }

  public long get(byte[] key) {
    long hash64 = Murmur3.hash64(key);
    int hash1 = (int) hash64;
    int hash2 = (int) (hash64 >>> 32);
    int min = Integer.MAX_VALUE;
    for (int i = 1; i <= d; i++) {
      int combinedHash = hash1 + (i * hash2);
      // hashcode should be positive, flip all the bits if it's negative
      if (combinedHash < 0) {
        combinedHash = ~combinedHash;
      }
      int pos = combinedHash % w;
      min = Math.min(min, multiset[i - 1][pos]);
    }

    return min;
  }

}
