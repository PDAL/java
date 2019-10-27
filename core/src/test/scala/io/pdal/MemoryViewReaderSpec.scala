/******************************************************************************
  * Copyright (c) 2016, hobu Inc.  (info@hobu.co)
  *
  * All rights reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following
  * conditions are met:
  *
  *     * Redistributions of source code must retain the above copyright
  *       notice, this list of conditions and the following disclaimer.
  *     * Redistributions in binary form must reproduce the above copyright
  *       notice, this list of conditions and the following disclaimer in
  *       the documentation and/or other materials provided
  *       with the distribution.
  *     * Neither the name of Hobu, Inc. nor the names of its
  *       contributors may be used to endorse or promote products derived
  *       from this software without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
  * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
  * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
  * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
  * OF SUCH DAMAGE.
  ****************************************************************************/

package io.pdal

class MemoryViewReaderSpec extends TestEnvironmentSpec {
  describe("Pipeline execution with data read via MemoryViewReader") {
    it("should allocate MemoryViewReader") {
      val reader = MemoryViewReader()
      reader.ptr() shouldNot be (0)
      reader.dispose()
      reader.ptr() shouldBe 0
    }

    it("should allocate PointView") {
      val pv = PointView()
      val l = pv.layout()
      pv.ptr() shouldNot be (0)
      l.ptr() shouldNot be (0)
      l.dispose()
      pv.dispose()
      l.ptr() shouldBe 0
      pv.ptr() shouldBe 0
    }

    it("should register DimType for a new layout"){
      val pv = PointView()
      val l = pv.layout()
      l.registerDim(DimType.X)
      pv.setField(DimType.X, 0, 20)
      val pc = pv.getPointCloud(0)
      println(s"pc.getDouble(0, DimType.X): ${pc.getDouble(0, DimType.X)}")

      l.dispose()
      pv.dispose()
    }

    it("should push a field") {
      val reader = MemoryViewReader()
      reader.pushField(Field("X", DimType.X, 0))
      reader.dispose()
    }
  }
}
