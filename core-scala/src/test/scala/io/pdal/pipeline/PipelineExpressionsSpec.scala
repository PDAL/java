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

package io.pdal.pipeline

import io.circe.syntax._
import io.circe.parser._

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PipelineExpressionsSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {
  describe("Pipeline Expressions spec") {
    it("should print a correct json, using DSL") {
      val expected =
        """
          |{
          |  "pipeline" : [
          |    {
          |      "filename" : "/path/to/las",
          |      "type" : "readers.las"
          |    },
          |    {
          |      "type" : "filters.crop"
          |    },
          |    {
          |      "filename" : "/path/to/new/las",
          |      "type" : "writers.las"
          |    }
          |  ]
          |}
        """.stripMargin

      val pc = ReadLas("/path/to/las") ~ FilterCrop() ~ WriteLas("/path/to/new/las")
      val pipelineJson = pc.asJson

      parse(expected) match {
        case Right(r) => pipelineJson shouldBe r
        case Left(e)  => throw e
      }
    }

    it("should print a correct json, using RAW JSON") {
      val expected =
        """
          |{
          |  "pipeline" : [
          |    {
          |      "filename" : "/path/to/las",
          |      "type" : "readers.las"
          |    },
          |    {
          |      "type" : "filters.crop"
          |    },
          |    {
          |      "filename" : "/path/to/new/las",
          |      "type" : "writers.las"
          |    }
          |  ]
          |}
        """.stripMargin

      val pipelineJson =
        ReadLas("/path/to/las") ~ RawExpr(Map("type" -> "filters.crop").asJson) ~ WriteLas("/path/to/new/las") asJson

      parse(expected) match {
        case Right(r) => pipelineJson shouldBe r
        case Left(e)  => throw e
      }
    }

    it("inductive constructor should work with ENil properly") {
      (ReadLas("/path/to/las") ~ ENil asJson) shouldBe (ReadLas("/path/to/las").toPipelineConstructor asJson)
      (ReadLas("/path/to/las") ~ FilterCrop() ~ WriteLas("/path/to/new/las") ~ ENil asJson) shouldBe (ReadLas(
        "/path/to/las"
      ) ~ FilterCrop() ~ WriteLas("/path/to/new/las") asJson)
    }

    it("should execute the pipeline built from the Scala DSL") {
      val expression =
        ReadLas("./core/src/test/resources/1.2-with-color.las", spatialreference = Some("EPSG:2993")) ~
          FilterReprojection(outSrs = "EPSG:3857")

      val pipeline = expression.toPipeline
      pipeline.execute()
      pipeline.close()
    }
  }
}
