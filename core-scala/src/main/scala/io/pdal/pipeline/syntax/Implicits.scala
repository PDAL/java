/*
 * Copyright 2017 Azavea
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

package io.pdal.pipeline.syntax

import io.pdal._
import org.locationtech.jts.geom.Coordinate

import java.nio.ByteBuffer
import java.util

object Implicits extends Implicits with Serializable

trait Implicits {
  implicit class withPointCloudMethods(self: PointCloud) {
    def getCoordinate(i: Int) = new Coordinate(self.getX(i), self.getY(i), self.getZ(i))
    def get(idx: Int, dims: SizedDimType*): ByteBuffer =
      self.get(idx, dims.toArray)
    def get(idx: Int, dims: DimType*)(implicit d0: DummyImplicit): ByteBuffer =
      self.get(idx, dims.toArray)
    def get(idx: Int, dims: String*)(implicit d0: DummyImplicit, d1: DummyImplicit): ByteBuffer =
      self.get(idx, dims.toArray)
  }

  implicit class withPointViewMethods(self: PointView) {
    def getPointCloud(dims: DimType*): PointCloud = self.getPointCloud(dims.toArray)
    def get(idx: Int, packedPoints: Array[Byte], dims: DimType*): Array[Byte] = self.get(idx, packedPoints, dims.toArray)
    def getPackedPoint(idx: Long, dims: DimType*): Array[Byte] = self.getPackedPoint(idx, dims.toArray)
    def getPackedPoints(dims: DimType*): Array[Byte] = self.getPackedPoints(dims.toArray)
  }

  implicit class withPointLayoutMethods(self: PointLayout) {
    def toSizedDimTypes(dimTypes: DimType*): util.Map[String, SizedDimType] =
      self.toSizedDimTypes(dimTypes.toArray)
  }
}
