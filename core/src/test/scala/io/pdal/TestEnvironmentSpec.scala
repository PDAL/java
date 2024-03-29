/**
 * **************************************************************************** Copyright (c) 2016, hobu Inc.
 * (info@hobu.co)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided with the distribution. * Neither the
 * name of Hobu, Inc. nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.pdal

import io.circe.{parser, Json, ParsingFailure}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

trait TestEnvironmentSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {
  def getJson(resource: String): String = {
    val stream = getClass.getResourceAsStream(resource)
    val lines = scala.io.Source.fromInputStream(stream).getLines
    val json = lines.mkString(" ")
    stream.close()
    json
  }

  val json: String = getJson("/las.json")
  val jsonDelaunay: String = getJson("/las-delaunay.json")
  val badJson: String =
    """
      |{
      |  "pipeline": [
      |    "nofile.las",
      |    {
      |        "type": "filters.sort",
      |        "dimension": "X"
      |    }
      |  ]
      |}
     """.stripMargin

  val jsonExpectedJson: Either[ParsingFailure, Json] = parser.parse(getJson("/las-expected.json"))
  val schemaJson: Either[ParsingFailure, Json] = parser.parse(getJson("/schema.json"))
  val metadataJson: Either[ParsingFailure, Json] = parser.parse(getJson("/metadata.json"))
  val quickInfoJson: Either[ParsingFailure, Json] = parser.parse(getJson("/quick-info.json"))
  val quickInfoWithMetadataJson: Either[ParsingFailure, Json] = parser.parse(getJson("/quick-info-with-metadata.json"))
  val quickInfoWithMetadataMacJson: Either[ParsingFailure, Json] =
    parser.parse(getJson("/quick-info-with-metadata-mac.json"))

  val proj4String =
    "+proj=lcc +lat_0=41.75 +lon_0=-120.5 +lat_1=43 +lat_2=45.5 +x_0=400000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
  val proj4StringMac =
    "+proj=lcc +lat_0=41.75 +lon_0=-120.5 +lat_1=43 +lat_2=45.5 +x_0=400000 +y_0=0 +ellps=GRS80 +units=m +no_defs"

  val pipeline: Pipeline = Pipeline(json)
  val pipelineDelaunay: Pipeline = Pipeline(jsonDelaunay)

  val expectedDelaunayPlyTriangles: List[Triangle] = {
    val stream = getClass.getResourceAsStream("/delaunay.ply")
    val lines = scala.io.Source.fromInputStream(stream).getLines.drop(1088)
    val triangles =
      lines.map { l =>
        val List(a, b, c) = l.split(" ").toList.tail.map(_.toInt)
        Triangle(a, b, c)
      }.toList
    stream.close()
    triangles
  }

  override def afterAll(): Unit = { pipeline.close(); pipelineDelaunay.close() }
}
