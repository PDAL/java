package com.azavea

import io.pdal.Pipeline

import io.pdal._

object MainLogging {
  val json =
    """
      |{
      |  "pipeline": [
      |    {
      |      "filename": "data/issue-79.las",
      |      "type": "readers.las",
      |      "spatialreference": "EPSG:2056"
      |    },
      |    {
      |      "distance": 250,
      |      "type": "filters.crop",
      |      "point": "POINT(2717706.2 1103466.2 677.54)"
      |    },
      |    {
      |      "count": 100000,
      |      "decay": 0.9,
      |      "type": "filters.relaxationdartthrowing",
      |      "radius": 1
      |    }
      |  ]
      |}
    """.stripMargin

  def main(args: Array[String]) = {
    val pipeline = Pipeline(json, LogLevel.Debug)
    pipeline.execute()
    println(s"pipeline.getMetadata(): ${pipeline.getMetadata()}")
    pipeline.close()
  }
}
