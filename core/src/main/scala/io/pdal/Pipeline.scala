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

import com.github.sbt.jni.syntax.NativeLoader

class Pipeline private (val json: String, val logLevel: Int) extends Native {
  Pipeline // reference companion object so nativeLoader loads the JNI native libraries

  def this(json: String, logLevel: LogLevel.Value = LogLevel.Error) = {
    this(json, logLevel.id); initialize()
  }

  @native def initialize(): Unit
  @native def execute(): Unit
  @native def getPointViews(): PointViewIterator
  @native def close(): Unit
  @native def getSrsWKT2(): String
  @native def getPipeline(): String
  @native def getMetadata(): String
  @native def getSchema(): String
  @native def getQuickInfo(): String
  @native private def getLogLevelInt(): Int

  def getLogLevel(): LogLevel.Value = LogLevel.apply(getLogLevelInt())
}

object Pipeline extends NativeLoader("pdaljni.2.6") {
  def apply(json: String, logLevel: LogLevel.Value = LogLevel.Error): Pipeline =
    new Pipeline(json, logLevel)
}
