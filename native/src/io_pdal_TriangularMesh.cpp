/******************************************************************************
* Copyright (c) 2020, hobu Inc.  (info@hobu.co)
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
#include "io_pdal_TriangularMesh.h"
#include "JavaPipeline.hpp"
#include "JavaTriangularMeshIterator.hpp"
#include "PointViewRawPtr.hpp"
#include "Accessors.hpp"

using libpdaljava::TriangularMeshIterator;
using pdal::Triangle;
using pdal::PointId;

JNIEXPORT jint JNICALL Java_io_pdal_TriangularMesh_size
  (JNIEnv *env, jobject obj)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);

    return it->size();
}

JNIEXPORT jobject JNICALL Java_io_pdal_TriangularMesh_get
  (JNIEnv *env, jobject obj, jlong jidx)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);
    Triangle triangle = it->get(static_cast<PointId>(jidx));

    jclass jtClass = env->FindClass("io/pdal/Triangle");
    jmethodID jtCtor = env->GetMethodID(jtClass, "<init>", "(III)V");
    jobject jt = env->NewObject(jtClass, jtCtor, triangle.m_a, triangle.m_b, triangle.m_c);
    
    return jt;
}

JNIEXPORT jboolean JNICALL Java_io_pdal_TriangularMesh_hasNext
  (JNIEnv *env, jobject obj)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);

    return it->hasNext();
}

JNIEXPORT jobject JNICALL Java_io_pdal_TriangularMesh_next
  (JNIEnv *env, jobject obj)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);

    Triangle triangle = it->next();

    jclass jtClass = env->FindClass("io/pdal/Triangle");
    jmethodID jtCtor = env->GetMethodID(jtClass, "<init>", "(III)V");
    jobject jt = env->NewObject(jtClass, jtCtor, triangle.m_a, triangle.m_b, triangle.m_c);

    return jt;
}

/*
 * Class:     io_pdal_TriangularMesh
 * Method:    asArray
 * Signature: ()[Lio/pdal/Triangle;
 */
JNIEXPORT jobjectArray JNICALL Java_io_pdal_TriangularMesh_asArray
  (JNIEnv *env, jobject obj)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);

    jclass jtClass = env->FindClass("io/pdal/Triangle");
    jmethodID jtCtor = env->GetMethodID(jtClass, "<init>", "(III)V");

    int size = it->size();
    jobjectArray result = env->NewObjectArray(size, jtClass, NULL);

    for (int i = 0; i < size; i++)
    {
        Triangle triangle = it->get(static_cast<PointId>(i));
        jobject element = env->NewObject(jtClass, jtCtor, triangle.m_a, triangle.m_b, triangle.m_c);

        env->SetObjectArrayElement(result, i, element);

        env->DeleteLocalRef(element);
    }

    return result;
}


JNIEXPORT void JNICALL Java_io_pdal_TriangularMesh_close
  (JNIEnv *env, jobject obj)
{
    TriangularMeshIterator *it = getHandle<TriangularMeshIterator>(env, obj);
    setHandle<int>(env, obj, 0);
    delete it;
}
