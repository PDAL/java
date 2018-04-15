package com.azavea

import io.pdal.pipeline._
import io.pdal.Pipeline

import io.circe._
import io.circe.parser._

object MainScala {
  // to check laz "filename":"data/autzen_trim.laz"
  val jsonExpected =
    """
      |{
      |  "pipeline":[
      |    {
      |      "type" : "readers.las",
      |      "filename":"data/1.2-with-color.las",
      |      "spatialreference":"EPSG:2993"
      |    },
      |    {
      |      "type": "filters.reprojection",
      |      "out_srs": "EPSG:3857"
      |    }
      |  ]
      |}
    """.stripMargin

  val pipelineExpr = LasRead(
    filename = "data/1.2-with-color.las",
    spatialreference = Some("EPSG:2993")
  ) ~ ReprojectionFilter(outSrs = "EPSG:3857")

  def main(args: Array[String]) = {
    // check that pipelineExpr corresponds to a raw json definition
    parse(jsonExpected) match {
      case Right(r) =>
        val json: Json = pipelineExpr
        assert(json == r)
      case Left(e) => throw e
    }

    val pipeline: Pipeline = pipelineExpr.toPipeline
    pipeline.execute()
    println(s"pipeline.getMetadata(): ${pipeline.getMetadata()}")
    pipeline.dispose()
  }
}
