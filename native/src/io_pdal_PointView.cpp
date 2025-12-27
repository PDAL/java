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
#include <float.h>
#include "io_pdal_PointView.h"
#include "JavaPipeline.hpp"
#include "JavaTriangularMeshIterator.hpp"
#include "JavaExceptions.hpp"
#include "PointViewRawPtr.hpp"
#include "Accessors.hpp"

using libpdaljava::PipelineExecutor;
using libpdaljava::PointViewRawPtr;
using libpdaljava::TriangularMeshIterator;

using pdal::PointView;
using pdal::PointViewPtr;
using pdal::PointLayoutPtr;
using pdal::Dimension::Type;
using pdal::Dimension::Id;
using pdal::PointId;
using pdal::DimTypeList;
using pdal::SpatialReference;
using pdal::TriangularMesh;
using pdal::Triangle;
using pdal::DimType;
using pdal::pdal_error;

using std::size_t;
using std::string;

/// Converts JavaArray of DimTypes (In Java interpretation DimType is a pair of strings)
/// into DimTypeList (vector of DimTypes), puts dim size into bufSize
/// \param[in] env       JNI environment
/// \param[in] dims      JavaArray of DimTypes
/// \param[in] bufSize   Dims sum size
/// \param[in] dimTypes  Vector of DimTypes
void convertDimTypeJavaArrayToVector(JNIEnv *env, jobjectArray dims, size_t *pointSize, DimTypeList *dimTypes, PointLayoutPtr pl) 
{
    for (jint i = 0; i < env->GetArrayLength(dims); i++) 
    {
        jobject jDimType = reinterpret_cast<jobject>(env->GetObjectArrayElement(dims, i));
        jclass cDimType = env->GetObjectClass(jDimType);
        jfieldID fid = env->GetFieldID(cDimType, "id", "Ljava/lang/String;");
        jfieldID ftype = env->GetFieldID(cDimType, "type", "Ljava/lang/String;");

        jstring jid = reinterpret_cast<jstring>(env->GetObjectField(jDimType, fid));
        DimType dimType = pl->findDimType(string(env->GetStringUTFChars(jid, 0)));

        *pointSize += pl->dimSize(dimType.m_id);
        dimTypes->insert(dimTypes->begin() + i, dimType);
    }
}

/// Fill a buffer with point data specified by the dimension list, accounts index
/// Using this functions it is possible to pack all points into one buffer
/// \param[in] pv    PointView pointer.
/// \param[in] dims  List of dimensions/types to retrieve.
/// \param[in] idx   Index of point to get.
/// \param[in] buf   Pointer to buffer to fill.
void appendPackedPoint(PointViewPtr pv, const DimTypeList& dims, PointId idx, size_t pointSize, char *buf)
{
    size_t from = idx * pointSize;
    if(from >= pv->size() * pointSize) return;
    buf += from;
    pv->getPackedPoint(dims, idx, buf);
}

JNIEXPORT jobject JNICALL Java_io_pdal_PointView_layout
  (JNIEnv *env, jobject obj)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    PointLayoutPtr pl = pv->layout();

    jclass pvlClass = env->FindClass("io/pdal/PointLayout");
    jmethodID pvlCtor = env->GetMethodID(pvlClass, "<init>", "()V");
    jobject pvl = env->NewObject(pvlClass, pvlCtor);

    setHandle(env, pvl, pl);

    return pvl;
}

JNIEXPORT jint JNICALL Java_io_pdal_PointView_size
  (JNIEnv *env, jobject obj)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    return pv->size();
}

JNIEXPORT jboolean JNICALL Java_io_pdal_PointView_empty
  (JNIEnv *env, jobject obj)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    return pv->empty();
}

JNIEXPORT jstring JNICALL Java_io_pdal_PointView_getCrsProj4
  (JNIEnv *env, jobject obj)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    return env->NewStringUTF(pv->spatialReference().getProj4().c_str());
}

JNIEXPORT jstring JNICALL Java_io_pdal_PointView_getCrsWKT__Z
  (JNIEnv *env, jobject obj, jboolean pretty)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;

    string wkt = pv->spatialReference().getWKT();

    if(pretty) wkt = SpatialReference::prettyWkt(wkt);

    return env->NewStringUTF(wkt.c_str());
}

JNIEXPORT jbyteArray JNICALL Java_io_pdal_PointView_getPackedPoint__J_3Lio_pdal_DimType_2
  (JNIEnv *env, jobject obj, jlong idx, jobjectArray dims)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;

    PointLayoutPtr pl = pv->layout();

    // we need to calculate buffer size
    size_t pointSize = 0;
    DimTypeList dimTypes;

    // calculate result buffer length (for one point) and get dimTypes
    convertDimTypeJavaArrayToVector(env, dims, &pointSize, &dimTypes, pl);

    char *buf = new char[pointSize];

    pv->getPackedPoint(dimTypes, idx, buf);

    jbyteArray array = env->NewByteArray(pointSize);
    env->SetByteArrayRegion (array, 0, pointSize, reinterpret_cast<jbyte *>(buf));

    delete[] buf;

    return array;
}

JNIEXPORT jbyteArray JNICALL Java_io_pdal_PointView_getPackedPoints___3Lio_pdal_DimType_2
  (JNIEnv *env, jobject obj, jobjectArray dims)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;

    PointLayoutPtr pl = pv->layout();

    // we need to calculate buffer size
    size_t pointSize = 0;
    DimTypeList dimTypes;

    // calculate result buffer length (for one point) and get dimTypes
    convertDimTypeJavaArrayToVector(env, dims, &pointSize, &dimTypes, pl);

    // reading all points
    size_t bufSize = pointSize * pv->size();
    char *buf = new char[bufSize];

    for (PointId idx = 0; idx < pv->size(); idx++) 
    {
        appendPackedPoint(pv, dimTypes, idx, pointSize, buf);
    }

    jbyteArray array = env->NewByteArray(bufSize);
    env->SetByteArrayRegion (array, 0, bufSize, reinterpret_cast<jbyte *>(buf));

    delete[] buf;

    return array;
}

JNIEXPORT jobject JNICALL Java_io_pdal_PointView_getTriangularMesh__Ljava_lang_String_2
  (JNIEnv *env, jobject obj, jstring name)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    string cname = string(env->GetStringUTFChars(name, 0));

    TriangularMesh *m = pv->mesh(cname);
    
    if(m == NULL)
    {
        return throwExecutionException(env, "No mesh was generated. Check that the appropriate filter is a part of a PDAL Pipeline.");
    }

    TriangularMeshIterator *it = new TriangularMeshIterator(m);
    jclass meshClass = env->FindClass("io/pdal/TriangularMesh");
    jmethodID meshCtor = env->GetMethodID(meshClass, "<init>", "()V");
    jobject mi = env->NewObject(meshClass, meshCtor);

    setHandle(env, mi, it);

    return mi;
}

// A rasterizer port from GeoTrellis, see:
// https://github.com/geotrellis/geotrellis-pointcloud/blob/837545f67818ce252e315b13a327a74821dc0594/pointcloud/src/main/scala/geotrellis/pointcloud/raster/rasterize/triangles/PDALTrianglesRasterizer.scala#L33-L153
JNIEXPORT jdoubleArray JNICALL Java_io_pdal_PointView_rasterizeTriangularMesh___3DIILio_pdal_DimType_2Ljava_lang_String_2
  (JNIEnv *env, jobject obj, jdoubleArray extent, jint cols, jint rows, jobject jDimType, jstring name)
{
    string cname = string(env->GetStringUTFChars(name, 0));

    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    PointViewPtr pv = pvrp->shared_pointer;
    TriangularMesh *mesh = pv->mesh(cname);

    if(mesh == NULL)
    {
        throwExecutionException(env, "No mesh was generated. Check that the appropriate filter is a part of a PDAL Pipeline.");
        return env->NewDoubleArray(0);
    }

    jclass cDimType = env->GetObjectClass(jDimType);
    jfieldID fid = env->GetFieldID(cDimType, "id", "Ljava/lang/String;");
    jstring jid = reinterpret_cast<jstring>(env->GetObjectField(jDimType, fid));
    
    PointLayoutPtr pl = pv->layout();
    DimType dimType = pl->findDimType(string(env->GetStringUTFChars(jid, 0)));
    Id dimId = dimType.m_id;

    int size = mesh->size();

    int length = cols * rows;

    jdouble *ext = env->GetDoubleArrayElements(extent, 0);
    
    double exmin = ext[0];
    double eymin = ext[1];
    double exmax = ext[2];
    double eymax = ext[3];

    env->ReleaseDoubleArrayElements(extent, ext, JNI_ABORT);

    double w = (exmax - exmin) / cols;
    double h = (eymax - eymin) / rows;

    jdoubleArray result = env->NewDoubleArray(length);

    double *buffer = new double[length];
    std::fill(buffer, buffer + length, strtod("NaN", NULL));

    for (int id = 0; id < size; id++)
    {
        const Triangle& tri = (*mesh)[id];
        PointId a = tri.m_a;
        PointId b = tri.m_b;
        PointId c = tri.m_c;

        double v1x = pv->getFieldAs<double>(Id::X, a);
        double v1y = pv->getFieldAs<double>(Id::Y, a);
        double v1z = pv->getFieldAs<double>(dimId, a);
        double s1x = pv->getFieldAs<double>(Id::X, c);
        double s1y = pv->getFieldAs<double>(Id::Y, c);

        double v2x = pv->getFieldAs<double>(Id::X, b);
        double v2y = pv->getFieldAs<double>(Id::Y, b);
        double v2z = pv->getFieldAs<double>(dimId, b);
        double s2x = pv->getFieldAs<double>(Id::X, a);
        double s2y = pv->getFieldAs<double>(Id::Y, a);

        double v3x = pv->getFieldAs<double>(Id::X, c);
        double v3y = pv->getFieldAs<double>(Id::Y, c);
        double v3z = pv->getFieldAs<double>(dimId, c);
        double s3x = pv->getFieldAs<double>(Id::X, b);
        double s3y = pv->getFieldAs<double>(Id::Y, b);

        double determinant = (v2y - v3y) * (v1x - v3x) + (v3x - v2x) * (v1y - v3y);

        double ymin = (std::min)(v1y, (std::min)(v2y, v3y));
        double ymax = (std::max)(v1y, (std::max)(v2y, v3y));

        double scanrow0 = (std::max)(std::ceil((ymin - eymin) / h - 0.5), 0.0);
        double scany = eymin + scanrow0 * h + h / 2.0;

        while (scany <= eymax && scany <= ymax) 
        {
            // get x at y for edge
            double xmin = -DBL_MAX;
            double xmax = DBL_MAX;

            if(s1y != v1y) 
            {
                double t = (scany - v1y) / (s1y - v1y);
                double xAtY1 = v1x + t * (s1x - v1x);

                if(v1y < s1y) 
                {
                    // Lefty
                    if(xmin < xAtY1) { xmin = xAtY1; }
                } 
                else 
                {
                    // Righty
                    if(xAtY1 < xmax) { xmax = xAtY1; }
                }
            }

            if(s2y != v2y) 
            {
                double t = (scany - v2y) / (s2y - v2y);
                double xAtY2 = v2x + t * (s2x - v2x);

                if(v2y < s2y) 
                {
                    // Lefty
                    if(xmin < xAtY2) { xmin = xAtY2; }
                } 
                else 
                {
                    // Righty
                    if(xAtY2 < xmax) { xmax = xAtY2; }
                }
            }

            if(s3y != v3y) 
            {
                double t = (scany - v3y) / (s3y - v3y);
                double xAtY3 = v3x + t * (s3x - v3x);

                if(v3y < s3y) 
                {
                    // Lefty
                    if(xmin < xAtY3) { xmin = xAtY3; }
                } 
                else 
                {
                    // Righty
                    if(xAtY3 < xmax) { xmax = xAtY3; }
                }
            }

            double scancol0 = (std::max)(std::ceil((xmin - exmin) / w - 0.5), 0.0);
            double scanx = exmin + scancol0 * w + w / 2;

            while (scanx <= exmax && scanx <= xmax) 
            {
                int col = (int)((scanx - exmin) / w);
                int row = (int)((eymax - scany) / h);

                if(0 <= col && col < cols && 0 <= row && row < rows)
                {

                    double lambda1 = ((v2y - v3y) * (scanx - v3x) + (v3x - v2x) * (scany - v3y)) / determinant;
                    double lambda2 = ((v3y - v1y) * (scanx - v3x) + (v1x - v3x) * (scany - v3y)) / determinant;
                    double lambda3 = 1.0 - lambda1 - lambda2;

                    double z = lambda1 * v1z + lambda2 * v2z + lambda3 * v3z;

                    buffer[row * cols + col] = z;
                }

                scanx += w;
            }

            scany += h;
        }
    }

    env->SetDoubleArrayRegion(result, 0, length, buffer);

    delete[] buffer;

    return result;
}

JNIEXPORT void JNICALL Java_io_pdal_PointView_close
  (JNIEnv *env, jobject obj)
{
    PointViewRawPtr *pvrp = getHandle<PointViewRawPtr>(env, obj);
    setHandle<int>(env, obj, 0);
    delete pvrp;
}
