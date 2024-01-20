/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_pdal_Pipeline */

#ifndef _Included_io_pdal_Pipeline
#define _Included_io_pdal_Pipeline
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:      io_pdal_Pipeline
 * Method:     initialize
 * Signature:  ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_Pipeline_initialize
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     execute
 * Signature:  ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_Pipeline_execute
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getPointViews
 * Signature:  ()Lio/pdal/PointViewIterator;
 */
JNIEXPORT jobject JNICALL Java_io_pdal_Pipeline_getPointViews
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     close
 * Signature:  ()V
 */
JNIEXPORT void JNICALL Java_io_pdal_Pipeline_close
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getSrsWKT2
 * Signature:  ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getSrsWKT2
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getPipeline
 * Signature:  ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getPipeline
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getMetadata
 * Signature:  ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getMetadata
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getSchema
 * Signature:  ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getSchema
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getQuickInfo
 * Signature:  ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_pdal_Pipeline_getQuickInfo
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     validate
 * Signature:  ()Z
 */
JNIEXPORT jboolean JNICALL Java_io_pdal_Pipeline_validate
  (JNIEnv *, jobject);

/*
 * Class:      io_pdal_Pipeline
 * Method:     setLogLevel
 * Signature:  (I)V
 */
JNIEXPORT void JNICALL Java_io_pdal_Pipeline_setLogLevel
  (JNIEnv *, jobject, jint);

/*
 * Class:      io_pdal_Pipeline
 * Method:     getLogLevel
 * Signature:  ()I
 */
JNIEXPORT jint JNICALL Java_io_pdal_Pipeline_getLogLevel
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
