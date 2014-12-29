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

import static org.junit.Assert.assertEquals;

import com.github.prasanthj.cmsketch.CountMinSketch;

import org.junit.Test;

import java.util.Random;

/**
 *
 */
public class TestCountMinSketch {

  @Test
  public void testWidth() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(272, cms.getWidth());
  }

  @Test
  public void testDepth() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(5, cms.getDepth());
  }

  @Test
  public void testSizeInBytes() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(5448, cms.getSizeInBytes());
    cms = new CountMinSketch(1024, 10);
    assertEquals(40968, cms.getSizeInBytes());
  }

  @Test
  public void testCMSketch() {
    CountMinSketch cms = new CountMinSketch(1024, 10);
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("HelloWorld".getBytes());
    assertEquals(4, cms.getEstimatedCount("Hello".getBytes()));
    assertEquals(1, cms.getEstimatedCount("HelloWorld".getBytes()));

    int[] actualFreq = new int[100];
    Random rand = new Random(123);
    CountMinSketch cms3 = new CountMinSketch();
    for (int i = 0; i < 10000; i++) {
      int idx = rand.nextInt(actualFreq.length);
      cms3.setInt(idx);
      actualFreq[idx] += 1;
    }

    assertEquals(actualFreq[10], cms3.getEstimatedCountInt(10), 0.01);
    assertEquals(actualFreq[20], cms3.getEstimatedCountInt(20), 0.01);
    assertEquals(actualFreq[30], cms3.getEstimatedCountInt(30), 0.01);
    assertEquals(actualFreq[40], cms3.getEstimatedCountInt(40), 0.01);
    assertEquals(actualFreq[50], cms3.getEstimatedCountInt(50), 0.01);
    assertEquals(actualFreq[60], cms3.getEstimatedCountInt(60), 0.01);
  }

  @Test
  public void testMerge() {
    CountMinSketch cms = new CountMinSketch(1024, 10);
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    CountMinSketch cms2 = new CountMinSketch(1024, 10);
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms.merge(cms2);
    assertEquals(8, cms.getEstimatedCountString("Hello"));

    int[] actualFreq = new int[100];
    Random rand = new Random(123);
    CountMinSketch cms3 = new CountMinSketch();
    for (int i = 0; i < 10000; i++) {
      int idx = rand.nextInt(actualFreq.length);
      cms3.setInt(idx);
      actualFreq[idx] += 1;
    }

    assertEquals(actualFreq[10], cms3.getEstimatedCountInt(10), 0.01);
    assertEquals(actualFreq[20], cms3.getEstimatedCountInt(20), 0.01);
    assertEquals(actualFreq[30], cms3.getEstimatedCountInt(30), 0.01);
    assertEquals(actualFreq[40], cms3.getEstimatedCountInt(40), 0.01);
    assertEquals(actualFreq[50], cms3.getEstimatedCountInt(50), 0.01);
    assertEquals(actualFreq[60], cms3.getEstimatedCountInt(60), 0.01);

    int[] actualFreq2 = new int[100];
    rand = new Random(321);
    CountMinSketch cms4 = new CountMinSketch();
    for (int i = 0; i < 10000; i++) {
      int idx = rand.nextInt(actualFreq2.length);
      cms4.setInt(idx);
      actualFreq2[idx] += 1;
    }
    cms3.merge(cms4);

    assertEquals(actualFreq[10] + actualFreq2[10], cms3.getEstimatedCountInt(10), 0.01);
    assertEquals(actualFreq[20] + actualFreq2[20], cms3.getEstimatedCountInt(20), 0.01);
    assertEquals(actualFreq[30] + actualFreq2[30], cms3.getEstimatedCountInt(30), 0.01);
    assertEquals(actualFreq[40] + actualFreq2[40], cms3.getEstimatedCountInt(40), 0.01);
    assertEquals(actualFreq[50] + actualFreq2[50], cms3.getEstimatedCountInt(50), 0.01);
    assertEquals(actualFreq[60] + actualFreq2[60], cms3.getEstimatedCountInt(60), 0.01);
  }

  @Test(expected = RuntimeException.class)
  public void testIncompatibleMerge() {
    CountMinSketch cms = new CountMinSketch(1024, 10);
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    CountMinSketch cms2 = new CountMinSketch(1024, 11);
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");

    // should throw exception
    cms.merge(cms2);
  }

  @Test
  public void testSerialization() {
    CountMinSketch cms = new CountMinSketch(1024, 10);
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    byte[] serialized = CountMinSketch.serialize(cms);
    assertEquals(cms.getSizeInBytes(), serialized.length);

    CountMinSketch cms2 = CountMinSketch.deserialize(serialized);
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms2.setString("Hello");
    cms.merge(cms2);
    assertEquals(cms.getSizeInBytes(), serialized.length);

    int[] actualFreq = new int[100];
    Random rand = new Random(123);
    CountMinSketch cms3 = new CountMinSketch();
    for (int i = 0; i < 10000; i++) {
      int idx = rand.nextInt(actualFreq.length);
      cms3.setInt(idx);
      actualFreq[idx] += 1;
    }

    assertEquals(actualFreq[10], cms3.getEstimatedCountInt(10), 0.01);
    assertEquals(actualFreq[20], cms3.getEstimatedCountInt(20), 0.01);
    assertEquals(actualFreq[30], cms3.getEstimatedCountInt(30), 0.01);
    assertEquals(actualFreq[40], cms3.getEstimatedCountInt(40), 0.01);
    assertEquals(actualFreq[50], cms3.getEstimatedCountInt(50), 0.01);
    assertEquals(actualFreq[60], cms3.getEstimatedCountInt(60), 0.01);

    serialized = CountMinSketch.serialize(cms3);
    CountMinSketch cms4 = CountMinSketch.deserialize(serialized);
    assertEquals(actualFreq[10], cms4.getEstimatedCountInt(10), 0.01);
    assertEquals(actualFreq[20], cms4.getEstimatedCountInt(20), 0.01);
    assertEquals(actualFreq[30], cms4.getEstimatedCountInt(30), 0.01);
    assertEquals(actualFreq[40], cms4.getEstimatedCountInt(40), 0.01);
    assertEquals(actualFreq[50], cms4.getEstimatedCountInt(50), 0.01);
    assertEquals(actualFreq[60], cms4.getEstimatedCountInt(60), 0.01);

    cms4.setInt(Integer.MAX_VALUE);
    cms4.setInt(Integer.MAX_VALUE);
    cms4.setInt(Integer.MAX_VALUE);
    cms4.setInt(Integer.MIN_VALUE);
    cms4.setInt(Integer.MIN_VALUE);
    assertEquals(3, cms4.getEstimatedCountInt(Integer.MAX_VALUE), 0.01);
    assertEquals(2, cms4.getEstimatedCountInt(Integer.MIN_VALUE), 0.01);
  }

}
