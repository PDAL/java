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
#include <iostream>
#include <string>
#include <mutex>

#include "io_pdal_Pipeline.h"
#include "JavaPipeline.hpp"
#include "JavaSetIterator.hpp"
#include "JavaExceptions.hpp"
#include "Accessors.hpp"

using libpdaljava::PipelineExecutor;
using libpdaljava::PointViewIterator;

using pdal::PointViewSet;
using pdal::PointView;
using pdal::PointViewLess;
using pdal::PointViewPtr;
using pdal::pdal_error;

std::mutex execute_mutex;

JNIEXPORT void JNICALL Java_io_pdal_Pipeline_initialize
  (JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    jfieldID fidJson = env->GetFieldID(c, "json", "Ljava/lang/String;");
    jfieldID fidLogLevel = env->GetFieldID(c, "logLevel", "I");
    jstring json = reinterpret_cast<jstring>(env->GetObjectField(obj, fidJson));
    jint logLevel = env->GetIntField(obj, fidLogLevel);

    if(json == NULL)
    {
        throwInitializationException(env, "Null string passed into the Pipeline constructor.");
    }
    else
    {
        try
        {
            setHandle(env, obj, new PipelineExecutor(std::string(env->GetStringUTFChars(json, 0)), logLevel));
        }
        catch (const pdal_error &pe)
        {
            throwInitializationException(env, pe.what());
        }
    }
}

JNIEXPORT void JNICALL Java_io_pdal_Pipeline_close
  (JNIEnv *env, jobject obj)
{
    PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
    setHandle<int>(env, obj, 0);
    delete p;
}

JNIEXPORT void JNICALL Java_io_pdal_Pipeline_execute
  (JNIEnv *env, jobject obj)
{
    PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
    try
    {
        std::lock_guard<std::mutex> lock (execute_mutex);
        p->execute();
    }
    catch(const pdal_error &pe)
    {
        throwExecutionException(env, pe.what());
    }
}

JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getSrsWKT2
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        return env->NewStringUTF(p->getSrsWKT2().c_str());
    }
    catch(const pdal_error &pe)
    {
        return throwExecutionException(env, pe.what());
    }
}

JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getPipeline
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        return env->NewStringUTF(p->getPipeline().c_str());
    }
    catch(const pdal_error &pe)
    {
        return throwExecutionException(env, pe.what());
    }
}


JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getMetadata
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        return env->NewStringUTF(p->getMetadata().c_str());
    }
    catch(const pdal_error &pe)
    {
        return throwExecutionException(env, pe.what());
    }
}

JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getSchema
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        return env->NewStringUTF(p->getSchema().c_str());
    }
    catch(const pdal_error &pe)
    {
        return throwExecutionException(env, pe.what());
    }
}

JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getQuickInfo
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        return env->NewStringUTF(p->getQuickInfo().c_str());
    }
    catch(const pdal_error &pe)
    {
        return throwExecutionException(env, pe.what());
    }
}

JNIEXPORT jint JNICALL Java_io_pdal_Pipeline_getLogLevelInt
  (JNIEnv *env, jobject obj)
{
    PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
    return p->getLogLevel();
}

JNIEXPORT jobject JNICALL Java_io_pdal_Pipeline_getPointViews
  (JNIEnv *env, jobject obj)
{
    try
    {
        PipelineExecutor *p = getHandle<PipelineExecutor>(env, obj);
        PointViewSet pvset = p->views();

        jclass pviClass = env->FindClass("io/pdal/PointViewIterator");
        jmethodID pviCtor = env->GetMethodID(pviClass, "<init>", "()V");
        jobject pvi = env->NewObject(pviClass, pviCtor);

        PointViewIterator *it = new PointViewIterator(pvset);

        setHandle(env, pvi, it);

        return pvi;
    }
    catch(const libpdaljava::java_error &je)
    {
        return throwExecutionException(env, je.what());
    }
}
