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

import io.pdal.Pipeline

import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._
import io.circe.generic.extras.ConfiguredJsonCodec
import cats.syntax.either._

@ConfiguredJsonCodec
sealed trait PipelineExpr {
  def ~(other: PipelineExpr): PipelineConstructor =
    other match {
      case ENil => toPipelineConstructor
      case _    => PipelineConstructor(this :: other :: Nil)
    }
  def ~(other: Option[PipelineExpr]): PipelineConstructor = other.fold(toPipelineConstructor)(this ~ _)
  def toPipelineConstructor: PipelineConstructor = PipelineConstructor(this :: Nil)
  def toPipeline: Pipeline = toPipelineConstructor.toPipeline
}
object PipelineExpr {
  implicit def pipelineExprToConstructor(expr: PipelineExpr): PipelineConstructor = expr.toPipelineConstructor
  implicit def pipelineExprToJson(expr: PipelineExpr): Json = expr.asJson
}

case object ENil extends PipelineExpr

case class RawExpr(json: Json) extends PipelineExpr
object RawExpr {
  implicit val rawExprEncoder: Encoder[RawExpr] = Encoder.instance { _.json }
  implicit val rawExprDecoder: Decoder[RawExpr] = Decoder.decodeJson.emap { json =>
    Either.catchNonFatal(RawExpr(json)).leftMap(_ => "RawExpr")
  }
}

@ConfiguredJsonCodec
case class Read(
  filename: String,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: Option[ReaderType] = None // usually auto derived by pdal
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadBpf(
  filename: String,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.bpf
)

@ConfiguredJsonCodec
case class ReadEpt(
  filename: String,
  spatialreference: Option[String] = None,
  bounds: Option[String] = None,
  resolution: Option[Double] = None,
  addons: Option[Json] = None,
  origin: Option[String] = None,
  threads: Option[Int] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.ept
)

@ConfiguredJsonCodec
case class ReadE57(
  filename: String,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.e57
)

@ConfiguredJsonCodec
case class ReadFaux(
  numPoints: Int,
  mode: String, // constant | random | ramp | uniform | normal
  stdevX: Option[Int] = None, // [default: 1]
  stdevY: Option[Int] = None, // [default: 1]
  stdevZ: Option[Int] = None, // [default: 1]
  meanX: Option[Int] = None, // [default: 0]
  meanY: Option[Int] = None, // [default: 0]
  meanZ: Option[Int] = None, // [default: 0]
  bounds: Option[String] = None, // [default: unit cube]
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.faux
) extends PipelineExpr

object ReadGdal {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.gdal))
}

@ConfiguredJsonCodec
case class ReadGeoWave(
  zookeeperUrl: String,
  instanceName: String,
  username: String,
  password: String,
  tableNamespace: String,
  featureTypeName: Option[String] = None, // [default: PDAL_Point]
  dataAdapter: Option[String] = None, // [default: FeatureCollectionDataAdapter]
  pointsPerEntry: Option[String] = None, // [default: 5000u]
  bounds: Option[String] = None, // [default: unit cube]
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.geowave
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadI3s(
  filename: String,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  dimensions: Option[String] = None,
  bounds: Option[String] = None,
  minDensity: Option[Double] = None,
  maxDensity: Option[Double] = None,
  threads: Option[Int] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.i3s
)

@ConfiguredJsonCodec
case class ReadIlvis2(
  filename: String,
  mapping: Option[String] = None,
  metadata: Option[String] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.ilvis2
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadMatlab(
  filename: String,
  struct: Option[String] = None, // [default: PDAL]
  `type`: ReaderType = ReaderTypes.matlab
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadMbio(
  filename: String,
  format: String,
  `type`: ReaderType = ReaderTypes.mbio
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadLas(
  filename: String,
  extraDims: Option[String] = None,
  compression: Option[String] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  useEbVlr: Option[String] = None,
  `type`: ReaderType = ReaderTypes.las
) extends PipelineExpr

object ReadMrsid {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.mrsid))
}

object ReadNitf {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.nitf))
}

@ConfiguredJsonCodec
case class ReadNumpy(
  filename: String,
  dimension: Option[String] = None,
  x: Option[Int] = None,
  y: Option[Int] = None,
  z: Option[Int] = None,
  assignZ: Option[String] = None,
  `type`: ReaderType = ReaderTypes.numpy
)

@ConfiguredJsonCodec
case class ReadOci(
  connection: String,
  query: String,
  xmlSchemaDump: Option[String] = None,
  populatePointsourceid: Option[String] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.oci
) extends PipelineExpr

object ReadOptech {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.optech))
}

object ReadOsg {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.osg))
}

object ReadPcd {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.pcd))
}

@ConfiguredJsonCodec
case class ReadPgpointcloud(
  connection: String,
  table: String,
  schema: Option[String] = None, // [default: public]
  column: Option[String] = None, // [default: pa]
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.pgpointcloud
) extends PipelineExpr

object ReadPly {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.ply))
}

object ReadPts {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.pts))
}

@ConfiguredJsonCodec
case class ReadQfit(
  filename: String,
  flipCoordinates: Option[Boolean] = None,
  scaleZ: Option[Double] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.qfit
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadRdb(
  filename: String,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  filter: Option[String] = None,
  extras: Option[Boolean] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.rdb
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadRxp(
  filename: String,
  rdtp: Option[Boolean] = None,
  syncToPps: Option[Boolean] = None,
  minimal: Option[Boolean] = None,
  reflectanceAsIntensity: Option[Boolean] = None,
  minReflectance: Option[Double] = None,
  maxReflectance: Option[Double] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.rxp
) extends PipelineExpr

object ReadSbet {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.sbet))
}

@ConfiguredJsonCodec
case class ReadSlpk(
  filename: String,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  dimensions: Option[String] = None,
  bounds: Option[String] = None,
  minDensity: Option[Double] = None,
  maxDensity: Option[Double] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.slpk
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadSqlite(
  connection: String,
  query: String,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.sqlite
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadText(
  filename: String,
  separator: Option[String] = None,
  spatialreference: Option[String] = None,
  header: Option[String] = None,
  skip: Option[Int] = None,
  count: Option[Long] = None,
  `type`: ReaderType = ReaderTypes.text
) extends PipelineExpr

@ConfiguredJsonCodec
case class ReadTindex(
  filename: String,
  layerName: Option[String] = None,
  srsColumn: Option[String] = None,
  tindexName: Option[String] = None,
  sql: Option[String] = None,
  wkt: Option[String] = None,
  boundary: Option[String] = None,
  tSrs: Option[String] = None,
  filterSrs: Option[String] = None,
  where: Option[String] = None,
  dialect: Option[String] = None,
  spatialreference: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.tindex
) extends PipelineExpr

object ReadTerrasolid {
  def apply(filename: String, spatialreference: Option[String] = None, tag: Option[String] = None): Read =
    Read(filename, spatialreference, tag, Some(ReaderTypes.terrasolid))
}

@ConfiguredJsonCodec
case class ReadTiledb(
  arrayName: String,
  configName: Option[String] = None,
  chunkSize: Option[Int] = None,
  stats: Option[String] = None,
  bbox3d: Option[String] = None,
  count: Option[Int] = None,
  overrideSrs: Option[String] = None,
  tag: Option[String] = None,
  `type`: ReaderType = ReaderTypes.tiledb
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterApproximateCoplanar(
  knn: Option[Int] = None, // [default: 8]
  thresh1: Option[Int] = None, // [default: 25]
  thresh2: Option[Int] = None, // [default: 6]
  `type`: FilterType = FilterTypes.approximatecoplanar
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterAssign(
  assignment: Option[String] = None,
  condition: Option[String] = None,
  `type`: FilterType = FilterTypes.assign
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterChipper(
  capacity: Option[Int] = None, // [default: 5000]
  `type`: FilterType = FilterTypes.chipper
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterCluster(
  minPoints: Option[Int] = None, // [default: 1]
  maxPoints: Option[Int] = None, // [default: UINT64_MAX]
  tolerance: Option[Double] = None, // [default: 1.0]
  `type`: FilterType = FilterTypes.cluster
)

@ConfiguredJsonCodec
case class FilterColorinterp(
  ramp: Option[String] = None, // [default: pestel_shades]
  dimension: Option[String] = None, // [default: Z]
  minimum: Option[String] = None,
  maximum: Option[String] = None,
  invert: Option[Boolean] = None, // [default: false]
  k: Option[Double] = None,
  mad: Option[Boolean] = None,
  madMultiplier: Option[Double] = None,
  `type`: FilterType = FilterTypes.colorinterp
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterColorization(
  raster: String,
  dimensions: Option[String] = None,
  `type`: FilterType = FilterTypes.colorization
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterComputerange(
  `type`: FilterType = FilterTypes.computerange
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterCovariancefeatures(
  knn: Option[Int] = None,
  threads: Option[Int] = None,
  featureSet: Option[String] = None,
  stride: Option[String] = None,
  `type`: FilterType = FilterTypes.covariancefeatures
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterCpd(
  method: Option[String] = None,
  `type`: FilterType = FilterTypes.cpd
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterCrop(
  bounds: Option[String] = None,
  polygon: Option[String] = None,
  outside: Option[String] = None,
  point: Option[String] = None,
  radius: Option[String] = None,
  `type`: FilterType = FilterTypes.crop
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterDecimation(
  step: Option[Int] = None,
  offset: Option[Int] = None,
  limit: Option[Int] = None,
  `type`: FilterType = FilterTypes.decimation
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterDem(
   raster: String,
   limits: String,
   band: Option[Int] = None,
  `type`: FilterType = FilterTypes.dem
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterDelaunay(
  `type`: FilterType = FilterTypes.delaunay
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterDivider(
   mode: Option[String] = None,
   count: Option[Int] = None,
   capacity: Option[Int] = None,
  `type`: FilterType = FilterTypes.divider
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterEigenValues(
  knn: Option[Int] = None,
  `type`: FilterType = FilterTypes.eigenvalues
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterEstimateRank(
  knn: Option[Int] = None,
  thresh: Option[Double] = None,
  `type`: FilterType = FilterTypes.estimaterank
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterElm(
  cell: Option[Double] = None,
  `class`: Option[Int] = None,
  threshold: Option[Double] = None,
  `type`: FilterType = FilterTypes.elm
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterFerry(
  dimensions: String,
  `type`: FilterType = FilterTypes.ferry
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterGreedyProjection(
  `type`: FilterType = FilterTypes.greedyprojection
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterGridProjection(
  `type`: FilterType = FilterTypes.gridprojection
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterGroupBy(
  dimension: String,
  `type`: FilterType = FilterTypes.groupby
)

@ConfiguredJsonCodec
case class FilterHag(
  `type`: FilterType = FilterTypes.hag
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterHead(
  count: Option[Int] = None, // [default: 10]
  `type`: FilterType = FilterTypes.head
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterHexbin(
  edgeSize: Option[Int] = None,
  sampleSize: Option[Int] = None,
  threshold: Option[Int] = None,
  precision: Option[Int] = None,
  `type`: FilterType = FilterTypes.hexbin
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterInfo(
  point: Option[String] = None,
  query: Option[String] = None,
  `type`: FilterType = FilterTypes.info
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterIcp(
  `type`: FilterType = FilterTypes.icp
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterIqr(
  dimension: String,
  k: Option[Double] = None,
  `type`: FilterType = FilterTypes.iqr
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterKDistance(
  k: Option[Int] = None,
  `type`: FilterType = FilterTypes.kdistance
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterLocate(
  dimension: String,
  minmax: String,
  `type`: FilterType = FilterTypes.locate
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterLof(
  minpts: Option[Int] = None,
  `type`: FilterType = FilterTypes.lof
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMad(
  dimension: String,
  k: Option[Double] = None,
  `type`: FilterType = FilterTypes.mad
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMatlab(
  script: String,
  source: String,
  addDimension: Option[String] = None,
  struct: Option[String] = None, // [default: PDAL]
  `type`: FilterType = FilterTypes.matlab
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMerge(
  inputs: List[String],
  tag: Option[String] = None,
  `type`: FilterType = FilterTypes.merge
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMongus(
  cell: Option[Double] = None,
  classify: Option[Boolean] = None,
  extract: Option[Boolean] = None,
  k: Option[Double] = None,
  l: Option[Int] = None,
  `type`: FilterType = FilterTypes.mongus
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMortonOrder(
  reverse: Option[String] = None,
  `type`: FilterType = FilterTypes.mortonorder
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMovingLeastSquares(
  `type`: FilterType = FilterTypes.movingleastsquares
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterMiniball(
  knn: Option[Int] = None,
  `type`: FilterType = FilterTypes.miniball
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterNeighborclassifier(
  candidate: Option[String] = None,
  domain: Option[String] = None,
  k: Option[Int] = None,
  `type`: FilterType = FilterTypes.neighborclassifier
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterNndistance(
  mode: Option[String] = None,
  k: Option[Int] = None,
  `type`: FilterType = FilterTypes.nndistance
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterNormal(
  knn: Option[Int] = None,
  `type`: FilterType = FilterTypes.normal
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterOutlier(
  method: Option[String] = None,
  minK: Option[Int] = None,
  radius: Option[Double] = None,
  meanK: Option[Int] = None,
  multiplier: Option[Double] = None,
  `type`: FilterType = FilterTypes.outlier
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterOverlay(
  dimension: Option[String] = None, // [default: none]
  datasource: Option[String] = None, // [default: none]
  column: Option[String] = None, // [default: none]
  query: Option[String] = None, // [default: first column]
  layer: Option[String] = None, // [default: first layer]
  `type`: FilterType = FilterTypes.overlay
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterPclBlock(
  filename: String,
  methods: Option[List[String]] = None,
  `type`: FilterType = FilterTypes.pclblock
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterPlanefit(
  knn: Option[Int] = None,
  threads: Option[Int] = None,
  `type`: FilterType = FilterTypes.planefit
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterPmf(
  maxWindowSize: Option[Int] = None,
  slope: Option[Double] = None,
  maxDistance: Option[Double] = None,
  initialDistance: Option[Double] = None,
  cellSize: Option[Int] = None,
  exponential: Option[Boolean] = None, // [default: true]
  `type`: FilterType = FilterTypes.pmf
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterPoisson(
  depth: Option[Int] = None,
  pointWeight: Option[Double] = None,
  `type`: FilterType = FilterTypes.poisson
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterPython(
  module: String,
  function: String,
  script: Option[String] = None,
  source: Option[String] = None,
  addDimension: Option[String] = None,
  pdalargs: Option[String] = None,
  `type`: FilterType = FilterTypes.python
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterRadialDensity(
  radius: Option[Double] = None,
  `type`: FilterType = FilterTypes.radialdensity
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterRandomize(
  `type`: FilterType = FilterTypes.randomize
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterRange(
  limits: Option[String] = None,
  `type`: FilterType = FilterTypes.range
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterReciprocity(
  knn: Option[Int] = None,
  `type`: FilterType = FilterTypes.reciprocity
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterReprojection(
  outSrs: String,
  inSrs: Option[String] = None,
  tag: Option[String] = None,
  `type`: FilterType = FilterTypes.reprojection
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterSample(
  radius: Option[Double] = None,
  `type`: FilterType = FilterTypes.sample
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterSmrf(
  cell: Option[Double] = None,
  classify: Option[Boolean] = None,
  cut: Option[Double] = None,
  extract: Option[Boolean] = None,
  slope: Option[Double] = None,
  threshold: Option[Double] = None,
  window: Option[Double] = None,
  `type`: FilterType = FilterTypes.smrf
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterSort(
  dimension: String,
  `type`: FilterType = FilterTypes.sort
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterSplitter(
  length: Option[Int] = None,
  originX: Option[Double] = None,
  originY: Option[Double] = None,
  `type`: FilterType = FilterTypes.splitter
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterStats(
  dimenstions: Option[String] = None,
  enumerate: Option[String] = None,
  count: Option[Int] = None,
  `type`: FilterType = FilterTypes.stats
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterTail(
  count: Option[Int] = None, // [default: 10]
  `type`: FilterType = FilterTypes.tail
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterTransformation(
  matrix: String,
  `type`: FilterType = FilterTypes.transformation
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterVoxelCenterNearestNeighbor(
  cell: Option[Double] = None, // [default: 1.0]
  `type`: FilterType = FilterTypes.voxelcenternearestneighbor
) extends PipelineExpr

@ConfiguredJsonCodec
case class VoxelCentroidNearestNeighbor(
  cell: Option[Double] = None, // [default: 1.0]
  `type`: FilterType = FilterTypes.voxelcentroidnearestneighbor
) extends PipelineExpr

@ConfiguredJsonCodec
case class FilterVoxelGrid(
  leafX: Option[Double] = None,
  leafY: Option[Double] = None,
  leafZ: Option[Double] = None,
  `type`: FilterType = FilterTypes.voxelgrid
) extends PipelineExpr

@ConfiguredJsonCodec
case class Write(
  filename: String,
  `type`: Option[WriterType] = None // usually auto derived by pdal
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteBpf(
  filename: String,
  compression: Option[Boolean] = None,
  format: Option[String] = None,
  bundledfile: Option[String] = None,
  headerData: Option[String] = None,
  coordId: Option[String] = None,
  scaleX: Option[Double] = None,
  scaleY: Option[Double] = None,
  scaleZ: Option[Double] = None,
  offsetX: Option[String] = None,
  offsetY: Option[String] = None,
  offsetZ: Option[String] = None,
  outputDims: Option[String] = None,
  `type`: WriterType = WriterTypes.bpf
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteGdal(
  filename: String,
  resolution: Int,
  radius: Double,
  gdaldriver: Option[String] = None,
  gdalopts: Option[String] = None,
  outputType: Option[String] = None,
  windowSize: Option[Int] = None,
  dimension: Option[String] = None,
  `type`: WriterType = WriterTypes.gdal
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteGeoWave(
  zookeeperUrl: String,
  instanceName: String,
  username: String,
  password: String,
  tableNamespace: String,
  featureTypeName: Option[String] = None,
  dataAdapter: Option[String] = None,
  pointsPerEntry: Option[String] = None, // [default: 5000u]
  `type`: WriterType = WriterTypes.geowave
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteLas(
  filename: String,
  forward: Option[String] = None,
  minorVersion: Option[Int] = None,
  softwareId: Option[String] = None,
  creationDoy: Option[Int] = None,
  creationYear: Option[Int] = None,
  dataformatId: Option[Int] = None,
  systemId: Option[String] = None,
  aSrs: Option[String] = None,
  globalEncoding: Option[String] = None,
  projectId: Option[String] = None,
  compression: Option[String] = None,
  scaleX: Option[Double] = None,
  scaleY: Option[Double] = None,
  scaleZ: Option[Double] = None,
  offsetX: Option[String] = None,
  offsetY: Option[String] = None,
  offsetZ: Option[String] = None,
  filesourceId: Option[Int] = None,
  discardHighReturnNumbers: Option[Boolean] = None,
  `type`: WriterType = WriterTypes.las
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteMatlab(
  filename: String,
  outputDims: Option[String] = None,
  `type`: WriterType = WriterTypes.matlab
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteNitf(
  filename: String,
  clevel: Option[String] = None,
  stype: Option[String] = None,
  ostaid: Option[String] = None,
  ftitle: Option[String] = None,
  fscalas: Option[String] = None,
  oname: Option[String] = None,
  ophone: Option[String] = None,
  fsctlh: Option[String] = None,
  fsclsy: Option[String] = None,
  idatim: Option[String] = None,
  iid2: Option[String] = None,
  fscltx: Option[String] = None,
  aimidb: Option[String] = None,
  acftb: Option[String] = None,
  `type`: WriterType = WriterTypes.nitf
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteNull(
  `type`: WriterType = WriterTypes.`null`
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteOci(
  connection: String,
  is3d: Option[Boolean] = None,
  solid: Option[Boolean] = None,
  overwrite: Option[Boolean] = None,
  verbose: Option[Boolean] = None,
  srid: Option[Int] = None,
  capacity: Option[Int] = None,
  streamOutputPrecision: Option[Int] = None,
  cloudId: Option[Int] = None,
  blockTableName: Option[String] = None,
  blockTablePartitionValue: Option[Int] = None,
  baseTableName: Option[String] = None,
  cloudColumnName: Option[String] = None,
  baseTableAuxColumns: Option[String] = None,
  baseTableAuxValues: Option[String] = None,
  baseTableBoundaryColumn: Option[String] = None,
  baseTableBoundaryWkt: Option[String] = None,
  preBlockSql: Option[String] = None,
  preSql: Option[String] = None,
  postBlockSql: Option[String] = None,
  baseTableBounds: Option[String] = None,
  pcId: Option[Int] = None,
  packIgnoredFields: Option[Boolean] = None,
  streamChunks: Option[Boolean] = None,
  blobChunkCount: Option[Int] = None,
  scaleX: Option[Double] = None,
  scaleY: Option[Double] = None,
  scaleZ: Option[Double] = None,
  offsetX: Option[Double] = None,
  offsetY: Option[Double] = None,
  offsetZ: Option[Double] = None,
  outputDims: Option[String] = None,
  `type`: WriterType = WriterTypes.oci
) extends PipelineExpr

@ConfiguredJsonCodec
case class WritePcd(
  filename: String,
  compression: Option[Boolean] = None,
  `type`: WriterType = WriterTypes.pcd
) extends PipelineExpr

@ConfiguredJsonCodec
case class WritePgpointcloud(
  connection: String,
  table: String,
  schema: Option[String] = None,
  column: Option[String] = None,
  compression: Option[String] = None,
  overwrite: Option[Boolean] = None,
  srid: Option[Int] = None,
  pcid: Option[Int] = None,
  preSql: Option[String] = None,
  postSql: Option[String] = None,
  scaleX: Option[Double] = None,
  scaleY: Option[Double] = None,
  scaleZ: Option[Double] = None,
  offsetX: Option[Double] = None,
  offsetY: Option[Double] = None,
  offsetZ: Option[Double] = None,
  outputDims: Option[String] = None,
  `type`: WriterType = WriterTypes.pgpointcloud
) extends PipelineExpr

@ConfiguredJsonCodec
case class WritePly(
  filename: String,
  storageMode: Option[String] = None,
  `type`: WriterType = WriterTypes.ply
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteRialto(
  filename: String,
  maxLevels: Option[Int] = None,
  overwrite: Option[Boolean] = None,
  `type`: WriterType = WriterTypes.rialto
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteSqlite(
  filename: String,
  cloudTableName: String,
  blockTableName: String,
  cloudColumnName: Option[String] = None,
  compression: Option[String] = None,
  overwrite: Option[Boolean] = None,
  preSql: Option[String] = None,
  postSql: Option[String] = None,
  scaleX: Option[Double] = None,
  scaleY: Option[Double] = None,
  scaleZ: Option[Double] = None,
  offsetX: Option[Double] = None,
  offsetY: Option[Double] = None,
  offsetZ: Option[Double] = None,
  outputDims: Option[String] = None,
  `type`: WriterType = WriterTypes.sqlite
) extends PipelineExpr

@ConfiguredJsonCodec
case class WriteText(
  filename: String,
  format: Option[String] = None,
  order: Option[String] = None,
  precision: Option[Int] = None,
  keepUnspecified: Option[Boolean] = None,
  jscallback: Option[String] = None,
  quoteHeader: Option[String] = None,
  newline: Option[String] = None,
  delimiter: Option[String] = None,
  `type`: WriterType = WriterTypes.text
) extends PipelineExpr
