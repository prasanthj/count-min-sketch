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

/**
 *
 */
public class TestCountMinSketch {

  @Test
  public void testWidth() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(91, cms.getWidth());
  }

  @Test
  public void testDepth() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(4, cms.getDepth());
  }

  @Test
  public void testSizeInBytes() {
    CountMinSketch cms = new CountMinSketch();
    assertEquals(4 * 91 * 4, cms.getSizeInBytes());
    cms = new CountMinSketch(1024, 10);
    assertEquals(40960, cms.getSizeInBytes());
  }

  @Test
  public void testCMSketch() {
    CountMinSketch cms = new CountMinSketch(1024, 10);
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("Hello".getBytes());
    cms.set("HelloWorld".getBytes());
    assertEquals(4, cms.get("Hello".getBytes()));
    assertEquals(1, cms.get("HelloWorld".getBytes()));
  }
}
