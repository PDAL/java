/******************************************************************************
* Copyright (c) 2016, hobu Inc.  (info@hobu.co)
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following
* conditions are met:
*
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in
*       the documentation and/or other materials provided
*       with the distribution.
*     * Neither the name of Hobu, Inc. nor the names of its
*       contributors may be used to endorse or promote products derived
*       from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
* COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
* AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
* OF SUCH DAMAGE.
****************************************************************************/

#include <stdio.h>
#include <vector>
#include "io_pdal_MemoryViewReader.h"
#include <pdal/io/MemoryViewReader.hpp>
#include "Accessors.hpp"
#include "Exceptions.hpp"

using pdal::MemoryViewReader;
using pdal::pdal_error;

/*
 * Class:     io_pdal_MemoryViewReader
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_MemoryViewReader_initialize
  (JNIEnv *env, jobject obj)
{
  try
  {
      auto r = new MemoryViewReader();
      setHandle(env, obj, r);
  } 
  catch (const pdal_error &pe)
  {
       throwInitializationException(env, pe.what());
  }
}

/*
 * Class:     io_pdal_MemoryViewReader
 * Method:    pushField
 * Signature: (Lio/pdal/Field;)V
 */
JNIEXPORT void JNICALL Java_io_pdal_MemoryViewReader_pushField
  (JNIEnv *env, jobject obj, jobject field)
{
  jclass c = env->GetObjectClass(field);
  
  jfieldID fname = env->GetFieldID(c, "name", "Ljava/lang/String;");
  jstring jname = reinterpret_cast<jstring>(env->GetObjectField(field, fname));

  std::string sname = std::string(env->GetStringUTFChars(jname, 0));

  jfieldID fdimType = env->GetFieldID(c, "dimType", "Lio/pdal/DimType;");
  jobject jdimType = env->GetObjectField(field, fdimType);

  jclass d = env->GetObjectClass(jdimType);
  jfieldID fid = env->GetFieldID(d, "id", "Ljava/lang/String;");
  jstring jid = reinterpret_cast<jstring>(env->GetObjectField(jdimType, fid));
  std::string sid = std::string(env->GetStringUTFChars(jid, 0));

  jfieldID ftype = env->GetFieldID(d, "type", "Ljava/lang/String;");
  jstring jtype = reinterpret_cast<jstring>(env->GetObjectField(jdimType, ftype));
  std::string stype = std::string(env->GetStringUTFChars(jtype, 0));

  jfieldID foffset = env->GetFieldID(c, "offset", "J");
  jlong joffset = env->GetLongField(field, foffset);

  MemoryViewReader::Field pfield = { sname, pdal::Dimension::type(stype), (size_t) joffset };

  MemoryViewReader *r = getHandle<MemoryViewReader>(env, obj);

  r->pushField(pfield);
}

/*
 * Class:     io_pdal_MemoryViewReader
 * Method:    setIncrementer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_MemoryViewReader_setIncrementer
  (JNIEnv *env, jobject obj, jlong id)
{
  MemoryViewReader *r = getHandle<MemoryViewReader>(env, obj);
  // table to push points? 
  // r->setIncrementer(...);
}

/*
 * Class:     io_pdal_MemoryViewReader
 * Method:    dispose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_MemoryViewReader_dispose
  (JNIEnv *env, jobject obj)
{
    MemoryViewReader *p = getHandle<MemoryViewReader>(env, obj);
    setHandle<int>(env, obj, 0);
    delete p;
}

