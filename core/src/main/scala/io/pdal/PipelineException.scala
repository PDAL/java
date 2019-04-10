package io.pdal

abstract class PipelineException(msg: String) extends Exception(msg)

case class InitializationException(msg: String) extends PipelineException(msg)
case class ExecutionException(msg: String) extends PipelineException(msg)
