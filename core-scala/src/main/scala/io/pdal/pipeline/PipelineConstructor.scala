/*
 * Copyright 2020 Azavea
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

import io.pdal.{LogLevel, Pipeline}
import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._

case class PipelineConstructor(list: List[PipelineExpr]) {
  def ~(e: PipelineExpr): PipelineConstructor =
    e match {
      case ENil => this
      case _    => PipelineConstructor(list :+ e)
    }
  def ~(e: Option[PipelineExpr]): PipelineConstructor = e.fold(this)(this ~ _)
  def map[B](f: PipelineExpr => B): List[B] = list.map(f)
  def toPipeline: Pipeline = Pipeline(this.asJson.noSpaces)
  def toPipeline(logLevel: LogLevel.Value): Pipeline = Pipeline(this.asJson.noSpaces, logLevel)
}

object PipelineConstructor {
  implicit val pipelineConstructorEncoder: Encoder[PipelineConstructor] = Encoder.instance { constructor =>
    Json.obj(
      "pipeline" -> constructor.list.flatMap {
        _.list
          .flatMap {
            case RawExpr(json) => json.asObject
            case expr          => expr.asJson.asObject
          }
          .map {
            _.remove("class_type") // remove type
              .filter { case (_, value) => !value.isNull } // cleanup options
          }
      }.asJson
    )
  }
  implicit val pipelineConstructorDecoder: Decoder[PipelineConstructor] = Decoder.instance {
    _.downField("pipeline").as[PipelineConstructor]
  }

  implicit def pipelineConstructorToJson(expr: PipelineConstructor): Json = expr.asJson
  implicit def pipelineConstructorToString(expr: PipelineConstructor): String = expr.asJson.noSpaces
}
