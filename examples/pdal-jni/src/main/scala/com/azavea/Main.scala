package com.azavea

import io.pdal.Pipeline

object Main {
  // to check laz "filename":"data/autzen_trim.laz"
  val json =
    """
      |{
      |  "pipeline":[
      |    {
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

  def main(args: Array[String]) = {
    val pipeline = Pipeline(json)
    pipeline.execute()
    println(s"pipeline.getMetadata(): ${pipeline.getMetadata()}")
    pipeline.dispose()
  }
}
