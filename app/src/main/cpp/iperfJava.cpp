//
// Created by pleshkanev.a.a on 2019-10-02.
//

#include "iperfJava.h"
#include "IperfTest.h"
#include <android/log.h>

#define APPNAME "SberWiFi"

extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_initTempPath(JNIEnv *env, jobject thisObject) {
    jclass cls2 = env->GetObjectClass(thisObject);

    if (cls2 == nullptr) {
        __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: class not found !");
    } else {
        jmethodID initContext = env->GetMethodID(cls2, "getTempPath", "()Ljava/lang/String;");
        if (initContext != nullptr) {
            auto path = env->CallObjectMethod(thisObject, initContext);
            tempPath = const_cast<char *>(env->GetStringUTFChars(static_cast<jstring>(path),
                                                                 nullptr));

            __android_log_print(ANDROID_LOG_INFO, APPNAME, "Nice!");
        } else {
            __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: getTempPath not found !");
        }
    }
}


extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_start(JNIEnv *env, jobject thisObject, jstring host,
                                                  jint port) {

    const char *hostPointer = env->GetStringUTFChars(host, nullptr);
    __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Start host %s", hostPointer);

    try {
        IperfTest test = IperfTest("10.18.32", 8080, VerboseEnum::YES, Role::CLIENT);
        test.set_duration(20);
        test.set_num_streams(2);
        test.set_reporter_interval(1);
        test.run();

        test.print_statistics([](std::string info) {
            __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Info stat %s", info.c_str());
        });
    } catch (const std::exception &e) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Error %s", e.what());
    }
}