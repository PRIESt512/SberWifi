//
// Created by pleshkanev.a.a on 2019-10-02.
//

#ifndef SBERWIFI_IPERFJAVA_H
#define SBERWIFI_IPERFJAVA_H

#include <jni.h>
#include "iperf/iperf_api.h"
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_start(JNIEnv *env, jobject thisObject, jstring host,
                                                  jint port);

JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_initTempPath(JNIEnv *env, jobject thisObject);
#ifdef __cplusplus
}
#endif

#endif //SBERWIFI_IPERFJAVA_H
